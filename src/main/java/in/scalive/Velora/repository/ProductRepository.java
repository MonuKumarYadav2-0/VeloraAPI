package in.scalive.Velora.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import in.scalive.Velora.entity.Product;


public interface ProductRepository extends JpaRepository<Product, Long>{
        Optional<Product>  findBySku(String sku);
        boolean existsBySku(String sku);
        List<Product> findByIsAvailableTrue();
        List<Product> findByCategoryIgnoreCase(String category);
        List<Product> findByPriceBetween(double minPrice, double maxPrice);
        
        List<Product> findTop8ByOrderByIdDesc();
        
        @Query("SELECT p FROM Product p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%',:keyword,'%'))"+
        "OR LOWER(p.description) LIKE LOWER(CONCAT('%',:keyword,'%'))) AND p.isAvailable=true")
        Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
        
        @Query("UPDATE Product p SET p.stockQuantity=p.stockQuantity+:quantity WHERE p.id=:productId")
        @Modifying
        int increaseStock(@Param("productId") Long productId,@Param("quantity") Integer quantity);
        
        @Query("UPDATE Product p SET p.stockQuantity=p.stockQuantity-:quantity WHERE p.id=:productId AND p.stockQuantity>=:quantity")
        @Modifying
        int decreaseStock(@Param("productId") Long productId,@Param("quantity") Integer quantity);
        
        @Query("SELECT p FROM Product p where p.stockQuantity<=:threshold AND p.isAvailable=true")
        List<Product> findLowStockProdcuts(@Param("threshold") Integer threshold);
        
        @Query("""
        	    SELECT p FROM Product p
        	    WHERE
        	        (:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
        	            OR LOWER(p.description) LIKE LOWER(CONCAT('%', :name, '%')))
        	        
        	        AND (:category IS NULL OR LOWER(p.category) LIKE LOWER(CONCAT('%', :category, '%')))
        	        
        	        AND (:brand IS NULL OR LOWER(p.brand) LIKE LOWER(CONCAT('%', :brand, '%')))
        	        
        	        AND (:minPrice IS NULL OR p.price >= :minPrice)
        	        
        	        AND (:maxPrice IS NULL OR p.price <= :maxPrice)
        	        
        	        AND (p.isAvailable = true)
        	""")
        	List<Product> searchByFilters(
        	    @Param("name")     String name,
        	    @Param("category") String category,
        	    @Param("brand")    String brand,
        	    @Param("minPrice") Double minPrice,
        	    @Param("maxPrice") Double maxPrice
        	);
        
     // Existing searchByFilters ke neeche yeh add karo

        @Query("""
            SELECT DISTINCT p FROM Product p
            WHERE p.isAvailable = true
            AND (p.category IN :categories OR p.brand IN :brands)
            AND p.id NOT IN :excludeProductIds
            ORDER BY p.price ASC
        """)
        List<Product> findPersonalizedProducts(
            @Param("categories") List<String> categories,
            @Param("brands")     List<String> brands,
            @Param("excludeProductIds") List<Long> excludeProductIds
        );

        List<Product> findTop20ByIsAvailableTrueOrderByPriceAsc();
}
