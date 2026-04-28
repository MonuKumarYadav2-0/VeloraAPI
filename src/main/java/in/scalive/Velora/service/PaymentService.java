package in.scalive.Velora.service;

public interface PaymentService {

    void verifyPayment(String razorpayOrderId,
                       String paymentId,
                       String signature) throws Exception;
}