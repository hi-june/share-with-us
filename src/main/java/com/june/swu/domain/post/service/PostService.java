package com.june.swu.domain.post.service;

import com.june.swu.domain.post.dto.request.PostCreateRequestDto;
import com.june.swu.domain.post.dto.request.PostUpdateRequestDto;
import com.june.swu.domain.post.dto.response.PostResponseDto;
import com.june.swu.domain.post.entity.Post;
import com.june.swu.domain.post.exception.CPostNotFoundException;
import com.june.swu.domain.post.exception.CPostUpdateNotAllowed;
import com.june.swu.domain.post.repository.PostRepository;
import com.june.swu.domain.token.config.JwtProvider;
import com.june.swu.domain.token.exception.CAccessTokenException;
import com.june.swu.domain.user.entity.User;
import com.june.swu.domain.user.exception.CUserNotFoundException;
import com.june.swu.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PostService {
    private final PostRepository postRepository;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto postCreateRequestDto, String accessToken) {
        Authentication authentication = getAuthByAccessToken(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 가져오기
        User user = userRepository
                .findById(Long.parseLong(authentication.getName()))
                .orElseThrow(CUserNotFoundException::new);

        Post post = postCreateRequestDto.toEntity(user);

        return new PostResponseDto(postRepository.save(post));
    }

    @Transactional
    public PostResponseDto updatePost(PostUpdateRequestDto postUpdateRequestDto, String accessToken) {
        Authentication authentication = getAuthByAccessToken(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 가져오기
        User user = userRepository
                .findById(Long.parseLong(authentication.getName()))
                .orElseThrow(CUserNotFoundException::new);

        // 게시글 찾기
        Post post = postRepository
                        .findById(postUpdateRequestDto.getPostId())
                        .orElseThrow(CPostNotFoundException::new);

        // 수정 요청자와 게시글 작성자 비교
        if (!user.equals(post.getCreator())) {
            throw new CPostUpdateNotAllowed();
        }

        post.updatePost(postUpdateRequestDto);

        return new PostResponseDto(post);
    }

    @Transactional
    public void deletePost(Long id, String accessToken) {
        Authentication authentication = getAuthByAccessToken(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 가져오기
        User user = userRepository
                .findById(Long.parseLong(authentication.getName()))
                .orElseThrow(CUserNotFoundException::new);

        // 게시글 찾기
        Post post = postRepository
                .findPostById(id)
                .orElseThrow(CPostNotFoundException::new);

        // 수정 요청자와 게시글 작성자 비교
        if (!user.equals(post.getCreator())) {
            throw new CPostUpdateNotAllowed();
        }

        post.deletePost();
        postRepository.save(post);
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostListWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return postRepository.findPostsWithPagination(pageRequest).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostListByKeywordWithPagination(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return postRepository.findPostsByKeywordWithPagination(pageRequest, keyword).stream()
                .map(PostResponseDto::new)
                .collect(Collectors.toList());
    }

    private Authentication getAuthByAccessToken(String accessToken) {
        // 만료된 access token인지 확인
        if (!jwtProvider.validationToken(accessToken)) {
            throw new CAccessTokenException();
        }

        // AccessToken 에서 Username (pk) 가져오기
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        return authentication;
    }
}
