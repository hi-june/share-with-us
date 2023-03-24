package com.june.swu.domain.token.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 정상적으로 Jwt이 제대로 오지 않은 경우
 * 토큰 인증 처리 자체가 불가능한 경우로 토큰을 검증하는 곳에서 프로세스가 끝나버리게 된다.
 * 따라서 해당 예외를 잡아내기 위해서는 스프링 시큐리티가 제공하는 AuthenticationEntryPoint를 상속받아서 재정의해야한다.
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException)
            throws IOException, ServletException {

        response.sendRedirect("/exception/entryPoint");
    }
}
