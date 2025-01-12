package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;
import be.pxl.services.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private final ICommentService commentService;
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody CommentRequest commentRequest,
                           @RequestHeader("Role") String roleHeader) {
        log.info("Creating Post: " + commentRequest);
        validateRole(roleHeader);

        commentService.createComment(commentRequest);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        log.info("Getting Comments for Post with id: " + postId);
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @PutMapping("/{author}")
    public void updateReviewStatus(@PathVariable String author,
                                   @RequestBody UpdateCommentRequest updateCommentRequest,
                                   @RequestHeader("Role") String roleHeader) {
        log.info("Updating Review status for Comment with id: " + updateCommentRequest.getCommentId() +
                ", Request: " + updateCommentRequest +
                ", Performed by: " + author);
        validateRole(roleHeader);

        commentService.updateComment(updateCommentRequest, author);
    }

    @DeleteMapping("/{commentId}/{author}")
    public void deleteComment(@PathVariable Long commentId,
                                   @PathVariable String author,
                                   @RequestHeader("Role") String roleHeader) {
        log.info("Deleting Comment with id: " + commentId +
                ", Performed by: " + author);
        validateRole(roleHeader);

        commentService.deleteComment(commentId, author);
    }

    private void validateRole(String roleHeader) {
        if (!"gebruiker".equalsIgnoreCase(roleHeader) && !"redacteur".equalsIgnoreCase(roleHeader)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have the required role to access this resource.");
        }
    }
}
