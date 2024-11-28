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
        System.out.println(postRequest);
        postService.createPost(postRequest);
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getDrafts(@RequestBody DraftsRequest draftsRequest) {
        System.out.println(draftsRequest);
        return postService.getDrafts(draftsRequest);
    }

    @PostMapping("/publishDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void publishDraft(@RequestBody DraftRequest draftRequest) {
        System.out.println(draftRequest);
        postService.publishDraft(draftRequest);
    }

    @PostMapping("/editDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void editDraft(@RequestBody EditDraftRequest editDraftRequest) {
        System.out.println(editDraftRequest);
        postService.editDraft(editDraftRequest);
    }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PostResponse> getDraft(@RequestBody DraftRequest draftRequest) {
        System.out.println(draftRequest);
        return postService.getDraft(draftRequest);
    }

    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getPosts() {
        return postService.getPosts();
    }
}
