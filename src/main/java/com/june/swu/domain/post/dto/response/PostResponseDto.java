package com.june.swu.domain.post.dto.response;

import com.june.swu.domain.post.entity.FoodCategory;
import com.june.swu.domain.post.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostResponseDto {
    private Long postId;    // 게시글 번호(인덱스)
    private String creatorName; // 게시자 이름
    private String title;   // 게시글 제목
    private LocalDateTime orderAt;  // 주문 예정 시간
    private Integer recruitment;    // 모집 인원
    private String restaurant;  // 식당 위치(추후에 좌표값으로 수정할 예정)
    private FoodCategory foodCategory;  // 음식 종류

    public PostResponseDto(Post post) {
        this.postId = post.getId();
        this.creatorName = post.getCreator().getName();
        this.title = post.getTitle();
        this.orderAt = post.getOrderAt();
        this.recruitment = post.getRecruitment();
        this.restaurant = post.getRestaurant();
        this.foodCategory = post.getFoodCategory();
    }
}
