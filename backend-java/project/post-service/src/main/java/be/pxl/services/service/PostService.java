package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.DraftsRequest;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PublishDraftRequest;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

    private final PostRepository postRepository;

    @Override
    public void createPost(PostRequest postRequest) {
        System.out.println(postRequest.isDraft());
        Post newPost = Post.builder()
                .title(postRequest.getTitle())
                .description(postRequest.getDescription())
                .author(postRequest.getAuthor())
                .isDraft(postRequest.isDraft())
                .publicationDate(LocalDateTime.now())
                .build();
        postRepository.save(newPost);
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
    public void publishDraft(PublishDraftRequest publishDraftRequest) {
        Optional<Post> optPost = postRepository.findById(publishDraftRequest.getId());
        if (optPost.isPresent()) {
            Post post = optPost.get();
            post.setDraft(false);
            postRepository.save(post);
        }
    }

    @Override
    public ResponseEntity<List<PostResponse>> getPosts() {
        List<PostResponse> response = postRepository.findAll().stream()
                .filter(p -> !p.isDraft())
                .map(PostResponse::mapToPostResponse)
                .toList();
        System.out.println(response);
        return ResponseEntity.ok(response);
    }
}
