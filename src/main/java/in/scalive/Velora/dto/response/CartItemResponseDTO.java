package in.scalive.Velora.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponseDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;
    private String productSku;
    private Double unitPrice;
    private Integer quantity;
    private Double subTotal;
    private Integer availableStock;
    private LocalDateTime addedAt;
}
