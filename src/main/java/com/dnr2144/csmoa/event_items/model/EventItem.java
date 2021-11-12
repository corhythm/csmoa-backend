package com.dnr2144.csmoa.event_items.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EventItem {

    private final Long eventItemId;
    private final String itemName;
    private final Integer itemPrice;
    private final Integer itemActualPrice;
    private final String itemImageSrc;
    private final String itemCategory;
    private final String csBrand;
    private final String itemEventType;

    @Builder
    public EventItem(Long eventItemId, String itemName, Integer itemPrice, Integer itemActualPrice,
                     String itemImageSrc, String itemCategory, String csBrand, String itemEventType) {

        this.eventItemId = eventItemId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.itemActualPrice = itemActualPrice;
        this.itemImageSrc = itemImageSrc;
        this.itemCategory = itemCategory;
        this.csBrand = csBrand;
        this.itemEventType = itemEventType;
    }
}
