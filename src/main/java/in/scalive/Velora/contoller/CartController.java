package in.scalive.Velora.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.request.AddToCartRequestDTO;
import in.scalive.Velora.dto.request.UpdateCartItemRequestDTO;
import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.CartItemResponseDTO;
import in.scalive.Velora.dto.response.CartResponseDTO;
import in.scalive.Velora.service.CartService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "*")
public class CartController {
   private CartService serv;
   @Autowired
   public CartController(CartService serv) {
	this.serv = serv;
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/{userId}")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> getCart(@PathVariable Long userId){
	   CartResponseDTO dto=serv.getCartByUserId(userId);
	   return ResponseEntity.ok(ApiResponseDTO.success(dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @PostMapping("/items")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> addItemToCart(
           Authentication authentication,
           @Valid @RequestBody AddToCartRequestDTO requestDTO) {

       String email = authentication.getName(); // logged-in user
       CartResponseDTO dto = serv.addItemToCart(email, requestDTO);

       return ResponseEntity.ok(ApiResponseDTO.success("Item added successfully", dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @DeleteMapping("/{userId}/items/{cartItemId}")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> removeItemFromCart(@PathVariable Long userId,@PathVariable Long cartItemId){
	   CartResponseDTO dto=serv.removeCartItem(userId, cartItemId);
	   return ResponseEntity.ok(ApiResponseDTO.success("Item removed successfully",dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @PutMapping("/{userId}/items/{cartItemId}")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> updateCartItem(@PathVariable Long userId,@PathVariable Long cartItemId,@Valid @RequestBody UpdateCartItemRequestDTO requestDTO){
	   CartResponseDTO dto=serv.updateCartItem(userId,cartItemId,requestDTO);
	   return ResponseEntity.ok(ApiResponseDTO.success("Item updated successfully",dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @DeleteMapping("/{userId}/clear")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> clearCart(@PathVariable Long userId){
	   CartResponseDTO dto=serv.clearCart(userId);
	   return ResponseEntity.ok(ApiResponseDTO.success("cart cleared successfully",dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/{userId}/items/{cartItemId}")
   public ResponseEntity<ApiResponseDTO<CartItemResponseDTO>> getCartItem(@PathVariable Long userId, @PathVariable Long cartItemId){
	   CartItemResponseDTO dto=serv.getCartItem(userId, cartItemId);
	   return ResponseEntity.ok(ApiResponseDTO.success(dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @GetMapping("/{userId}/check-product/{productId}")
   public ResponseEntity<ApiResponseDTO<Boolean>> isProductInCart(@PathVariable Long userId, @PathVariable Long productId){
	  Boolean inCart= serv.isProductInCart(userId, productId);
	   return ResponseEntity.ok(ApiResponseDTO.success(inCart?"Product is available in the cart":"Product is not available in the cart",inCart));
   }
   
   @PreAuthorize("hasRole('USER')")
   @PatchMapping("/{userId}/products/{productId}/increment")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> incrementItemQuantity(@PathVariable Long userId,@PathVariable Long productId,@RequestParam(defaultValue = "1") int quantity){
	   CartResponseDTO dto=serv.incrementItemQuantity(userId, productId, quantity);
	   return ResponseEntity.ok(ApiResponseDTO.success("quantity incremented successfully",dto));
   }
   
   @PreAuthorize("hasRole('USER')")
   @PatchMapping("/{userId}/products/{productId}/decrement")
   public ResponseEntity<ApiResponseDTO<CartResponseDTO>> decrementItemQuantity(@PathVariable Long userId,@PathVariable Long productId,@RequestParam(defaultValue = "1") int quantity){
	   CartResponseDTO dto=serv.decrementItemQuantity(userId, productId, quantity);
	   return ResponseEntity.ok(ApiResponseDTO.success("quantity decremented successfully",dto));
   }
}
