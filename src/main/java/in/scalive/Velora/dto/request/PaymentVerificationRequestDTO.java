package in.scalive.Velora.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class PaymentVerificationRequestDTO {
	@NotBlank
    private String razorpay_order_id;

    @NotBlank
    private String razorpay_payment_id;

    @NotBlank
    private String razorpay_signature;
}
