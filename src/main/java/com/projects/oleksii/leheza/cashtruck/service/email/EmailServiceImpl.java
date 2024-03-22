package com.projects.oleksii.leheza.cashtruck.service.email;


import com.projects.oleksii.leheza.cashtruck.domain.EmailContext;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.util.EmailUtils;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
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
    private final EmailUtils emailUtils;

    String pop3Host = "smtp.elasticemail.com";
    String storeType = "pop3";


    //    @Async
//    public void sendEmail(String fromAddress, String toAddress, String subject, String message) {
//         SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom("leheza.oleksii@gmail.com"); //TODO replace with fromAddress
//        simpleMailMessage.setTo(toAddress);
//        simpleMailMessage.setSubject(subject);
//        simpleMailMessage.setText(message);
//        mailSender.send(simpleMailMessage);
//    }
    @Async
    public void sendEmailWithAttachment(String from,EmailContext emailContext) {
        try {
            MimeMessage mimeMessage = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8);
            helper.setFrom("leheza.oleksii@gmail.com");
//            User user = userRepository.findByEmailIgnoreCase(to);
//            if (user != null) {
            helper.setTo("leheza.oleksii@gmail.com");
//            }
            helper.setFrom("leheza.oleksii@gmail.com");
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

//    @Async
//    public void sendConformationEmailRequest(String to, String token) {
//        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
//        simpleMailMessage.setFrom("leheza.oleksii@gmail.com"); //TODO replace with fromAddress
//        simpleMailMessage.setTo("leheza.oleksii@gmail.com");
//        simpleMailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
//        simpleMailMessage.setText(message);
//        mailSender.send(simpleMailMessage);
//    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }
}
