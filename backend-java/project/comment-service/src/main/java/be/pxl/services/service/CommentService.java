package be.pxl.services.service;

import be.pxl.services.controller.CommentController;
import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;
    private static final Logger log = LoggerFactory.getLogger(ICommentService.class);

    @Override
    public List<CommentResponse> getCommentsForPost(Long postId) {
        return commentRepository
                .findByPostId(postId)
                .stream()
                .map(CommentResponse::mapToCommentResponse)
                .toList();
    }

    @Override
    public void createComment(CommentRequest commentRequest) {
        Comment comment = Comment.builder()
                .description(commentRequest.getDescription())
                .author(commentRequest.getAuthor())
                .postId(commentRequest.getPostId())
                .build();
        commentRepository.save(comment);
        log.info("Create Comment: " + comment);
    }

    @Override
    public void deleteComment(Long commentId, String author) {
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if (optComment.isPresent()) {
            Comment comment = optComment.get();
            if (!comment.getAuthor().equals(author)) {
                String error = "Comment is not made by author: " + author;
                log.error(error);
                throw new IllegalArgumentException(error);
            }
            commentRepository.deleteById(commentId);
            log.info("Deleted Comment with id: " + commentId);
        } else {
            String error = "Comment with id: " + commentId + " was not found when trying to delete it.";
            log.error(error);
            throw new IllegalArgumentException(error);
        }
    }

    @Override
    public void updateComment(UpdateCommentRequest updateCommentRequest, String author) {
        Optional<Comment> optComment = commentRepository.findById(updateCommentRequest.getCommentId());

        if (optComment.isPresent()) {
            Comment comment = optComment.get();
            log.debug("Found comment: {}", comment);

            if (!comment.getAuthor().equals(author)) {
                String error = "Comment is not made by author: " + author;
                log.error(error);
                throw new IllegalArgumentException(error);
            }

            comment.setDescription(updateCommentRequest.getDescription());
            commentRepository.save(comment);
            log.info("Updated comment: {}", comment);
        } else {
            String error = "Comment with ID: " + updateCommentRequest.getCommentId() + " was not found";
            log.error(error);
            throw new IllegalArgumentException(error);
        }
    }
}
