package be.pxl.services.service;

import be.pxl.services.domain.Post;
import be.pxl.services.domain.dto.*;
import be.pxl.services.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public void publishDraft(DraftRequest draftRequest) {
        Optional<Post> optPost = postRepository.findById(draftRequest.getId());
        if (optPost.isPresent()) {
            Post post = optPost.get();
            post.setDraft(false);
            post.setPublicationDate(LocalDateTime.now());
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
}
