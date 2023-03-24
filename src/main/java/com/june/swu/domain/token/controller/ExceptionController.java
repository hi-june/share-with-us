package com.june.swu.domain.token.controller;

import com.june.swu.domain.token.exception.CAccessDeniedException;
import com.june.swu.domain.token.exception.CAuthenticationEntryPointException;
import com.june.swu.global.common.response.model.CommonResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Exception")
@RequiredArgsConstructor
@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @GetMapping("/entryPoint")
    public CommonResult entrypointException() {
        throw new CAuthenticationEntryPointException();
    }

    @GetMapping("/accessDenied")
    public CommonResult accessDeniedException() {
        throw new CAccessDeniedException();
    }
}