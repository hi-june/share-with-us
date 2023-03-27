package com.june.swu.domain.post.dto.request;

import com.june.swu.domain.post.entity.FoodCategory;
import com.june.swu.domain.post.entity.Post;
import com.june.swu.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.validation.constraints.Future;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PostCreateRequestDto {
    @NotBlank(message = "제목은 빈칸일 수 없습니다")
    private String title;

    @Future(message = "날짜는 미래만 고를 수 있습니다.")
    private LocalDateTime orderAt;

    @Min(value = 2, message = "최소 2명의 모집인원이 필요합니다.")
    private Integer recruitment;

    @NotNull(message = "음식점의 위치가 필요합니다.")
    private String restaurant;

    @NotNull(message = "음식 카테고리는 공백이 올 수 없습니다.")
    private FoodCategory foodCategory;

    public Post toEntity(User creator) {
        return Post.builder()
                .title(title)
                .orderAt(orderAt)
                .recruitment(recruitment)
                .restaurant(restaurant)
                .foodCategory(foodCategory)
                .creator(creator)
                .build();
    }
}
