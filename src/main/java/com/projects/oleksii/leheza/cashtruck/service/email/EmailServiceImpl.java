package com.projects.oleksii.leheza.cashtruck.service.email;


import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.User;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
public class EmailServiceImpl {

    private static final String UTF_8 = "UTF-8";
    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;

    @Async
    public void sendEmail(String fromAddress, String toAddress, String subject, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("leheza.oleksii@gmail.com"); //TODO replace with fromAddress
        simpleMailMessage.setTo(toAddress);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);
        mailSender.send(simpleMailMessage);
    }

    @Async
    public void sendEmailWithAttachment(String from, EmailContext emailContext) {
        try {
            MimeMessage mimeMessage = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8);
            User userFrom = userRepository.findByEmailIgnoreCase(from);
            if (userFrom != null) {
                helper.setFrom(from);
            } else {
                throw new MessagingException("User with email does not exist at the system"); //User with email does not exist at the system
            }
            User userTo = userRepository.findByEmailIgnoreCase(from);
            if (userTo != null) {
                helper.setTo(emailContext.getTo());
            } else {
                throw new MessagingException("User with email does not exist at the system"); //User with email does not exist at the system
            }
            helper.setSubject(emailContext.getSubject());
            helper.setText(emailContext.getEmail());
            MultipartFile attachment = emailContext.getAttachment();
            if (attachment != null && !attachment.isEmpty()) {
                helper.addAttachment(attachment.getName(), attachment);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            System.out.println(exception.getMessage());
            throw new RuntimeException(exception.getMessage());
        }
    }

    @Async
    public void sendConformationEmailRequest(String to, String token) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("leheza.oleksii@gmail.com"); //TODO replace with fromAddress
        simpleMailMessage.setTo("oleksii.leheza@gmail.com");
        simpleMailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
        simpleMailMessage.setText("12345");
        mailSender.send(simpleMailMessage);
    }

    @Async
    public static String getEmailMessage(String name, String host, String token) {
        return "Hello " + name + ", \n\nYour new account has been created. Please click the link below to verify your account. \n\n" +
                getVerificationUrl(host, token) + "\n\nThe support Team";
    }

    public static String getVerificationUrl(String host, String token) {
        return host + "/api/users?token=" + token;
    }


    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }
}
