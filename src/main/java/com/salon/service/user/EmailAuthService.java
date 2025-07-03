package com.salon.service.user;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailAuthService {

    private final JavaMailSender mailSender;

    private static final String AUTH_CODE_KEY = "authCode";
    private static final String AUTH_EMAIL_KEY = "authEmail";
    private static final String AUTH_SUCCESS_KEY = "authSuccess";

    public boolean sendCodeEmail(String email, String context, HttpSession session) {
        String code = generateCode();

        String subject;
        String text;

        switch (context) {
            case "find" -> {
                subject = "SalonLog 아이디 찾기 인증번호 안내";
                text = "아이디 찾기 인증 번호입니다. 아이디를 찾으시려면 아래의 인증번호를 입력하세요.\n\n[ " + code + " ]";
            }
            case "pw" -> {
                subject = "SalonLog 비밀번호 찾기 인증번호 안내";
                text = "비밀번호 찾기 인증 번호입니다. 비밀번호를 변경하시려면 아래의 인증번호를 입력하세요.\n\n[ " + code + " ]";
            }
            case "signUp_chk" -> {
                subject = "SalonLog 회원가입 이메일 인증번호 안내";
                text = "나의 완벽한 순간을 남기다. SalonLog에 가입하시려면 아래의 인증번호를 입력하여주세요.\n\n[ " + code + " ]";
            } default ->  {
                System.out.println("[WARN] 알 수 없는 context: " + context);
                return false;
            }
        }

        session.setAttribute(AUTH_CODE_KEY, code);
        session.setAttribute(AUTH_EMAIL_KEY, email);
        session.setAttribute(AUTH_SUCCESS_KEY, false);

        System.out.println("[DEBUG] 이메일 인증 요청: email=" + email + ", code=" + code);

        return send(email, subject, text);

    }

    public boolean sendPasswordResetNoticeEmail(String email) {
        String subject = "SalonLog 비밀번호가 변경되었습니다.";
        String text = "안녕하세요. SalonLog입니다. \n 요청하신 비밀번호가 성공적으로 변경되었습니다. \n\n" + "만약 본인이 비밀번호 변경을 요청한게 아닐시에는 즉시 비밀번호를 재변경하고 고객센터에 문의하여주십시오.";

        return send(email, subject, text);
    }

    public boolean send (String email, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);

            return true;
        } catch (Exception e) {
            System.out.println("이메일 전송 실패 : " + email);
             e.printStackTrace();
             return false;
        }
    }


    public String generateCode() {
        Random random = new Random();

        int code = 100000 + random.nextInt(900000);

        return String.valueOf(code);
    }

}
