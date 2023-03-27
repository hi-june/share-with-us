package com.june.swu.domain.post.repository;

import com.june.swu.domain.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("select p from Post p where p.id = :id and p.isActive = true")
    Optional<Post> findPostById(@Param("id") Long id);
    @Query("select p from Post p where p.isActive is true")
    Page<Post> findPostsWithPagination(Pageable pageable);

    @Query("select p from Post p where p.isActive is true and p.title like %:keyword%")
    Page<Post> findPostsByKeywordWithPagination(Pageable pageable, @Param("keyword") String keyword);
}
