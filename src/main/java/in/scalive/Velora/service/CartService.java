package in.scalive.Velora.service;

import in.scalive.Velora.dto.request.AddToCartRequestDTO;
import in.scalive.Velora.dto.request.UpdateCartItemRequestDTO;
import in.scalive.Velora.dto.response.CartItemResponseDTO;
import in.scalive.Velora.dto.response.CartResponseDTO;

public interface CartService {
     CartResponseDTO getCartByUserId(Long userId);
     CartResponseDTO addItemToCart(String email,AddToCartRequestDTO dto);
     CartResponseDTO updateCartItem(Long userId,Long cartItemId,UpdateCartItemRequestDTO dto);
     CartResponseDTO removeCartItem(Long userId,Long cartItemId);
     CartResponseDTO clearCart(Long userId);
     CartItemResponseDTO getCartItem(Long userId,Long cartItemId);
     boolean isProductInCart(Long userId,Long productId);
     CartResponseDTO incrementItemQuantity(Long userId,Long productId,int quantity);
     CartResponseDTO decrementItemQuantity(Long userId,Long productId,int quantity);
}
