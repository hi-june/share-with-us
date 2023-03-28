package com.june.swu.domain.post.service;

import com.june.swu.domain.location.dto.response.LocationResponseDto;
import com.june.swu.domain.location.entity.Location;
import com.june.swu.domain.location.exception.CLocationNotFoundException;
import com.june.swu.domain.location.repository.LocationRepository;
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
    private final LocationRepository locationRepository;

    /**
     * 게시글 작성
     *
     * accessToken에서 요청한 사람의 정보를 가져와 post를 저장합니다.
     * 식당의 좌표 정보도 함께 가져와 location에 저장합니다.
     *
     * @param postCreateRequestDto
     * @param accessToken
     * @return
     */
    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto postCreateRequestDto, String accessToken) {
        Authentication authentication = getAuthByAccessToken(accessToken);

        // user pk로 유저 검색 / repo 에 저장된 Refresh Token 가져오기
        User user = userRepository
                .findById(Long.parseLong(authentication.getName()))
                .orElseThrow(CUserNotFoundException::new);

        Post post = postCreateRequestDto.toEntity(user);
        Post savedPost = postRepository.save(post);

        Location location = postCreateRequestDto.getLocation().toEntity(savedPost);
        locationRepository.save(location);

        return mapPostEntityToPostResponseDto(savedPost);
    }

    /**
     * 게시글 수정
     *
     * 게시글 번호를 통해 post를 찾습니다.
     * access Token을 통해 요청한 사람과 post 작성자가 일치하는지 검사 합니다.
     * 식당 좌표 정보 또한 함께 업데이트합니다.
     *
     * @param postUpdateRequestDto
     * @param accessToken
     * @return
     */
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

        // 식당 좌표 찾기
        Location location = locationRepository
                        .findByPostId(postUpdateRequestDto.getPostId())
                        .orElseThrow(CLocationNotFoundException::new);

        location.updateLocation(postUpdateRequestDto.getLocation());    // 식당 좌표 업데이트
        post.updatePost(postUpdateRequestDto);  // post 업데이트

        return mapPostEntityToPostResponseDto(post);
    }

    /**
     * 게시글 삭제
     *
     * soft delete를 진행합니다.
     * isActive 필드만 false로 설정합니다.
     * post를 delete 시 그에 엮여있는 location도 같이 delete하도록 만듭니다.
     *
     * @param id
     * @param accessToken
     */
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

        // 좌표 값도 삭제
        Location location = locationRepository
                        .findByPostId(post.getId())
                        .orElseThrow(CLocationNotFoundException::new);

        post.deletePost();
        location.deleteLocation();
        postRepository.save(post);
        locationRepository.save(location);
    }

    /**
     * 게시글 목록 조회
     *
     * page 객체를 통해 게시글의 목록을 조회합니다.
     *
     * @param page
     * @param size
     * @return
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostListWithPagination(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return postRepository.findPostsWithPagination(pageRequest).stream()
                .map(this::mapPostEntityToPostResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 게시글 목록 조회(검색어)
     *
     * 검색어를 받아 게시글의 목록을 조회합니다.
     *
     * @param page
     * @param size
     * @param keyword
     * @return
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostListByKeywordWithPagination(int page, int size, String keyword) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return postRepository.findPostsByKeywordWithPagination(pageRequest, keyword).stream()
                .map(this::mapPostEntityToPostResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * 인증정보 가져오기
     *
     * access token을 검증하고, claims에 포함된 정보를 가져옵니다.
     *
     * @param accessToken
     * @return
     */
    private Authentication getAuthByAccessToken(String accessToken) {
        // 만료된 access token인지 확인
        if (!jwtProvider.validationToken(accessToken)) {
            throw new CAccessTokenException();
        }

        // AccessToken 에서 Username (pk) 가져오기
        Authentication authentication = jwtProvider.getAuthentication(accessToken);

        return authentication;
    }

    /**
     * post entity로부터 post 응답 모델을 변환합니다.
     *
     * @param post
     * @return
     */
    private PostResponseDto mapPostEntityToPostResponseDto(Post post) {
        LocationResponseDto location = getLocationResponseDto(post);

        return PostResponseDto.builder()
                .postId(post.getId())
                .creatorName(post.getCreator().getName())
                .title(post.getTitle())
                .orderAt(post.getOrderAt())
                .recruitment(post.getRecruitment())
                .location(location)
                .foodCategory(post.getFoodCategory())
                .build();
    }

    /**
     * post entity로부터 location 응답 모델을 반환합니다.
     *
     * @param post
     * @return
     */
    private LocationResponseDto getLocationResponseDto(Post post) {
        Location location = locationRepository
                .findByPostId(post.getId())
                .orElseThrow(CLocationNotFoundException::new);

        return LocationResponseDto.builder()
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .build();
    }
}
