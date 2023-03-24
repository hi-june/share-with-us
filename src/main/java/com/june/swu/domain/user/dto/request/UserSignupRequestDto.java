package com.june.swu.domain.user.dto.request;

import com.june.swu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserSignupRequestDto {
    private String email;
    private String password;
    private String name;
    private String nickName;

    public User toEntity(PasswordEncoder passwordEncoder) { // password를 encoding하여 엔티티 생성
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .nickName(nickName)
                .name(name)
                .roles(Collections.singletonList("ROLE_USER"))
                .build();
    }
}
