package in.scalive.Velora.service.impl;

import java.time.LocalDateTime;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.scalive.Velora.dto.request.UserRequestDTO;
import in.scalive.Velora.dto.response.UserResponseDTO;
import in.scalive.Velora.entity.Cart;
import in.scalive.Velora.entity.OtpVerification;
import in.scalive.Velora.entity.Role;
import in.scalive.Velora.entity.User;
import in.scalive.Velora.repository.OtpRepository;
import in.scalive.Velora.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class OtpVerificationService {

    private OtpRepository otpRepo;
    private UserRepository userRepo;
    private PasswordEncoder passwordEncoder;
    private EmailService emailService;

    @Autowired
    public OtpVerificationService(OtpRepository otpRepo,
                                 UserRepository userRepo,
                                 PasswordEncoder passwordEncoder,
                                 EmailService emailService) {
        this.otpRepo = otpRepo;
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // 🔥 STEP 1: VERIFY OTP + CREATE USER
    public UserResponseDTO verifyOtp(String email, String otp) {

        OtpVerification temp = otpRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("OTP not found"));

        if(!temp.getOtp().trim().equals(otp.trim())){
            throw new RuntimeException("Invalid OTP");
        }

        if (LocalDateTime.now().isAfter(temp.getExpiryTime())) {
            throw new RuntimeException("OTP expired");
        }

        // 🔐 CREATE USER
        User user = User.builder()
                .fullName(temp.getFullName())
                .email(temp.getEmail())
                .password(passwordEncoder.encode(temp.getPassword()))
                .phone(temp.getPhone())
                .address(temp.getAddress())
                .role(Role.USER)
                .build();

        Cart cart = Cart.builder().user(user).build();
	    user.setCart(cart);
        
        User savedUser = userRepo.save(user);

        // 🧹 cleanup OTP
        otpRepo.delete(temp);
        
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFullName());

        return UserServiceImpl.mapToResponseDTO(savedUser);
    }

    // 🔥 STEP 2: REGISTER (SEND OTP)
    @Transactional
    public void register(UserRequestDTO dto) {

        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("User already exists");
        }

        // 🧹 delete old OTP if exists
        otpRepo.deleteByEmail(dto.getEmail());

        // 🔢 generate OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // 💾 save temp data
        OtpVerification temp = OtpVerification.builder()
                .email(dto.getEmail())
                .fullName(dto.getFullName())
                .password(dto.getPassword())
                .phone(dto.getPhone())
                .address(dto.getAddress())
                .otp(otp)
                .expiryTime(LocalDateTime.now().plusMinutes(5))
                .build();

        otpRepo.save(temp);

        // 📩 SEND EMAIL
        System.out.println("Before email send");
        emailService.sendOtp(dto.getEmail(), otp);
        System.out.println("After Email set");

        // optional debug
        System.out.println("OTP: " + otp);
    }
}