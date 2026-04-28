package in.scalive.Velora.service.impl;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import in.scalive.Velora.entity.Order;
import in.scalive.Velora.exception.PaymentVerificationException;
import in.scalive.Velora.exception.ResourceNotFoundException;
import in.scalive.Velora.repository.OrderRepository;
import in.scalive.Velora.repository.UserRepository;
import in.scalive.Velora.service.OrderService;
import in.scalive.Velora.service.PaymentService;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private OrderRepository orderRepo;

    @Value("${razorpay.key}")
    private String key;

    @Value("${razorpay.secret}")
    private String secret;

    

    @Override
    public void verifyPayment(String razorpayOrderId,
                              String paymentId,
                              String signature) {

        Order order = orderRepo.findByRazorpayOrderId(razorpayOrderId)
                .orElseThrow(() -> new ResourceNotFoundException("razorPayOrderId","Order",razorpayOrderId));

        String generatedSignature = hmacSHA256(
                razorpayOrderId + "|" + paymentId,
                secret
        );

        if (!generatedSignature.equals(signature)) {
            order.setStatus(Order.STATUS_FAILED);
            orderRepo.save(order);

            throw new PaymentVerificationException("Payment verification failed");
        }

        order.setStatus(Order.STATUS_CONFIRMED);
        order.setRazorpayPaymentId(paymentId);
        order.setRazorpaySignature(signature);

        orderRepo.save(order);
    }

    // 🔐 HMAC function
    private String hmacSHA256(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(secretKey);

            byte[] hash = mac.doFinal(data.getBytes());
            return bytesToHex(hash);

        } catch (Exception e) {
            throw new RuntimeException("Error while generating HMAC", e);
        }
    }
    
    private String bytesToHex(byte[] hash) {
        StringBuilder hex = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String s = Integer.toHexString(0xff & b);
            if (s.length() == 1) hex.append('0'); // leading zero
            hex.append(s);
        }
        return hex.toString();
    }
}
