package be.pxl.services.service;

import be.pxl.services.domain.dto.PostRequest;

public interface IPostService {
    void createPost(PostRequest postRequest);
}
