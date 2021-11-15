package com.dnr2144.csmoa.event_items.domain;

import com.dnr2144.csmoa.event_items.model.EventItem;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@ToString
@Getter
public class GetDetailEventItemRes {
    private final EventItem detailEventItem;
    private final List<EventItem> detailRecommendedEventItems;

    @Builder
    public GetDetailEventItemRes(EventItem detailEventItem, List<EventItem> detailRecommendedEventItems) {
        this.detailEventItem = detailEventItem;
        this.detailRecommendedEventItems = detailRecommendedEventItems;
    }
}
