package be.pxl.services.controller;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;
import be.pxl.services.service.ICommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class CommentController {
    private final ICommentService commentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createPost(@RequestBody CommentRequest commentRequest) {
        commentService.createComment(commentRequest);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<List<CommentResponse>> getCommentsByPostId(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @PutMapping("/{author}")
    public void updateReviewStatus(@PathVariable String author, @RequestBody UpdateCommentRequest updateCommentRequest) {
        commentService.updateComment(updateCommentRequest, author);
    }

    @DeleteMapping("/{commentId}/{author}")
    public void deleteNotification(@PathVariable Long commentId, @PathVariable String author) {
        commentService.deleteComment(commentId, author);
    }
}
