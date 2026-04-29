package in.scalive.Velora.service.impl;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.*;
import com.sendgrid.helpers.mail.objects.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Value("${sendgrid.api.key}")
    private String apiKey;

    @Value("${app.mail.from}")
    private String fromEmail;

    // 🔐 OTP MAIL
    public void sendOtp(String toEmail, String otp) {

        try {
            Email from = new Email(fromEmail); // verified sender
            Email to = new Email(toEmail);

            String subject = "🎁 Velora - Verify Your Email";

            Content content = new Content(
                "text/html",
                "<h2>Hi there! 👋</h2>" +
                "<p>Welcome to <b>Velora</b> – where every gift tells a story 🎀</p>" +
                "<p>Your OTP is:</p>" +
                "<h1 style='color:#ff4d6d;'>" + otp + "</h1>" +
                "<p>This OTP is valid for 5 minutes.</p>" +
                "<p>If you didn’t request this, ignore this email.</p>" +
                "<br><p>With love 💖,<br>Team Velora ✨</p>"
            );

            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(apiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("OTP Mail Status: " + response.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🎉 WELCOME MAIL
    public void sendWelcomeEmail(String toEmail, String name) {

        try {
            Email from = new Email(fromEmail);
            Email to = new Email(toEmail);

            String subject = "🎉 Welcome to Velora!";

            Content content = new Content(
                "text/html",
                "<h2>Hi " + name + " 👋</h2>" +
                "<p>Welcome to <b>Velora</b> – where every gift tells a story 🎁✨</p>" +
                "<p>Your account has been successfully created 🎉</p>" +
                "<p>Explore beautiful gifts and make every moment special 💝</p>" +
                "<br><p>With love 💖,<br>Team Velora ✨</p>"
            );

            Mail mail = new Mail(from, subject, to, content);

            SendGrid sg = new SendGrid(apiKey);

            Request request = new Request();
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            System.out.println("Welcome Mail Status: " + response.getStatusCode());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
