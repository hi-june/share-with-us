package com.june.swu.domain.user.service;

import com.june.swu.domain.user.exception.CUserNotFoundException;
import com.june.swu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 스프링 시큐리티에서 토큰에 포함된 유저 정보로 유저를 조회하는 것을 UserDetailsService인터페이스에 만들어놨는데
 * 여기에는 단 하나의 메소드 loadUserByUsername(String username)가 존재한다.
 */
@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userPk) throws UsernameNotFoundException {
        return userRepository.findById(Long.parseLong(userPk)).orElseThrow(CUserNotFoundException::new);
    }
}
