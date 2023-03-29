package com.june.swu.domain.post.service;

import com.june.swu.domain.post.dto.request.PointRequestDto;
import com.june.swu.domain.post.dto.request.PostCreateRequestDto;
import com.june.swu.domain.post.dto.request.PostUpdateRequestDto;
import com.june.swu.domain.post.dto.response.PointResponseDto;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
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
    private GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);  // 0(좌표 평면), 4326(위도-경도 좌표계)

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

        Point point = getPoint(
                postCreateRequestDto.getPointRequestDto().getLatitude(),    // 위도
                postCreateRequestDto.getPointRequestDto().getLongitude()    // 경도
        );

        Post post = postCreateRequestDto.toEntity(user, point);
        Post savedPost = postRepository.save(post);

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

        Point point = getPoint(
                postUpdateRequestDto.getLocation().getLatitude(),    // 위도
                postUpdateRequestDto.getLocation().getLongitude()    // 경도
        );

        post.updatePost(postUpdateRequestDto, point);  // post 업데이트

        return mapPostEntityToPostResponseDto(post);
    }

    /**
     * 게시글 삭제
     *
     * soft delete를 진행합니다.
     * isActive 필드만 false로 설정합니다.
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

        post.deletePost();
        postRepository.save(post);
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
     * 게시글 목록 조회(위치 기반)
     *
     * 사용자의 좌표를 받아 사용자와 일정 거리 이내에 있는 게시글들의 목록을 조회합니다.
     *
     * @param page
     * @param size
     * @param pointRequestDto
     * @return
     */
    @Transactional(readOnly = true)
    public List<PostResponseDto> getPostListByPointWithPagination(int page, int size, PointRequestDto pointRequestDto) {
        PageRequest pageRequest = PageRequest.of(page, size);

        Point point = getPoint(
                pointRequestDto.getLatitude(),    // 위도
                pointRequestDto.getLongitude()    // 경도
        );

        return postRepository.findPostByDistance(pageRequest, point).stream()
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
        PointResponseDto pointResponseDto =
                PointResponseDto.builder()
                        .latitude(post.getRestaurantPoint().getX())
                        .longitude(post.getRestaurantPoint().getY())
                        .build();

        return PostResponseDto.builder()
                .postId(post.getId())
                .creatorName(post.getCreator().getName())
                .title(post.getTitle())
                .orderAt(post.getOrderAt())
                .recruitment(post.getRecruitment())
                .location(pointResponseDto)
                .foodCategory(post.getFoodCategory())
                .build();
    }


    /**
     * 위도, 경도를 받아 Point 객체를 반환하는 메소드
     *
     * @param latitude
     * @param longitude
     * @return
     */
    private Point getPoint(double latitude, double longitude) {
        Point point = geometryFactory.createPoint(new Coordinate(latitude, longitude));

        return point;
    }
}
