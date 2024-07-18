package com.projects.oleksii.leheza.cashtruck.service.email;


import com.projects.oleksii.leheza.cashtruck.dto.mail.EmailContext;
import com.projects.oleksii.leheza.cashtruck.domain.OtpToken;
import com.projects.oleksii.leheza.cashtruck.exception.ResourceNotFoundException;
import com.projects.oleksii.leheza.cashtruck.repository.OtpRepository;
import com.projects.oleksii.leheza.cashtruck.repository.UserRepository;
import com.projects.oleksii.leheza.cashtruck.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private static final String USER_EMAIL = "leheza.oleksii@gmail.com";
    private static final String UTF_8 = "UTF-8";
    private static final String NEW_USER_ACCOUNT_VERIFICATION = "New User Account Verification";
    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final OtpRepository otpRepository;

    @Async
    public void sendEmailWithAttachment(EmailContext emailContext) {
        try {
            MimeMessage mimeMessage = getMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, UTF_8);
            helper.setFrom(USER_EMAIL);//TODO prod input "from" variable
            helper.setTo(USER_EMAIL);//TODO prod input "to" variable
            helper.setSubject(emailContext.getSubject());
            helper.setText(emailContext.getEmail());
            MultipartFile attachment = emailContext.getAttachment();
            if (attachment != null && !attachment.isEmpty()) {
                helper.addAttachment(attachment.getName(), attachment);
            }
            mailSender.send(mimeMessage);
        } catch (MessagingException exception) {
            log.error("message sending failed; cause:{}", exception.getCause().toString());
        }
    }

    @Async
    public void sendConformationEmailRequest(String to, String token) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(USER_EMAIL);//TODO prod input "from" variable
        simpleMailMessage.setTo(USER_EMAIL);//TODO prod input "to" variable
        simpleMailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
        simpleMailMessage.setText(getEmailMessage("localhost:8080", token));//TODO change path
        mailSender.send(simpleMailMessage);
    }

    @Async
    public String getEmailMessage(String host, String token) {
        return "Your new account has been created. Please click the link below to verify your account. \n\n" +
                getVerificationUrl(host, token) + "\n\nThe support Team";
    }

    @Override
    @Async
    public void sendOTP(String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(USER_EMAIL);//TODO prod input "from" variable
        simpleMailMessage.setTo(USER_EMAIL);//TODO prod input to variable
        simpleMailMessage.setSubject(NEW_USER_ACCOUNT_VERIFICATION);
        OtpToken otp = new OtpToken(userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("user with email " + email + " does not found")));
        otpRepository.save(otp);
        simpleMailMessage.setText(Integer.toString(otp.getPassword()));
        mailSender.send(simpleMailMessage);
    }

    public String getVerificationUrl(String host, String token) {
        return host + "http://localhost:8080/auth/users?token=" + token;//TODO change path
    }

    private MimeMessage getMimeMessage() {
        return mailSender.createMimeMessage();
    }
}
