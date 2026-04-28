package in.scalive.Velora.dto.response;

import in.scalive.Velora.dto.request.UpdateOrderStatusRequestDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponseDTO {
   private Long id;
   private Long productId;
   private String productName;
   private String productSku;
   private String productImage;
   private Integer quantity;
   private Double unitPrice;
   private Double subTotal;
   
}
