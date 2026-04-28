package in.scalive.Velora.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.scalive.Velora.dto.response.ProductResponseDTO;
import in.scalive.Velora.entity.CartItem;
import in.scalive.Velora.entity.Order;
import in.scalive.Velora.entity.OrderItem;
import in.scalive.Velora.entity.Product;
import in.scalive.Velora.repository.CartRepository;
import in.scalive.Velora.repository.OrderRepository;
import in.scalive.Velora.repository.ProductRepository;

@Service
public class FeedService {
	@Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;
    
    private ProductResponseDTO mapToProductResponseDTO(Product product) {
		return ProductResponseDTO.builder()
				.id(product.getId())
				.name(product.getName())
				.description(product.getDescription())
				.price(product.getPrice())
				.stockQuantity(product.getStockQuantity())
				.category(product.getCategory())
				.brand(product.getBrand())
				.imageUrl(product.getImageUrl())
				.sku(product.getSku())
				.isAvailable(product.getIsAvailable())
				.inStock(product.getStockQuantity()>0)
				.build();
	}

    public List<ProductResponseDTO> getPersonalizedFeed(Long userId) {

        List<String> categories = new ArrayList<>();
        List<String> brands = new ArrayList<>();
        List<Long> excludeProductIds = new ArrayList<>(); // jo already order/cart mein hain unhe feed mein mat dikhao

        // ── Step 1: Order history se categories/brands nikalo ──
        List<Order> orders = orderRepository.findByUserId(userId);

        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                Product p = item.getProduct();
                if (p.getCategory() != null) categories.add(p.getCategory());
                if (p.getBrand() != null)    brands.add(p.getBrand());
                excludeProductIds.add(p.getId()); // already ordered product ko exclude karo
            }
        }

        // ── Step 2: Cart se categories/brands nikalo ──
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            for (CartItem item : cart.getCartItems()) {
                Product p = item.getProduct();
                if (p.getCategory() != null) categories.add(p.getCategory());
                if (p.getBrand() != null)    brands.add(p.getBrand());
                excludeProductIds.add(p.getId()); // cart wale product ko bhi exclude karo
            }
        });

        // ── Step 3: Agar user naya hai — koi history nahi ──
        if (categories.isEmpty() && brands.isEmpty()) {
            // Sabse saste available products dikhao (default feed)
        	    List<Product>productList= productRepository.findTop20ByIsAvailableTrueOrderByPriceAsc();
        	    List<ProductResponseDTO>responseList=new ArrayList<>();
        	    for(Product product:productList) {
        	    	     responseList.add(mapToProductResponseDTO(product));
        	    }
        	    return responseList;
        }

        // ── Step 4: Personalized products fetch karo ──
        // Agar excludeProductIds empty hai toh dummy -1 daalo (IN () SQL error deta hai)
        if (excludeProductIds.isEmpty()) excludeProductIds.add(-1L);

        List<Product>productList= productRepository.findPersonalizedProducts(categories, brands, excludeProductIds);
        List<ProductResponseDTO>responseList=new ArrayList<>();
	    for(Product product:productList) {
	    	     responseList.add(mapToProductResponseDTO(product));
	    }
	    return responseList;
   }
}
