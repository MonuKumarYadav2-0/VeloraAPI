package in.scalive.Velora.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import in.scalive.Velora.dto.request.AddToCartRequestDTO;
import in.scalive.Velora.dto.request.UpdateCartItemRequestDTO;
import in.scalive.Velora.dto.response.CartItemResponseDTO;
import in.scalive.Velora.dto.response.CartResponseDTO;
import in.scalive.Velora.entity.Cart;
import in.scalive.Velora.entity.CartItem;
import in.scalive.Velora.entity.Product;
import in.scalive.Velora.exception.InsufficientStockException;
import in.scalive.Velora.exception.InvalidOperationException;
import in.scalive.Velora.exception.ResourceNotFoundException;
import in.scalive.Velora.repository.CartItemRepository;
import in.scalive.Velora.repository.CartRepository;
import in.scalive.Velora.repository.ProductRepository;
import in.scalive.Velora.service.CartService;

@Service
@Transactional
public class CartServiceImpl implements CartService{
  private CartRepository cartRepo;
  private CartItemRepository cartItemRepo;
  private ProductRepository productRepo;
  @Autowired
  public CartServiceImpl(CartRepository cartRepo, CartItemRepository cartItemRepo, ProductRepository productRepo) {
	this.cartRepo = cartRepo;
	this.cartItemRepo = cartItemRepo;
	this.productRepo=productRepo;
  }
  
  private Cart getCart(String email) {
	  Optional<Cart> opt=cartRepo.findByUserEmail(email);
	  if(!opt.isPresent()) {
		  throw new ResourceNotFoundException("Cart","userId", email);
	  }
	  return opt.get();
  }
  
  private Cart getCart(Long userId) {
	  Optional<Cart> opt=cartRepo.findByUserId(userId);
	  if(!opt.isPresent()) {
		  throw new ResourceNotFoundException("Cart","userId", userId);
	  }
	  return opt.get();
  }
  
  private Product findProductById(Long productId) {
	  Optional<Product> opt=productRepo.findById(productId);
	  if(opt.isEmpty()) {
		  throw new ResourceNotFoundException("Product","productId", productId);
	  }
	  return opt.get();
  }
  
  private CartItem findCartItemById(Long cartItemId) {
	  Optional<CartItem> opt=cartItemRepo.findById(cartItemId);
	  if(opt.isEmpty()) {
		  throw new ResourceNotFoundException("CartItem","cartItemId", cartItemId);
	  }
	  return opt.get();
  }
  
  private void validateProductAvailability(Product product,int requestedQuantity) {
	  if(!product.getIsAvailable()) {
		  throw new InsufficientStockException(product.getName()+" is not available");
	  }
	  if(product.getStockQuantity()<requestedQuantity) {
		  throw new InsufficientStockException("Insufficient stock for "+product.getName()+
				  ".Available: "+product.getStockQuantity()+", Requested: "+requestedQuantity);
	  }
  }
  
  private CartItemResponseDTO maptToCartItemResponseDTO(CartItem cartItem) {
	  Product product=cartItem.getProduct();
	  return CartItemResponseDTO.builder()
			  .id(cartItem.getId())
			  .productId(product.getId())
			  .productName(product.getName())
			  .productImage(product.getImageUrl())
			  .productSku(product.getSku())
			  .unitPrice(product.getPrice())
			  .quantity(cartItem.getQuantity())
			  .subTotal(cartItem.getSubTotal())
			  .availableStock(product.getStockQuantity())
			  .addedAt(cartItem.getAddedAt())
			  .build();
  }
  
  private CartResponseDTO maptToCartResponseDTO(Cart cart) {
	  List<CartItemResponseDTO>items=new ArrayList<>();
	  for(CartItem item:cart.getCartItems()) {
		  items.add(maptToCartItemResponseDTO(item));
	  }
	  return CartResponseDTO.builder()
			  .id(cart.getId())
			  .userId(cart.getUser().getId())
			  .userName(cart.getUser().getFullName())
			  .items(items)
			  .totalItems(cart.getTotalItems())
			  .totalAmount(cart.getTotalAmount())
			  .createdAt(cart.getCreatedAt())
			  .updatedAt(cart.getUpdatedAt())
			  .build();
  }

  @Override
  public CartResponseDTO getCartByUserId(Long userId) {
	 Cart cart=getCart(userId);
	return maptToCartResponseDTO(cart);
  }

