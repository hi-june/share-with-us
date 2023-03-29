package com.june.swu.domain.post.dto.request;

import lombok.Getter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
public class PointRequestDto {
    @NotNull(message = "latitude는 공백이 올 수 없습니다.")
    @Min(value = 0, message = "위도는 0이하가 될 수 없습니다.")
    private double latitude;

    @NotNull(message = "longitude는 공백이 올 수 없습니다.")
    @Min(value = 0, message = "경도는 0이하가 될 수 없습니다.")
    private double longitude;
}
