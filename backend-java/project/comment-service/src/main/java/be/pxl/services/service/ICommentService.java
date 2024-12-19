package be.pxl.services.service;

import be.pxl.services.domain.dto.CommentRequest;
import be.pxl.services.domain.dto.CommentResponse;
import be.pxl.services.domain.dto.UpdateCommentRequest;

import java.util.List;

public interface ICommentService {
    List<CommentResponse> getCommentsForPost(Long postId);
    void createComment(CommentRequest commentRequest);
    void deleteComment(Long commentId, String author);
    void updateComment(UpdateCommentRequest updateCommentRequest, String author);
}
