package com.june.swu.domain.token.entity;

import com.june.swu.global.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "Refresh_token")
@Getter
@NoArgsConstructor
public class RefreshToken extends BaseEntity {  // 추후 expire 시간과 비교하여 만료시켜주기 위해 BaseEntity를 상속하여 time 정보를 받아옴

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "token_key") // key가 예약어일 가능성이 있기에 column명을 바꿔줌
    private Long key;   // 객체를 가져오기 위한 용도. 토큰의 claims의 값은 공개값이므로 유저를 특정할 수 없는 userPk값을 key값으로 해줌

    @Column(nullable = false)
    private String token;

    public RefreshToken updateToken(String token) {
        this.token = token;
        return this;
    }

    @Builder
    public RefreshToken(Long key, String token) {
        this.key = key;
        this.token = token;
    }
}