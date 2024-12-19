package be.pxl.services.service;

import be.pxl.services.domain.Comment;
import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;
import be.pxl.services.repository.CommentRepository;
import jakarta.ws.rs.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {

    private final CommentRepository commentRepository;

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
    }

    @Override
    public void deleteComment(Long commentId, String author) {
        Optional<Comment> optComment = commentRepository.findById(commentId);
        if (optComment.isPresent()) {
            Comment comment = optComment.get();
            if (!comment.getAuthor().equals(author)) {
                throw new BadRequestException("Comment is not made by author: " + author);
            }
            commentRepository.deleteById(commentId);
        } else {
            throw new BadRequestException("Comment was not found");
        }
    }

    @Override
    public void updateComment(UpdateCommentRequest updateCommentRequest, String author) {
        Optional<Comment> optComment = commentRepository.findById(updateCommentRequest.getCommentId());
        if (optComment.isPresent()) {
            Comment comment = optComment.get();
            if (!comment.getAuthor().equals(author)) {
                throw new BadRequestException("Comment is not made by author: " + author);
            }
            comment.setDescription(updateCommentRequest.getDescription());
            commentRepository.save(comment);
        } else {
            throw new BadRequestException("Comment was not found");
        }
    }
}
