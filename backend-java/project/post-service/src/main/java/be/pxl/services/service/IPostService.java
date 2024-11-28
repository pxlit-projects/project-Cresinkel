package be.pxl.services.service;

import be.pxl.services.domain.dto.DraftsRequest;
import be.pxl.services.domain.dto.PostRequest;
import be.pxl.services.domain.dto.PostResponse;
import be.pxl.services.domain.dto.PublishDraftRequest;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IPostService {
    void createPost(PostRequest postRequest);

    ResponseEntity<List<PostResponse>> getDrafts(DraftsRequest draftsRequest);

    void publishDraft(PublishDraftRequest publishDraftRequest);

    ResponseEntity<List<PostResponse>> getPosts();
}
