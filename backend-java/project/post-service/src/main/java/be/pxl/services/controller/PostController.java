package be.pxl.services.controller;

import be.pxl.services.domain.dto.*;
import be.pxl.services.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final IPostService postService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody PostRequest postRequest) {
        System.out.println("Creating post: " + postRequest);
        postService.createPost(postRequest);
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getDrafts(@RequestBody DraftsRequest draftsRequest) {
        System.out.println("Getting Drafts: " + draftsRequest);
        return postService.getDrafts(draftsRequest);
    }

    @PostMapping("/sendInDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendInDraft(@RequestBody DraftRequest draftRequest) {
        System.out.println("Sending in draft: " + draftRequest);
        postService.sendForReview(draftRequest);
    }

    @PostMapping("/editDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void editDraft(@RequestBody EditDraftRequest editDraftRequest) {
        System.out.println("Editing Draft: " + editDraftRequest);
        postService.editDraft(editDraftRequest);
    }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PostResponse> getDraft(@RequestBody DraftRequest draftRequest) {
        System.out.println("Getting 1 Draft: " + draftRequest);
        return postService.getDraft(draftRequest);
    }

    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getPosts() {
        System.out.println("Getting Posts");
        return postService.getPosts();
    }
}
