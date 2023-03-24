package com.june.swu.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.june.swu.global.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "Users")
@Entity
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // Json 결과로 출력하지 않을 값들은 애노테이션 선언으로 read하지 못하게 함
    @Column(length = 100)
    private String password;

    @Column(nullable = false, unique = true, length = 30)
    private String email;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String nickName;

    @ElementCollection(fetch = FetchType.EAGER) // User 객체의 권한이 담긴 컬렉션 객체를 User 조회시 EAGER로 즉시로딩하지 않는다면, Porxy객체가 담겨서 반환되므로 제대로 "ROLE_USER"를 확인할 수 없다.
    @Builder.Default
    private List<String> roles = new ArrayList<>(); // 권한은 회원당 여러개가 정의될 수 있으므로 컬렉션 타입으로 정의

    public void updateNickName(String nickName) {
        this.nickName = nickName;
    }


    /**
     * UserDetails Overriding
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public String getUsername() {
        return String.valueOf(this.userId);
    }


    // 아래는 스프링 시큐리티가 제공하는 회원 보안관련 메소드. 여기서는 사용하지 않으므로 기본 true로 세팅
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }   // 계정이 만료되었는지 여부

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }   // 계정이 잠겼는지 여부

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }   // 계정 패스워드가 만료되었는지 여부

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        return true;
    }   // 계정이 사용하능한지 여부
}
