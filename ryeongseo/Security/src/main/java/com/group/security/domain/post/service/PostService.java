package com.group.security.domain.post.service;


import com.group.security.domain.post.dto.PostCreateRequest;
import com.group.security.domain.post.dto.PostResponse;
import com.group.security.domain.post.entity.Post;
import com.group.security.domain.post.repository.PostRepository;
import com.group.security.global.exception.ErrorCode;
import com.group.security.global.exception.ForbiddenException;
import com.group.security.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;

    @Transactional
    public PostResponse createPost(Long userId, PostCreateRequest request) {
        Post post = new Post(request.title(), request.content(), userId);
        return PostResponse.from(postRepository.save(post));
    }

    public List<PostResponse> getPosts() {
        return postRepository.findAll().stream()
                .map(PostResponse::from)
                .toList();
    }

    @Transactional
    public void deletePost(Long postId, Long userId, boolean isAdmin) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorCode.POST_NOT_FOUND));

        // 작성자 본인이거나 관리자라면 삭제 가능
        if (!post.isWrittenBy(userId) && !isAdmin) {
            throw new ForbiddenException(ErrorCode.POST_FORBIDDEN);
        }

        postRepository.delete(post);
    }
}
//
