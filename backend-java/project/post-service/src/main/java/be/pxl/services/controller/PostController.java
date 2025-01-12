package be.pxl.services.controller;

import be.pxl.services.domain.dto.*;
import be.pxl.services.service.IPostService;
import lombok.RequiredArgsConstructor;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody PostRequest postRequest,
                           @RequestHeader("Role") String roleHeader) {
        System.out.println("Creating post: " + postRequest);

        if (!"redacteur".equalsIgnoreCase(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to create posts.");
        }

        postService.createPost(postRequest);
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getDrafts(@RequestBody DraftsRequest draftsRequest,
                                                        @RequestHeader("Role") String roleHeader) {
        System.out.println("Getting Drafts: " + draftsRequest);

        validateRole(roleHeader);

        return postService.getDrafts(draftsRequest);
    }

    @PostMapping("/sendInDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void sendInDraft(@RequestBody DraftRequest draftRequest,
                            @RequestHeader("Role") String roleHeader) {
        System.out.println("Sending in draft: " + draftRequest);

        validateRole(roleHeader);

        postService.sendForReview(draftRequest);
    }

    @PostMapping("/editDraft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void editDraft(@RequestBody EditDraftRequest editDraftRequest,
                          @RequestHeader("Role") String roleHeader) {
        System.out.println("Editing Draft: " + editDraftRequest);

        validateRole(roleHeader);

        postService.editDraft(editDraftRequest);
    }

    @PostMapping("/draft")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PostResponse> getDraft(@RequestBody DraftRequest draftRequest,
                                                 @RequestHeader("Role") String roleHeader) {
        System.out.println("Getting 1 Draft: " + draftRequest);

        validateRole(roleHeader);

        return postService.getDraft(draftRequest);
    }

    @GetMapping("/posts")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<List<PostResponse>> getPosts() {
        System.out.println("Getting Posts");
        return postService.getPosts();
    }

    private void validateRole(String roleHeader) {
        if (!"gebruiker".equalsIgnoreCase(roleHeader) && !"redacteur".equalsIgnoreCase(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have the required role to access this resource.");
        }
    }
}
