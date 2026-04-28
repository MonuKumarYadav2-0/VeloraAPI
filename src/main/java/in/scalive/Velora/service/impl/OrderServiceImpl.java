package in.scalive.Velora.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.razorpay.RazorpayClient;

import in.scalive.Velora.dto.request.PlaceOrderRequestDTO;
import in.scalive.Velora.dto.request.UpdateOrderStatusRequestDTO;
import in.scalive.Velora.dto.response.OrderItemResponseDTO;
import in.scalive.Velora.dto.response.OrderResponseDTO;
import in.scalive.Velora.dto.response.PageResponseDTO;
import in.scalive.Velora.entity.Cart;
import in.scalive.Velora.entity.CartItem;
import in.scalive.Velora.entity.Order;
import in.scalive.Velora.entity.OrderItem;
import in.scalive.Velora.entity.Product;
import in.scalive.Velora.entity.User;
import in.scalive.Velora.exception.EmptyCartException;
import in.scalive.Velora.exception.InsufficientStockException;
import in.scalive.Velora.exception.InvalidOperationException;
import in.scalive.Velora.exception.ResourceNotFoundException;
import in.scalive.Velora.repository.CartRepository;
import in.scalive.Velora.repository.OrderItemRepository;
import in.scalive.Velora.repository.OrderRepository;
import in.scalive.Velora.repository.ProductRepository;
import in.scalive.Velora.repository.UserRepository;
import in.scalive.Velora.service.OrderService;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService{
        private final OrderRepository orderRepo;
        private final OrderItemRepository orderItemRepo;
        private final CartRepository cartRepo;
        private final UserRepository userRepo;
        private final ProductRepository productRepo;
        
        @Value("${razorpay.key}")
        private String key;

        @Value("${razorpay.secret}")
        private String secret;
        
        //helper
        private Order findOrderById(Long orderId) {
        	Optional<Order> opt=orderRepo.findById(orderId);
        	    if(opt.isPresent()) {
        	    	    return opt.get();
        	    }
        	    throw new ResourceNotFoundException("order", "orderId", orderId);
        }
        
        //helper
        private void validateStatusTransition(String currentStatus,String newStatus) {
        	      if(currentStatus.equals(Order.STATUS_CONFIRMED)) {
        	    	     if(!newStatus.equals(Order.STATUS_CANCELLED)) {
        	    	    	       throw new InvalidOperationException("Cannot transition from CONFIRMED to "+newStatus);
        	    	     }
        	      }
        	      else if(currentStatus.equals(Order.STATUS_CANCELLED)) {
   	    	         throw new InvalidOperationException("Cannot change status of "+currentStatus+ " order");
        	      }
        }
        
        //helper in case of cancelling the order
        private void restoreStock(Order order) {
        	  for(OrderItem orderItem: order.getOrderItems()) {
        		  productRepo.increaseStock(orderItem.getProduct().getId(), orderItem.getQuantity());
        	  }
        }
        
        //helper
        private OrderResponseDTO mapToOrderResponseDTO(Order order) {
        	     List<OrderItemResponseDTO> items=new ArrayList<>();
        	     int totalItems=0;
        	     for(OrderItem orderItem: order.getOrderItems()) {
        	    	     items.add(mapToOrderItemResponseDTO(orderItem));
        	    	     totalItems=totalItems+orderItem.getQuantity();
        	     }
        	     return OrderResponseDTO.builder()
        	    		 .id(order.getId())
        	    		 .userId(order.getUser().getId())
        	    		 .orderItems(items)
        	    		 .status(order.getStatus())
        	    		 .orderNumber(order.getOrderNumber())
        	    		 .userName(order.getUser().getFullName())
        	    		 .userEmail(order.getUser().getEmail())
        	    		 .totalAmount(order.getTotalAmount())
        	    		 .totalItems(totalItems)
        	    		 .notes(order.getNotes())
        	    		 .orderDate(order.getOrderDate())
        	    		 .razorpayOrderId(order.getRazorpayOrderId())
        	    		 .build();
        }

        //helper
		private OrderItemResponseDTO mapToOrderItemResponseDTO(OrderItem orderItem) {		
			return OrderItemResponseDTO.builder()
					.id(orderItem.getId())
					.productId(orderItem.getProduct().getId())
					.productName(orderItem.getProductName())
					.productSku(orderItem.getProductSku())
					.productImage(orderItem.getProduct().getImageUrl())
					.quantity(orderItem.getQuantity())
					.unitPrice(orderItem.getUnitPrice())
					.subTotal(orderItem.getSubTotal())
					.build();
		}
		
		//helper
		private PageResponseDTO<OrderResponseDTO> mapToPageResponseDTO(Page<Order> orderPage){
			  List<OrderResponseDTO> orders=new ArrayList<>();
			  for(Order order:orderPage.getContent()) {
				  orders.add(mapToOrderResponseDTO(order));
			  }
			  return PageResponseDTO.<OrderResponseDTO>builder()
					  .content(orders)
					  .pageNumber(orderPage.getNumber())
					  .pageSize(orderPage.getSize())
					  .totalElements(orderPage.getTotalElements())
					  .totalPages(orderPage.getTotalPages())
					  .first(orderPage.isFirst())
					  .last(orderPage.isLast())
					  .hasNext(orderPage.hasNext())
					  .hasPrevious(orderPage.hasPrevious())
					  .build();
		}

		@Override
		public OrderResponseDTO placeOrder(PlaceOrderRequestDTO placeOrderRequestDTO) {
			Long userId=placeOrderRequestDTO.getUserId();
			Optional<User>userOpt=userRepo.findById(userId);
			if(userOpt.isEmpty()) {
				throw new ResourceNotFoundException("User","userId", userId);
			}
		    User user=userOpt.get();
		    //fetch cart with items
		    Optional<Cart>cartOpt=cartRepo.findByUserIdWithItems(userId);
		    if(cartOpt.isEmpty()) {
		    	throw new ResourceNotFoundException("Cart","userId", userId);
		    }
		    Cart cart=cartOpt.get();
		    
		    //validate cart is not empty
		    if(cart.getCartItems().isEmpty()) {
		    	   throw new EmptyCartException("Cannot place order with an empty cart");
		    }
		    
		    List<CartItem>cartItems=cart.getCartItems();
		    double totalAmount=0.0;
		    //validate stock quantity for all items
		    for(CartItem cartItem : cartItems) {
		    	   Product product=cartItem.getProduct();
		    	   if(!product.getIsAvailable()) {
		    		   throw new InsufficientStockException(product.getName()+" is no longer available");
		    	   }
		    	   if(product.getStockQuantity()<cartItem.getQuantity()) {
		    		   throw new InsufficientStockException("Insufficient stock for "+product.getName()+
		    				                                ". Available: "+product.getStockQuantity()+
		    				                                ", Requested: "+cartItem.getQuantity());
		    	   }
		    	   totalAmount+=cartItem.getSubTotal();
		    }
		    //create order
		    Order order=Order.builder()
		    		        .user(user)
		    		        .totalAmount(totalAmount)
		    		        .notes(placeOrderRequestDTO.getNotes())
		    		        .status(Order.STATUS_PENDING)
		    		        .build();
		    try {
	             RazorpayClient client = new RazorpayClient(key, secret);

	             JSONObject options = new JSONObject();
	             options.put("amount", (int)(order.getTotalAmount() * 100));
	             options.put("currency", "INR");

	             com.razorpay.Order razorpayOrder = client.orders.create(options);

	             order.setRazorpayOrderId(razorpayOrder.get("id"));

	         } catch (Exception e) {
	             throw new RuntimeException("Error while creating Razorpay order", e);
	         }
		    Order savedOrder=orderRepo.save(order);
		    
		    //create orderItems from cartItems
		    List<OrderItem>orderItems=new ArrayList<>();
		    for(CartItem cartItem:cartItems) {
		    	    OrderItem orderItem=OrderItem.fromCartItem(cartItem);
		    	    savedOrder.addOrderItem(orderItem);
		    	    orderItems.add(orderItem);
		    	    
		    	    //decrease product stock
		    	    productRepo.decreaseStock(cartItem.getProduct().getId(), cartItem.getQuantity());
		    }
		    orderItemRepo.saveAll(orderItems);
		    
		    //clear the cart after order is placed
		    cart.clearCart();
		    cartRepo.save(cart);
			return mapToOrderResponseDTO(savedOrder);
		}

		@Override
		public OrderResponseDTO getOrderById(Long orderId) {
			Order order=findOrderById(orderId);
			return mapToOrderResponseDTO(order);
		}

		@Override
		public OrderResponseDTO getOrderByOrderNumber(String orderNumber) {
			Optional<Order> opt=orderRepo.findByOrderNumber(orderNumber);
			if(!opt.isPresent()) {
				throw new ResourceNotFoundException("Order","orderNumber",orderNumber);
			}
		    Order order=opt.get();
		    return mapToOrderResponseDTO(order);
		}

		@Override
		public List<OrderResponseDTO> getOrdersByUserId(Long userId) {
			if(!userRepo.existsById(userId)) {
				throw new ResourceNotFoundException("User", "userId", userId);
			}
			List<Order>orders= orderRepo.findByUserIdOrderByOrderDateDesc(userId);
			List<OrderResponseDTO>responseList=new ArrayList<>();
			for(Order order:orders) {
				responseList.add(mapToOrderResponseDTO(order));
			}
			return responseList;
		}

		@Override
		public PageResponseDTO<OrderResponseDTO> getAllOrdersPaginated(int page, int size, String sortBy,
				String sortDir) {
			Sort sort;
			if(sortDir.equalsIgnoreCase("desc")) {
			    sort=Sort.by(sortBy).descending();
			}
			else {
				sort=Sort.by(sortBy).ascending();
			}
			Pageable  pageable=PageRequest.of(page, size,sort);
			Page<Order>orderPage=orderRepo.findAll(pageable);
			return mapToPageResponseDTO(orderPage);
		}

		@Override
		public List<OrderResponseDTO> getOrdersByStatus(String status) {
			List<Order>orders= orderRepo.findByStatus(status);
			List<OrderResponseDTO>responseList=new ArrayList<>();
			for(Order order:orders) {
				responseList.add(mapToOrderResponseDTO(order));
			}
			return responseList;
		}

		@Override
		public OrderResponseDTO updateOrderStatus(Long orderId,
				UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
			Order order=findOrderById(orderId);
			String newStatus=updateOrderStatusRequestDTO.getStatus();
			String currentStatus=order.getStatus();
			validateStatusTransition(currentStatus, newStatus);
			order.setStatus(newStatus);
			if(newStatus.equals(Order.STATUS_CANCELLED)) {
				restoreStock(order);
			}
			String newNotes=updateOrderStatusRequestDTO.getNotes();
			if(newNotes!=null && !newNotes.isBlank()) {
				StringBuilder sb=new StringBuilder();
				if(order.getNotes()!=null) {
					sb.append(order.getNotes()).append("\n");
				}
				sb.append("["+LocalDateTime.now()+"] "+newNotes);
				order.setNotes(sb.toString());
			}
			Order updatedOrder=orderRepo.save(order);
			return mapToOrderResponseDTO(updatedOrder);
		}

		@Override
		public OrderResponseDTO cancelOrder(Long orderId, String reason) {
			Order order=findOrderById(orderId);
			if(Order.STATUS_CANCELLED.equals(order.getStatus())) {
				throw new InvalidOperationException("Cannot cancel order with status: "+order.getStatus());
			}
			    order.setStatus(Order.STATUS_CANCELLED);	
				StringBuilder sb=new StringBuilder();
				if(order.getNotes()!=null) {
					sb.append(order.getNotes()).append("\n");
				}
				sb.append("["+LocalDateTime.now()+"] Cancelled: "+reason);
				order.setNotes(sb.toString());
				restoreStock(order);
			    Order cancelledOrder=orderRepo.save(order);
			return mapToOrderResponseDTO(cancelledOrder);
		}

		@Override
		public PageResponseDTO<OrderResponseDTO> searchOrders(String keyword, int page, int size) {
			Pageable pageable=PageRequest.of(page, size);
			Page<Order>orderPage=orderRepo.searchByOrders(keyword, pageable);
			return mapToPageResponseDTO(orderPage);
		}
}
