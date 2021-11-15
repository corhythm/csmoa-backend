package com.dnr2144.csmoa.event_items.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EventItem {

    private final Long eventItemId;
    private final String itemName;
    private final String itemPrice;
    private final String itemActualPrice;
    private final String itemImageSrc;
    private final String itemCategory;
    private final String csBrand;
    private final String itemEventType;
    private final Integer viewCount;
    private final Integer likeCount;
    private final Boolean isLike;

    @Builder
    public EventItem(Long eventItemId, String itemName, String itemPrice, String itemActualPrice, String itemImageSrc, String itemCategory, String csBrand, String itemEventType, Integer viewCount, Integer likeCount, Boolean isLike) {
        this.eventItemId = eventItemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemActualPrice = itemActualPrice;
        this.itemImageSrc = itemImageSrc;
        this.itemCategory = itemCategory;
        this.csBrand = csBrand;
        this.itemEventType = itemEventType;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.isLike = isLike;
    }
}
