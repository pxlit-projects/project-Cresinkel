package be.pxl.services.controller;

import be.pxl.services.domain.dto.*;
import be.pxl.services.service.IPostService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PostController {

    private final IPostService postService;
    private static final Logger log = LoggerFactory.getLogger(PostController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody PostRequest postRequest,
                           @RequestHeader("Role") String roleHeader) {
        log.info("Received request to create post: {}", postRequest);

        if (!"redacteur".equalsIgnoreCase(roleHeader)) {
            String error = "You do not have permission to create posts.";
            log.warn(error);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, error);
        }

        postService.createPost(postRequest);
        log.info("Successfully created post: {}", postRequest);
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getDrafts(@RequestBody DraftsRequest draftsRequest,
                                                        @RequestHeader("Role") String roleHeader) {
        log.info("Received request to get drafts: {}", draftsRequest);
        validateRole(roleHeader);

        ResponseEntity<List<PostResponse>> response = postService.getDrafts(draftsRequest);
        log.info("Successfully retrieved drafts for request: {}", draftsRequest);
        return response;
    }

    @PostMapping("/sendInDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendInDraft(@RequestBody DraftRequest draftRequest,
                            @RequestHeader("Role") String roleHeader) {
        log.info("Received request to send draft for review: {}", draftRequest);
        validateRole(roleHeader);

        postService.sendForReview(draftRequest);
        log.info("Successfully sent draft for review: {}", draftRequest);
    }

    @PostMapping("/editDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void editDraft(@RequestBody EditDraftRequest editDraftRequest,
                          @RequestHeader("Role") String roleHeader) {
        log.info("Received request to edit draft: {}", editDraftRequest);
        validateRole(roleHeader);

        postService.editDraft(editDraftRequest);
        log.info("Successfully edited draft: {}", editDraftRequest);
    }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PostResponse> getDraft(@RequestBody DraftRequest draftRequest,
                                                 @RequestHeader("Role") String roleHeader) {
        log.info("Received request to get a single draft: {}", draftRequest);
        validateRole(roleHeader);

        ResponseEntity<PostResponse> response = postService.getDraft(draftRequest);
        log.info("Successfully retrieved draft: {}", draftRequest);
        return response;
    }

    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getPosts() {
        log.info("Received request to get all posts.");
        ResponseEntity<List<PostResponse>> response = postService.getPosts();
        log.info("Successfully retrieved all posts.");
        return response;
    }

    private void validateRole(String roleHeader) {
        if (!"gebruiker".equalsIgnoreCase(roleHeader) && !"redacteur".equalsIgnoreCase(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have the required role to access this resource.");
        }
    }
}
