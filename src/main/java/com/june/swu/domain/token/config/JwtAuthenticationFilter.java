package com.june.swu.domain.token.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {
    private final JwtProvider jwtProvider;

    // request 로 들어오는 Jwt 의 유효성을 검증 - JwtProvider.validationToken() 을 필터로써 FilterChain 에 추가
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // request header에서 토큰 쿼리값에 해당하는 value값을 통해 토큰을 가져와 검증
        String token = jwtProvider.resolveToken((HttpServletRequest) request);

        // 검증 관련 로그
        log.info("[Verifying token]");
        log.info(((HttpServletRequest) request).getRequestURL().toString());

        if (token != null && jwtProvider.validationToken(token)) {
            Authentication authentication = jwtProvider.getAuthentication(token);   // 토큰에서 유저 정보를 추출
            SecurityContextHolder.getContext().setAuthentication(authentication);   // 해당 정보를 SecurityContextHolder에 저장
        }
        filterChain.doFilter(request, response);
    }
}
