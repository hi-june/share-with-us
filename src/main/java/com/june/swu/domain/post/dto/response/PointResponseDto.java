package com.june.swu.domain.post.dto.response;

import lombok.Builder;
import lombok.Getter;


@Getter
@Builder
public class PointResponseDto {
    private double latitude;
    private double longitude;
}