  @Override
  public CartResponseDTO addItemToCart(String email, AddToCartRequestDTO dto) {
	Cart cart=getCart(email);
	Product product=findProductById(dto.getProductId());
	validateProductAvailability(product, dto.getQuantity());
	Optional<CartItem>opt=cartItemRepo.findByCartIdAndProductId(cart.getId(), product.getId());
	if(opt.isPresent()) {
		CartItem cartItem=opt.get();
		int newQty=cartItem.getQuantity()+dto.getQuantity();
		validateProductAvailability(product,newQty);
		cartItem.setQuantity(newQty);
		cartItem.calculateSubTotal();
		//cartItemRepo.save(cartItem); not necessary to call it because hibernate is doing dirty tracking of cartItem
	}
	else {
	CartItem cartItem=CartItem.builder()
			          .product(product)
			          .quantity(dto.getQuantity())
			          .unitPrice(product.getPrice())
			          .build();
	cartItem.calculateSubTotal();
	cart.addCartItem(cartItem);
	
	}
	cart.recalulateTotals();
    cartRepo.save(cart);
	return maptToCartResponseDTO(cart);
  }

  @Override
  public CartResponseDTO updateCartItem(Long userId, Long cartItemId, UpdateCartItemRequestDTO dto) {
	  Cart cart=getCart(userId);
	  CartItem cartItem=findCartItemById(cartItemId);
	  if(cart.getId()!=cartItem.getCart().getId()) {
		  throw new ResourceNotFoundException("cartItem","id",cartItemId);
	  }
	  validateProductAvailability(cartItem.getProduct(), dto.getQuantity());
	  cartItem.setQuantity(dto.getQuantity());
	  cartItem.calculateSubTotal();
	  cart.recalulateTotals();
	  cartRepo.save(cart);
	  return maptToCartResponseDTO(cart); 
  }

  @Override
  public CartResponseDTO removeCartItem(Long userId, Long cartItemId) {
	  Cart cart=getCart(userId);
	  CartItem cartItem=findCartItemById(cartItemId);
	  if(cart.getId()!=cartItem.getCart().getId()) {
		  throw new ResourceNotFoundException("cartItem","id",cartItemId);
	  }
      cart.removeCartItem(cartItem);
      cart.recalulateTotals();
      cartRepo.save(cart);
	return maptToCartResponseDTO(cart);
  }

  @Override
  public CartResponseDTO clearCart(Long userId) {
	  Cart cart=getCart(userId);
	  cart.clearCart();
	  cartRepo.save(cart);//ise bhi call karne ki jarurat nhi hai kyuki hibernate isko bhi track kar rha hai
	return maptToCartResponseDTO(cart);
  }

  @Override
  public CartItemResponseDTO getCartItem(Long userId, Long cartItemId) {
	  Cart cart=getCart(userId);
	  CartItem cartItem=findCartItemById(cartItemId);
	  if(cart.getId()!=cartItem.getCart().getId()) {
		  throw new ResourceNotFoundException("cartItem","id",cartItemId);
	  }
	  
	return maptToCartItemResponseDTO(cartItem);
  }

  @Override
  public boolean isProductInCart(Long userId, Long productId) {
	  Optional<Cart>opt=cartRepo.findByUserId(userId);
	  if(opt.isEmpty())
		  return false;
	  if(!productRepo.existsById(productId))
		  return false;
	  Cart cart=opt.get();
	return cartItemRepo.existsByCartIdAndProductId(cart.getId(), productId);
  }

  @Override
  public CartResponseDTO incrementItemQuantity(Long userId, Long productId, int quantity) {
	if(quantity<=0) {
		throw new IllegalArgumentException("quantity must be positive");
	}
	Cart cart=getCart(userId);
	Product product=findProductById(productId);
	Optional<CartItem> opt=cartItemRepo.findByCartIdAndProductId(cart.getId(), productId);
	if(opt.isEmpty()) {
		throw new ResourceNotFoundException("cartItem", "productId", productId);
	}
	validateProductAvailability(product, quantity);
	CartItem cartItem=opt.get();
	int newQty=cartItem.getQuantity()+quantity;
	validateProductAvailability(product,newQty);
	cartItem.setQuantity(newQty);
	cartItem.calculateSubTotal();
	cart.recalulateTotals();
	cartRepo.save(cart);
	return maptToCartResponseDTO(cart);
  }

  @Override
  public CartResponseDTO decrementItemQuantity(Long userId, Long productId, int quantity) {
	  if(quantity<=0) {
			throw new IllegalArgumentException("quantity must be positive");
		}
		Cart cart=getCart(userId);
		Product product=findProductById(productId);
		Optional<CartItem> opt=cartItemRepo.findByCartIdAndProductId(cart.getId(), productId);
		if(opt.isEmpty()) {
			throw new ResourceNotFoundException("cartItem", "productId", productId);
		}
		CartItem cartItem=opt.get();
		int newQty=cartItem.getQuantity()-quantity;
		if(newQty<=0) {
			cart.removeCartItem(cartItem);
		}
		else {
		cartItem.setQuantity(newQty);
		cartItem.calculateSubTotal();
		}
		cart.recalulateTotals();
		cartRepo.save(cart);
		return maptToCartResponseDTO(cart);
  }
  
  
}
