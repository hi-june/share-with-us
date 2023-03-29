package com.june.swu.domain.post.dto.response;

import com.june.swu.domain.post.entity.FoodCategory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Builder
@Getter
public class PostResponseDto {
    private Long postId;    // 게시글 번호(인덱스)
    private String creatorName; // 게시자 이름
    private String title;   // 게시글 제목
    private LocalDateTime orderAt;  // 주문 예정 시간
    private Integer recruitment;    // 모집 인원
    private PointResponseDto location;   // 식당 좌표
    private FoodCategory foodCategory;  // 음식 종류
}
