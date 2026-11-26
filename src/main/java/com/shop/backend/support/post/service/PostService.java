
package com.shop.backend.support.post.service;

import com.shop.backend.support.post.dto.PostCreateRequestDto;
import com.shop.backend.support.post.dto.PostResponseDto;
import com.shop.backend.support.post.dto.NoticeListItemDto;
import com.shop.backend.support.post.dto.NoticeListResponseDto;
import com.shop.backend.support.post.dto.FaqListItemDto;
import com.shop.backend.support.post.dto.FaqListResponseDto;

import com.shop.backend.support.post.entity.BoardType;
import com.shop.backend.support.post.entity.FaqCategory;
import com.shop.backend.support.post.entity.Post;

import com.shop.backend.support.post.repository.PostRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    // 공지사항 목록 조회
    @Transactional(readOnly = true)
    public NoticeListResponseDto getNotices(int page) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Post> posts = postRepository.findByBoardType(
                BoardType.NOTICE,
                pageable
        );

        long totalCount = postRepository.countByBoardType(
                BoardType.NOTICE
        );

        List<NoticeListItemDto> notices = posts.stream()
                .map(NoticeListItemDto::new)
                .toList();

        return new NoticeListResponseDto(
                "ok",
                totalCount,
                notices
        );
    }

    // FAQ 목록 조회
    @Transactional(readOnly = true)
    public FaqListResponseDto getFaqs(
            FaqCategory faqCategory,
            int page
    ) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "페이지 번호는 0 이상이어야 합니다."
            );
        }

        int pageSize = 10;
        Pageable pageable = PageRequest.of(page, pageSize);

        List<Post> posts = postRepository.findFaqs(
                BoardType.FAQ,
                faqCategory,
                pageable
        );

        long totalCount = postRepository.countFaqs(
                BoardType.FAQ,
                faqCategory
        );

        List<FaqListItemDto> faqs = posts.stream()
                .map(FaqListItemDto::new)
                .toList();

        return new FaqListResponseDto(
                "ok",
                totalCount,
                faqs
        );
    }

    // 공지사항 상세 조회
    @Transactional(readOnly = true)
    public PostResponseDto getNoticeDetail(Integer noticeId) {

        Post post = postRepository.findById(noticeId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "존재하지 않는 공지사항입니다."
                        )
                );

        if (post.getBoard_type() != BoardType.NOTICE) {
            throw new IllegalArgumentException(
                    "공지사항이 아닙니다."
            );
        }

        return new PostResponseDto(
                post.getPost_id(),
                post.getBoard_type(),
                post.getFaq_category(),
                post.getTitle(),
                post.getContent(),
                post.getIs_pinned(),
                post.getCreated_at()
        );
    }

    // 공지사항 및 FAQ 작성
    @Transactional
    public PostResponseDto createPost(PostCreateRequestDto request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "게시글 정보를 입력해야 합니다."
            );
        }

        if (request.getBoard_type() == null) {
            throw new IllegalArgumentException(
                    "게시글 유형을 입력해야 합니다."
            );
        }

        if (request.getTitle() == null
                || request.getTitle().isBlank()) {
            throw new IllegalArgumentException(
                    "제목을 입력해야 합니다."
            );
        }

        if (request.getTitle().length() > 100) {
            throw new IllegalArgumentException(
                    "제목은 최대 100자까지 입력할 수 있습니다."
            );
        }

        if (request.getContent() == null
                || request.getContent().isBlank()) {
            throw new IllegalArgumentException(
                    "내용을 입력해야 합니다."
            );
        }

        // FAQ는 FAQ 분류 필수
        if (request.getBoard_type() == BoardType.FAQ
                && request.getFaq_category() == null) {
            throw new IllegalArgumentException(
                    "FAQ 분류를 선택해야 합니다."
            );
        }

        // NOTICE에는 FAQ 분류 지정 불가
        if (request.getBoard_type() == BoardType.NOTICE
                && request.getFaq_category() != null) {
            throw new IllegalArgumentException(
                    "공지사항에는 FAQ 분류를 지정할 수 없습니다."
            );
        }

        Post post = new Post();

        post.setBoard_type(request.getBoard_type());
        post.setFaq_category(request.getFaq_category());
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        post.setIs_pinned(
                request.getIs_pinned() != null
                        ? request.getIs_pinned()
                        : false
        );

        post.setCreated_at(LocalDateTime.now());

        Post savedPost = postRepository.save(post);

        return new PostResponseDto(
                savedPost.getPost_id(),
                savedPost.getBoard_type(),
                savedPost.getFaq_category(),
                savedPost.getTitle(),
                savedPost.getContent(),
                savedPost.getIs_pinned(),
                savedPost.getCreated_at()
        );
    }
}
