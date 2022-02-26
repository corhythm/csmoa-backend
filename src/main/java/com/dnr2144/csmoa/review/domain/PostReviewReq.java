package com.dnr2144.csmoa.review.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@ToString
@RequiredArgsConstructor
public class PostReviewReq {

    private final List<MultipartFile> reviewImages;
    private final String title;
    private final Integer price;
    private final Float rating;
    private final String category;
    private final String csBrand;
    private final String content;
}





