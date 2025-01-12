package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.*;
import be.pxl.services.repository.PostRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(PostService.class);

    @Override
    public void createPost(PostRequest postRequest) {
        log.info("Creating post: {}", postRequest);
        Post newPost = Post.builder()
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .author(postRequest.getAuthor())
                .isDraft(postRequest.isDraft())
                .publicationDate(LocalDateTime.now())
                .build();
        postRepository.save(newPost);
        log.info("Post created: {}", newPost);

        if (!newPost.isDraft()) {
            log.info("Sending post for acceptation: {}", newPost.getId());
            sendForAcceptation(newPost.getId());
        }
    }

    @Override
    public ResponseEntity<List<PostResponse>> getDrafts(DraftsRequest draftsRequest) {
        log.info("Fetching drafts for request: {}", draftsRequest);
        List<PostResponse> response = postRepository.findAll().stream()
                .filter(p -> p.getAuthor().equals(draftsRequest.getAuthor()) && p.isDraft())
                .map(PostResponse::mapToPostResponse)
                .toList();
        log.info("Drafts fetched: {}", response);
        return ResponseEntity.ok(response);
    }

    @Override
    public void sendForReview(DraftRequest draftRequest) {
        log.info("Sending draft for review: {}", draftRequest);
        Optional<Post> optPost = postRepository.findById(draftRequest.getId());

        if (optPost.isPresent()) {
            Post post = optPost.get();
            log.debug("Post found for review: {}", post);

            post.setDraft(false);
            postRepository.save(post);
            log.info("Draft updated to non-draft: {}", post);
            sendForAcceptation(post.getId());
        } else {
            log.warn("Post not found for review: {}", draftRequest.getId());
        }
    }

    @Override
    public ResponseEntity<List<PostResponse>> getPosts() {
        log.info("Fetching all posts.");
        List<PostResponse> response = postRepository.findAll().stream()
                .filter(p -> !p.isDraft() && p.isAccepted())
                .map(PostResponse::mapToPostResponse)
                .toList();
        log.info("Posts fetched: {}", response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PostResponse> getDraft(DraftRequest draftRequest) {
        log.info("Fetching draft with ID: {}", draftRequest.getId());
        Optional<PostResponse> optResponse = postRepository.findById(draftRequest.getId()).map(PostResponse::mapToPostResponse);

        if (optResponse.isPresent()) {
            log.info("Draft found: {}", optResponse.get());
            return ResponseEntity.ok(optResponse.get());
        } else {
            log.warn("Draft not found with ID: {}", draftRequest.getId());
            return null;
        }
    }

    @Override
    public void editDraft(EditDraftRequest editDraftRequest) {
        log.info("Editing draft with ID: {}", editDraftRequest.getId());
        Optional<Post> optPost = postRepository.findById(editDraftRequest.getId());

        if (optPost.isPresent()) {
            Post post = optPost.get();
            log.debug("Post found for editing: {}", post);

            post.setTitle(editDraftRequest.getTitle());
            post.setDescription(editDraftRequest.getDescription());
            post.setLastEditedDate(LocalDateTime.now());
            postRepository.save(post);
            log.info("Draft updated: {}", post);
        } else {
            log.warn("Post not found for editing: {}", editDraftRequest.getId());
        }
    }

    @Override
    public String getUserOfPost(Long postId) {
        log.info("Fetching author of post with ID: {}", postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("Post not found with ID: {}", postId);
            return new IllegalArgumentException("Post not found");
        });
        log.info("Author found: {}", post.getAuthor());
        return post.getAuthor();
    }

    @RabbitListener(queues = "ReviewQueue")
    public void getReview(ReviewResponse reviewResponse) {
        log.info("Processing review response: {}", reviewResponse);
        Post post = postRepository.findById(reviewResponse.getId()).orElseThrow(() -> {
            log.warn("Post not found for review ID: {}", reviewResponse.getId());
            return new IllegalArgumentException("Post not found");
        });

        post.setAccepted(reviewResponse.isAccepted());
        post.setRejectionReason(reviewResponse.getRejectionReason());

        if (reviewResponse.isAccepted()) {
            post.setPublicationDate(LocalDateTime.now());
        } else {
            post.setDraft(true);
        }

        postRepository.save(post);
        log.info("Review processed and post updated: {}", post);
    }

    public void sendForAcceptation(Long postId) {
        log.info("Sending post with ID: {} for acceptation", postId);
        Post post = postRepository.findById(postId).orElseThrow(() -> {
            log.warn("Post not found with ID: {}", postId);
            return new IllegalArgumentException("Post not found");
        });

        if (post.isAccepted()) {
            String error = "Post is already accepted";
            log.error(error);
            throw new IllegalArgumentException(error);
        } else if (post.isDraft()) {
            String error = "Post is a draft";
            log.error(error);
            throw new IllegalArgumentException(error);
        }

        log.info("Post is valid for review. Sending to review queue: {}", post);
        ReviewResponse reviewResponse = ReviewResponse.builder()
                .id(postId)
                .author(post.getAuthor())
                .title(post.getTitle())
                .lastEditedDate(post.getLastEditedDate())
                .publicationDate(post.getPublicationDate())
                .description(post.getDescription())
                .rejectionReason(post.getRejectionReason())
                .accepted(post.isAccepted())
                .build();
        rabbitTemplate.convertAndSend("ReviewQueue", reviewResponse);
        log.info("Post sent to review queue: {}", reviewResponse);
    }
}
