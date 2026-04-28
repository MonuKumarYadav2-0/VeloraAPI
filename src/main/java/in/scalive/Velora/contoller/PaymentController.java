package in.scalive.Velora.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.scalive.Velora.dto.request.PaymentVerificationRequestDTO;
import in.scalive.Velora.dto.request.PlaceOrderRequestDTO;
import in.scalive.Velora.dto.response.ApiResponseDTO;
import in.scalive.Velora.dto.response.OrderResponseDTO;
import in.scalive.Velora.service.OrderService;
import in.scalive.Velora.service.PaymentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private OrderService orderService;

   
    @PostMapping("/create")
    public  ResponseEntity<ApiResponseDTO<OrderResponseDTO>> placeOrder(@Valid @RequestBody PlaceOrderRequestDTO request) throws Exception {
        OrderResponseDTO dto=orderService.placeOrder(request);
        return new ResponseEntity<>(ApiResponseDTO.success(dto),HttpStatus.CREATED);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponseDTO<String>> verify(
            @Valid @RequestBody PaymentVerificationRequestDTO request) throws Exception{

        paymentService.verifyPayment(
                request.getRazorpay_order_id(),
                request.getRazorpay_payment_id(),
                request.getRazorpay_signature()
        );

        return ResponseEntity.ok(ApiResponseDTO.success("Payment Verified"));
    }
}