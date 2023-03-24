package com.june.swu.global.config;

import com.june.swu.domain.token.config.CustomAccessDeniedHandler;
import com.june.swu.domain.token.config.CustomAuthenticationEntryPoint;
import com.june.swu.domain.token.config.JwtAuthenticationFilter;
import com.june.swu.domain.token.config.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {
    private final JwtProvider jwtProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable() // 기본설정은 비 인증시 로그인 폼 화면으로 리다이렉트 되는데 RestApi이므로 disable 함
                .csrf().disable() // rest api이므로 상태를 저장하지 않으니 csrf 보안을 설정하지 않아도된다.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Jwt으로 인증하므로 세션이 필요지 않으므로 생성 안한다.

                .and()
                .authorizeRequests() // URL 별 권한 관리를 설정하는 옵션의 시작점, antMathcers를 작성하기 위해서는 먼저 선언되어야 한다.
                .antMatchers(HttpMethod.POST, "/api/signup", "/api/login", "/api/reissue")
                .permitAll()
                .anyRequest() // 그 외 나머지 요청은 인증된 회원만 가능함
                .hasRole("USER")

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(customAuthenticationEntryPoint)
                .accessDeniedHandler(customAccessDeniedHandler)

                .and()
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public WebSecurityCustomizer ignoringWebSecurityCustomizer() {
        return (web) -> web.ignoring()
                .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**");
    }
}
