package com.real.realoasis.domain.auth.service.Impl;

import com.real.realoasis.domain.auth.data.dto.LoginDto;
import com.real.realoasis.domain.auth.data.dto.SearchPwDto;
import com.real.realoasis.domain.auth.data.dto.SignupDto;
import com.real.realoasis.domain.auth.data.dto.TokenDto;
import com.real.realoasis.domain.auth.data.response.SearchPwResponse;
import com.real.realoasis.domain.auth.data.response.SignupResponse;
import com.real.realoasis.domain.auth.data.response.TokenResponse;
import com.real.realoasis.domain.auth.exception.ExpiredTokenException;
import com.real.realoasis.domain.auth.exception.InvalidTokenException;
import com.real.realoasis.domain.auth.service.AuthService;
import com.real.realoasis.domain.auth.util.AuthConverter;
import com.real.realoasis.domain.user.data.entity.User;
import com.real.realoasis.domain.user.exception.DuplicateIdException;
import com.real.realoasis.domain.user.facade.UserFacade;
import com.real.realoasis.global.error.type.ErrorCode;
import com.real.realoasis.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserFacade userFacade;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final AuthConverter authConverter;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TokenResponse login(LoginDto loginDto) {
        User user = userFacade.findUserById(loginDto.getId());
        userFacade.checkPassword(user, loginDto.getPassword());

        String accessToken = jwtTokenProvider.generateAccessToken(loginDto.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(loginDto.getId());
        Long expiredAt = jwtTokenProvider.getExpiredTime(accessToken);

        redisTemplate.opsForValue()
                .set("RefreshToken:" + user.getId(), refreshToken,
                        jwtTokenProvider.getExpiredTime(refreshToken), TimeUnit.MILLISECONDS);

        TokenDto tokenDto = authConverter.toTokenDto(accessToken, refreshToken, expiredAt, user);

        return authConverter.toTokenResponse(tokenDto);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TokenResponse reissue(String refreshToken) {
        if(jwtTokenProvider.validateToken(refreshToken)){
            throw new ExpiredTokenException(ErrorCode.EXPIRATION_TOKEN_EXCEPTION);
        }

        User user = userFacade.findUserById(jwtTokenProvider.getUserPk(refreshToken));

        String redisRefreshToken = (String) redisTemplate.opsForValue().get("refresh:" + user.getId());

        if(Objects.equals(redisRefreshToken, refreshToken)){
            throw new InvalidTokenException(ErrorCode.INVALID_TOKEN_EXCEPTION);
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId());
        Long expiredAt = jwtTokenProvider.getExpiredTime(newAccessToken);

        redisTemplate.opsForValue()
                .set("RefreshToken:" + user.getId(), newRefreshToken,
                        jwtTokenProvider.getExpiredTime(newRefreshToken), TimeUnit.MILLISECONDS);

        TokenDto tokenDto = authConverter.toTokenDto(newAccessToken, newRefreshToken, expiredAt, user);
        return authConverter.toTokenResponse(tokenDto);
    }

    @Override
    public SearchPwResponse searchPW(SearchPwDto searchPwDto) {
        User user = userFacade.findUserById(searchPwDto.getId());
        String pw = user.getPassword();

        return authConverter.toSearchPwResponse(pw);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SignupResponse signUp(SignupDto signupDto) {
        User user = authConverter.toEntity(signupDto);
        if(userFacade.existsById(user.getId())){
            throw new DuplicateIdException(ErrorCode.DUPLICATE_ID_EXCEPTION);
        }
        userFacade.saveUser(user);

        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for(int i = 0; i < 8; i++) {
            int index = random.nextInt(3);
            switch (index) {
                case 0 :
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1 :
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append(random.nextInt(9));
                    break;
            }
        }
        return authConverter.toSignResponse(key.toString());
    }
}
