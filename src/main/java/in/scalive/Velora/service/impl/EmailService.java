package in.scalive.Velora.service.impl;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail, String otp) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("your_email@gmail.com"); // 🔥 MUST ADD
        message.setTo(toEmail);
        message.setSubject("🎁 Velora - Verify Your Email");

        message.setText(
            "Hi there! 👋\n\n" +
            "Welcome to Velora – where every gift tells a story 🎀\n\n" +
            "To complete your registration, please use the OTP below:\n\n" +
            "🔐 OTP: " + otp + "\n\n" +
            "This OTP is valid for 5 minutes.\n\n" +
            "If you didn’t request this, you can safely ignore this email.\n\n" +
            "With love 💖,\n" +
            "Team Velora ✨"
        );

        mailSender.send(message);
    }

    public void sendWelcomeEmail(String toEmail, String name) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("your_email@gmail.com"); // 🔥 MUST ADD
        message.setTo(toEmail);
        message.setSubject("🎉 Welcome to Velora!");

        message.setText(
            "Hi " + name + " 👋\n\n" +
            "Welcome to Velora – where every gift tells a story 🎁✨\n\n" +
            "Your account has been successfully created 🎉\n\n" +
            "Now you can explore a world full of beautiful gifts and surprises 💝\n\n" +
            "Start your journey with us and make every moment special!\n\n" +
            "With love 💖,\n" +
            "Team Velora ✨"
        );

        mailSender.send(message);
    }
}
