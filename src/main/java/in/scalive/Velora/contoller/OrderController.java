package in.scalive.Velora.contoller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.request.PlaceOrderRequestDTO;
import in.scalive.Velora.dto.request.UpdateOrderStatusRequestDTO;
import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.OrderResponseDTO;
import in.scalive.Velora.dto.response.PageResponseDTO;
import in.scalive.Velora.service.OrderService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {
   private OrderService serv;
   @Autowired
   public OrderController(OrderService serv) {
	this.serv = serv;
   }
   
   @PreAuthorize("hasRole('USER')")
   @PostMapping
   public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> placeOrder(@Valid @RequestBody PlaceOrderRequestDTO placeOrderRequestDTO){
	   OrderResponseDTO order=serv.placeOrder(placeOrderRequestDTO);
	   return new ResponseEntity<>(ApiResponseDTO.success("order placed successfully",order),HttpStatus.CREATED);
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/{orderId}")
   public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderById(@PathVariable Long orderId){
	   OrderResponseDTO order=serv.getOrderById(orderId);
	   return ResponseEntity.ok(ApiResponseDTO.success(order));
   }

   @PreAuthorize("hasRole('USER')")
   @GetMapping("/orderNumber/{orderNumber}")
   public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> getOrderByOrderNumber(@PathVariable String orderNumber){
	   OrderResponseDTO order=serv.getOrderByOrderNumber(orderNumber);
	   return ResponseEntity.ok(ApiResponseDTO.success(order));
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/user/{userId}")
   public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByUserId(@PathVariable Long userId){
	   List<OrderResponseDTO> orders=serv.getOrdersByUserId(userId);
	   return ResponseEntity.ok(ApiResponseDTO.success("Fetched "+orders.size()+" orders",orders));
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> getAllOrdersPaginated(
		   @RequestParam(defaultValue = "0") int page,
		   @RequestParam(defaultValue = "10") int size,
		   @RequestParam(defaultValue = "orderDate") String sortBy,
		   @RequestParam(defaultValue = "desc") String sortDir){
	   PageResponseDTO<OrderResponseDTO> orders=serv.getAllOrdersPaginated(page, size, sortBy, sortDir);
	   return ResponseEntity.ok(ApiResponseDTO.success(orders));
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/status/{status}")
   public ResponseEntity<ApiResponseDTO<List<OrderResponseDTO>>> getOrdersByStatus(@PathVariable String status){
	   List<OrderResponseDTO> orders=serv.getOrdersByStatus(status); 
	   return ResponseEntity.ok(ApiResponseDTO.success(orders));
   }
   
   @PreAuthorize("hasRole('USER')")
   @PatchMapping("/{orderId}/status")
   public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> updateOrderStatus(@PathVariable Long orderId ,@Valid @RequestBody UpdateOrderStatusRequestDTO dto){
	   OrderResponseDTO order=serv.updateOrderStatus(orderId, dto);
	   return ResponseEntity.ok(ApiResponseDTO.success("order status updated successfully",order));
   }
   
   @PreAuthorize("hasRole('USER')")
   @PostMapping("/{orderId}/cancel")
   public ResponseEntity<ApiResponseDTO<OrderResponseDTO>> cancelOrder(@PathVariable Long orderId,@RequestParam(required = false,defaultValue = "Customer requested cancellation")String reason){
	   OrderResponseDTO order=serv.cancelOrder(orderId, reason);
	   return ResponseEntity.ok(ApiResponseDTO.success("order cancelled successfully",order));
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/search")
   public ResponseEntity<ApiResponseDTO<PageResponseDTO<OrderResponseDTO>>> searchOrders(
		   @RequestParam String keyword,
		   @RequestParam(defaultValue = "0") int page,
		   @RequestParam(defaultValue = "10") int size
		  ){
	   PageResponseDTO<OrderResponseDTO> orders=serv.searchOrders(keyword, page, size);
	   return ResponseEntity.ok(ApiResponseDTO.success(orders));
   }
   

}