package in.scalive.Velora.dto.response;

import java.time.LocalDateTime;
import java.util.List;

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
public class OrderResponseDTO {
      private Long id;
      private Long userId;
      private List<OrderItemResponseDTO> orderItems;
      private String status;
      private String orderNumber;
      private String userName;
      private String userEmail;
      private Integer totalItems;
      private Double totalAmount;
      private String notes;
      private LocalDateTime orderDate; 
      private String razorpayOrderId;
}
