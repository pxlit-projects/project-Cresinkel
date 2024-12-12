package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.*;
import be.pxl.services.repository.PostRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void createPost(PostRequest postRequest) {
        Post newPost = Post.builder()
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .author(postRequest.getAuthor())
                .isDraft(postRequest.isDraft())
                .publicationDate(LocalDateTime.now())
                .build();
        postRepository.save(newPost);
        if (!newPost.isDraft()) {
            sendForAcceptation(newPost.getId());
        }
    }

    @Override
    public ResponseEntity<List<PostResponse>> getDrafts(DraftsRequest draftsRequest) {
        System.out.println(draftsRequest);
        List<PostResponse> response = postRepository.findAll().stream()
                .filter(p -> p.getAuthor().equals(draftsRequest.getAuthor()) && p.isDraft())
                .map(PostResponse::mapToPostResponse)
                .toList();
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @Override
    public void sendForReview(DraftRequest draftRequest) {
        System.out.println("service call");
        Optional<Post> optPost = postRepository.findById(draftRequest.getId());
        if (optPost.isPresent()) {
            System.out.println("post is present");
            Post post = optPost.get();
            post.setDraft(false);
            postRepository.save(post);
            sendForAcceptation(post.getId());
        }
    }

    @Override
    public ResponseEntity<List<PostResponse>> getPosts() {
        List<PostResponse> response = postRepository.findAll().stream()
                .filter(p -> !p.isDraft() && p.isAccepted())
                .map(PostResponse::mapToPostResponse)
                .toList();
        System.out.println(response);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<PostResponse> getDraft(DraftRequest draftRequest) {
        Optional<PostResponse> optResponse = postRepository.findById(draftRequest.getId()).map(PostResponse::mapToPostResponse);
        if (optResponse.isPresent()) {
            PostResponse response = optResponse.get();
            return ResponseEntity.ok(response);
        }
        return null;
    }

    @Override
    public void editDraft(EditDraftRequest editDraftRequest) {
        Optional<Post> optPost = postRepository.findById(editDraftRequest.getId());
        if (optPost.isPresent()) {
            Post post = optPost.get();
            post.setTitle(editDraftRequest.getTitle());
            post.setDescription(editDraftRequest.getDescription());
            post.setLastEditedDate(LocalDateTime.now());
            postRepository.save(post);
        }
    }

    @Override
    public String getUserOfPost(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        return post.getAuthor();
    }

    @RabbitListener(queues = "ReviewQueue")
    public void getReview(ReviewResponse reviewResponse) {
        Post post = postRepository.findById(reviewResponse.getId()).orElseThrow();
        post.setAccepted(reviewResponse.isAccepted());
        post.setRejectionReason(reviewResponse.getRejectionReason());
        if (reviewResponse.isAccepted()) {
            post.setPublicationDate(LocalDateTime.now());
        } else {
            post.setDraft(true);
        }
        postRepository.save(post);
    }

    public void sendForAcceptation(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow();
        if (post.isAccepted()) {
            throw new BadRequestException("Post is already accepted");
        } else  if (post.isDraft()) {
            throw new BadRequestException("Post is a draft");
        } else {
            System.out.println("SENDING post to review");
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
        }
    }
}
