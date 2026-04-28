package in.scalive.Velora.contoller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import in.scalive.Velora.dto.request.UserRequestDTO;
import in.scalive.Velora.service.impl.OtpVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController // 🔥 REQUIRED
@RequestMapping("/auth") // 🔥 base path
@Tag(name = "OTP Authentication APIs", description = "Handles OTP based registration")
public class OtpController {

    private OtpVerificationService service;

    @Autowired // 🔥 inject service
    public OtpController(OtpVerificationService service) {
        this.service = service;
    }

    // 🔥 REGISTER (SEND OTP)
    @Operation(summary = "Register user and send OTP to email")
    @PostMapping("/register-user")
    public ResponseEntity<?> register(@org.springframework.web.bind.annotation.RequestBody UserRequestDTO dto) {

        service.register(dto);
        return ResponseEntity.ok("OTP sent to email");
    }

    // 🔥 VERIFY OTP
    @Operation(summary = "Verify OTP and create user")
    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email,
                                       @RequestParam String otp) {

        return ResponseEntity.ok(service.verifyOtp(email, otp));
    }
}