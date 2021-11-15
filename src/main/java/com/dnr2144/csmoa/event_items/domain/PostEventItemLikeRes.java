package com.dnr2144.csmoa.event_items.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
public class PostEventItemLikeRes {

    private final Long userId;
    private final Long eventItemId;
    private final Boolean isLike;

    @Builder
    public PostEventItemLikeRes(Long userId, Long eventItemId, Boolean isLike) {
        this.userId = userId;
        this.eventItemId = eventItemId;
        this.isLike = isLike;
    }
}
