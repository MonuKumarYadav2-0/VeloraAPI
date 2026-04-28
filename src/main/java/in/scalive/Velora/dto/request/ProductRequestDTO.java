package in.scalive.Velora.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class ProductRequestDTO {
	   @NotBlank(message = "product name is required")
	   @Size(min = 2,max = 200,message = "product name must be in between 2 and 200 chars")
       private String name;
	   
	   @Size(max = 2000,message = "description must not exceed 2000 chars")
       private String description;
	   
	   @NotNull(message = "product price is required")
	   @Min(value = 0,message = "price must be greater than or equal to 0")
       private Double price;
	   
	   @NotNull(message = "product quantity is required")
	   @Min(value = 0,message = "quantity must be greater than or equal to 0")
       private Integer stockQuantity;
	   
	   @Size(max = 100,message = "category must not exceed 100 chars")
       private String category;
	   
	   @Size(max = 100,message = "brand must not exceed 100 chars")
       private String brand;
	   
	   @Size(max = 2000,message = "imageUrl must not exceed 2000 chars")
       private String imageUrl;
	   
	   @Size(max = 50,message = "sku must not exceed 50 chars")
       private String sku;
	   
	   private String modelUrl;
	   
       private Boolean isAvailable;
}
