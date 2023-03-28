package com.june.swu.domain.location.dto.request;

import com.june.swu.domain.location.entity.Location;
import com.june.swu.domain.post.entity.Post;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;


@Getter
public class LocationRequestDto {
    @NotNull(message = "latitude는 공백이 올 수 없습니다.")
    @Min(value = 0, message = "위도는 0이하가 될 수 없습니다.")
    private Float latitude;

    @NotNull(message = "longitude는 공백이 올 수 없습니다.")
    @Min(value = 0, message = "경도는 0이하가 될 수 없습니다.")
    private Float longitude;

    public Location toEntity(Post post) {
        return Location.builder()
                .latitude(latitude)
                .longitude(longitude)
                .post(post)
                .build();
    }
}