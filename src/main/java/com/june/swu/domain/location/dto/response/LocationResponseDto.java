package com.june.swu.domain.location.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class LocationResponseDto {
    private Float latitude;
    private Float longitude;
}