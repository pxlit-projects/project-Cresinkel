package be.pxl.services.service;

import be.pxl.services.domain.dto.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPostService {
    void createPost(PostRequest postRequest);

    ResponseEntity<List<PostResponse>> getDrafts(DraftsRequest draftsRequest);

    void sendForReview(DraftRequest draftRequest);

    ResponseEntity<List<PostResponse>> getPosts();

    ResponseEntity<PostResponse> getDraft(DraftRequest draftRequest);

    void editDraft(EditDraftRequest editDraftRequest);

    String getUserOfPost(Long postId);
}
