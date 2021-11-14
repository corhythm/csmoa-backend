package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.event_items.model.EventItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventItemService {

    private final EventItemRepository eventItemRepository;

    public List<EventItem> getRecommendedEventItems(long userId) throws BaseException{
        return eventItemRepository.getRecommendedEventItems(userId);
    }

    public List<EventItem> getEventItems(long userId, int pageNum) throws BaseException {
        return eventItemRepository.getEventItems(userId, pageNum);
    }

    public List<EventItem> getDetailRecommendedEventItems(long userId, long eventItemId) throws BaseException {
        return eventItemRepository.getDetailRecommendedEventItem(userId, eventItemId);
    }
}
