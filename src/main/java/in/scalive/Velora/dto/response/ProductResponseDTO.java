package in.scalive.Velora.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {
   private Long id;
   private String name;
   private String description;
   private Double price;
   private Integer stockQuantity;
   private String category;
   private String brand;
   private String imageUrl;
   private String sku;
   private Boolean isAvailable;
   private Boolean inStock;
   private double averageRating;
   private String modelUrl;
}
