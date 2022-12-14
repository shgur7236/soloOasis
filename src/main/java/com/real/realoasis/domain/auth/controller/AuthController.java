package com.real.realoasis.domain.auth.controller;

import com.real.realoasis.domain.auth.data.dto.*;
import com.real.realoasis.domain.auth.data.request.*;
import com.real.realoasis.domain.auth.data.response.*;
import com.real.realoasis.domain.auth.service.*;
import com.real.realoasis.domain.auth.util.AuthConverter;
import com.real.realoasis.domain.auth.util.MailConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final EmailService emailService;
    private final AuthConverter authConverter;
    private final MailConverter mailConverter;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signUp(@Valid @RequestBody SignUpRequest signupRequest){
        SignupDto signupDto = authConverter.toSignupDto(signupRequest);
        SignupResponse signupResponse = authService.signUp(signupDto);
        return new ResponseEntity<>(signupResponse,HttpStatus.CREATED);
    }

    // 이메일에 인증코드 전송
    @PostMapping("/sendmail")
    public ResponseEntity<Void> sendMail(@RequestBody SendMailRequest sendMailRequest) throws MessagingException, UnsupportedEncodingException {
        MailDto mailDto = mailConverter.toDto(sendMailRequest);
        emailService.sendEmail(mailDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 인증코드 확인
    @PostMapping("/mailconfirm")
    public ResponseEntity<Void> confirmAuthenticationCode(@RequestBody AuthenticationCodeRequest authenticationCodeRequest) {
        AuthCodeDto authCodeDto = mailConverter.toDto(authenticationCodeRequest);
        emailService.confirmAuthenticationCode(authCodeDto.getCode());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginDto loginDto = authConverter.toLoginDto(loginRequest);
        TokenResponse tokenResponse = authService.login(loginDto);
        return new ResponseEntity<>(tokenResponse,HttpStatus.OK);
    }

    // 토큰 재발급
    @PatchMapping("/refresh")
    public ResponseEntity<TokenResponse> reissue(@RequestHeader("RefreshToken") String refreshToken){
        TokenResponse tokenResponse = authService.reissue(refreshToken);
        return new ResponseEntity<>(tokenResponse, HttpStatus.CREATED);
    }

    // 이메일을 통해 아이디 찾기
    @GetMapping("/search/id")
    public ResponseEntity<Void> searchID(@RequestBody SearchIdRequest searchIDRequest) throws MessagingException, UnsupportedEncodingException {
        SearchIdDto searchIdDto = mailConverter.toSearchIdDto(searchIDRequest);
        emailService.sendId(searchIdDto.getEmail());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // id 를 통해 비밀번호 찾기
    @GetMapping("/search/pw")
    public ResponseEntity<SearchPwResponse> searchPW(@RequestBody SearchPwRequest searchPWRequest) {
        SearchPwDto searchPwDto = authConverter.toSearchPwDto(searchPWRequest);
        SearchPwResponse searchPWResponse = authService.searchPW(searchPwDto);
        return new ResponseEntity<>(searchPWResponse, HttpStatus.OK);
    }
}
