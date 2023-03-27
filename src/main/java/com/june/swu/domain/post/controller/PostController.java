package com.june.swu.domain.post.controller;

import com.june.swu.domain.post.dto.request.PostCreateRequestDto;
import com.june.swu.domain.post.dto.request.PostUpdateRequestDto;
import com.june.swu.domain.post.dto.response.PostResponseDto;
import com.june.swu.domain.post.service.PostService;
import com.june.swu.global.common.response.model.CommonResult;
import com.june.swu.global.common.response.model.ListResult;
import com.june.swu.global.common.response.model.SingleResult;
import com.june.swu.global.common.response.service.ResponseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Tag(name = "Post")
@RequiredArgsConstructor
@RestController
public class PostController {
    private final PostService postService;
    private final ResponseService responseService;

    @Parameter(
            name = "X-AUTH-TOKEN",
            description = "로그인 성공 후 AccessToken",
            required = true,
            schema = @Schema(type = "string"),
            in = ParameterIn.HEADER)
    @Operation(summary = "게시글 등록", description = "게시글을 등록합니다.")
    @PostMapping(value = "/api/post")
    public SingleResult<PostResponseDto> createPost(
            @Valid @RequestBody PostCreateRequestDto postCreateRequestDto,
            @RequestHeader("X-AUTH-TOKEN") String accessToken) {
        PostResponseDto postResponseDto = postService.createPost(postCreateRequestDto, accessToken);
        return responseService.getSingleResult(postResponseDto);
    }


    @Parameter(
            name = "X-AUTH-TOKEN",
            description = "로그인 성공 후 AccessToken",
            required = true,
            schema = @Schema(type = "string"),
            in = ParameterIn.HEADER)
    @Operation(summary = "게시글 수정", description = "게시글을 수정합니다.")
    @PutMapping("/api/post")
    public SingleResult<PostResponseDto> updatePost(
            @Valid @RequestBody PostUpdateRequestDto postUpdateRequestDto,
            @RequestHeader("X-AUTH-TOKEN") String accessToken) {
        PostResponseDto postResponseDto = postService.updatePost(postUpdateRequestDto, accessToken);
        return responseService.getSingleResult(postResponseDto);
    }

    @Parameter(
            name = "X-AUTH-TOKEN",
            description = "로그인 성공 후 AccessToken",
            required = true,
            schema = @Schema(type = "string"),
            in = ParameterIn.HEADER)
    @Operation(summary = "게시글 삭제", description = "게시글을 삭제합니다.")
    @DeleteMapping("/api/post")
    public CommonResult deletePost(
            @RequestParam Long postId,
            @RequestHeader("X-AUTH-TOKEN") String accessToken) {
        postService.deletePost(postId, accessToken);
        return responseService.getSuccessResult();
    }

    @Parameter(
            name = "X-AUTH-TOKEN",
            description = "로그인 성공 후 AccessToken",
            required = true,
            schema = @Schema(type = "string"),
            in = ParameterIn.HEADER)
    @Operation(summary = "게시글 목록", description = "게시글 목록을 조회합니다.")
    @GetMapping("/api/posts")
    public ListResult<PostResponseDto> getPostListByPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<PostResponseDto> postDetailList = postService.getPostListWithPagination(page, size);
        return responseService.getListResult(postDetailList);
    }

    @Parameter(
            name = "X-AUTH-TOKEN",
            description = "로그인 성공 후 AccessToken",
            required = true,
            schema = @Schema(type = "string"),
            in = ParameterIn.HEADER)
    @Operation(summary = "게시글 목록(키워드)", description = "검색어 기준으로 게시글 목록을 조회합니다.")
    @GetMapping("/api/posts/search")
    public ListResult<PostResponseDto> getPostListByPagination(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {
        List<PostResponseDto> postDetailList =
                postService.getPostListByKeywordWithPagination(page, size, keyword);
        return responseService.getListResult(postDetailList);
    }
}
