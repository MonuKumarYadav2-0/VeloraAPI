package in.scalive.Velora.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("veloraappltd@gmail.com");
        message.setTo(toEmail);
        message.setSubject("🎁 Velora - Verify Your Email");

        message.setText(
            "Hi there! 👋" +
            "Welcome to Velora – where every gift tells a story 🎀" +
            "To complete your registration, please use the OTP below:" +
            "🔐 OTP: " + otp +
            " This OTP is valid for 5 minutes." +
            "If you didn’t request this, you can safely ignore this email.\n\n" +
            "With love 💖," +
            "Team Velora ✨"
        );
        mailSender.send(message);
   }
    
    public void sendWelcomeEmail(String toEmail, String name) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("🎉 Welcome to Velora!");

        message.setText(
            "Hi " + name + " 👋" +
            "Welcome to Velora – where every gift tells a story 🎁✨" +
            "Your account has been successfully created 🎉" +
            "Now you can explore a world full of beautiful gifts and surprises 💝" +
            "Start your journey with us and make every moment special!" +
            "With love 💖," +
            "Team Velora ✨"
        );

        mailSender.send(message);
    }
}
