package com.june.swu.global.common.response.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "단일 응답 모델", description = "API 반환값이 단일 객체일 경우 해당 모델로 처리합니다.")
public class SingleResult<T> extends CommonResult {
    private T data;
}
