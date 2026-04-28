package in.scalive.Velora.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class CartResponseDTO {
  private Long id;
  private Long userId;
  private String userName;
  private List<CartItemResponseDTO> items;
  private Integer totalItems;
  private Double totalAmount;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
