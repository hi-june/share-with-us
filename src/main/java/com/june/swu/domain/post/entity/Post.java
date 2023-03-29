package com.june.swu.domain.post.entity;

import com.june.swu.domain.post.dto.request.PostUpdateRequestDto;
import com.june.swu.domain.user.entity.User;
import com.june.swu.global.common.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

import javax.persistence.*;
import java.time.LocalDateTime;

@Table(name = "post")
@Builder
@NoArgsConstructor @AllArgsConstructor
@Getter
@Entity
public class Post extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @Column(nullable = false)
    private String title;   // 게시글 제목

    @Column(nullable = false)
    private LocalDateTime orderAt;  // 주문 예정 시간

    @Column(nullable = false)
    private Integer recruitment;    // 모집 인원

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FoodCategory foodCategory;  // 음식 종류

    @Column(columnDefinition = "geometry(Point, 4326)", nullable = false)
    private Point restaurantPoint;  // 음식점(Point) 객체

    @Column
    private Boolean isActive;

    @PrePersist
    public void prePersist() {
        this.isActive = this.isActive == null ? true : this.isActive;
    }

    public void updatePost(PostUpdateRequestDto postUpdateRequestDto, Point point) {
        this.title = postUpdateRequestDto.getTitle();
        this.orderAt = postUpdateRequestDto.getOrderAt();
        this.recruitment = postUpdateRequestDto.getRecruitment();
        this.foodCategory = postUpdateRequestDto.getFoodCategory();
        this.restaurantPoint = point;
    }

    public void deletePost() {
        this.isActive = false;
    }
}
