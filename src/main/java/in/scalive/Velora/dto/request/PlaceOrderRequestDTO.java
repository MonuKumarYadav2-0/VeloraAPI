package in.scalive.Velora.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class PlaceOrderRequestDTO {
  @NotNull(message = "userId is required")
  private Long userId;
  @Size(max=500,message = "size must not exceed 500 characters")
  private String notes;
}
