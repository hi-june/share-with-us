package com.june.swu.global.common.response.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(title = "공통 응답 모델", description = "전달될 데이터와 별개로 API의 처리여부, 상태, 메시지가 담긴 데이터")
public class CommonResult {
    @Schema(title = "응답 성공 여부", description = "여부에 따라 True 혹은 False")
    private boolean success;

    @Schema(title = "응답 코드", description = "0 이상이면 정상, 0 미만이면 비정상")
    private int code;

    @Schema(title = "응답 메시지")
    private String message;
}
