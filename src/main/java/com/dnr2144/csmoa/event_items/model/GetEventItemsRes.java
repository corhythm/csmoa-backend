package com.dnr2144.csmoa.event_items.model;

import lombok.Builder;
import lombok.Getter;

import java.awt.event.ItemEvent;
import java.util.List;

@Getter
public class GetEventItemsRes {
    private final List<EventItem> recommendedEventItemList;
    private final List<EventItem> eventItemList;

    @Builder
    public GetEventItemsRes(List<EventItem> recommendedEventItemList, List<EventItem> eventItemList) {
        this.recommendedEventItemList = recommendedEventItemList;
        this.eventItemList = eventItemList;
    }
}
