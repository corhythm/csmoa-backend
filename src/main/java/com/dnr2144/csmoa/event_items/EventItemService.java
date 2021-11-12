package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.event_items.model.EventItem;
import com.dnr2144.csmoa.event_items.model.GetEventItemsRes;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventItemService {

    private final EventItemRepository eventItemRepository;

    public GetEventItemsRes getEventItems(int pageNum) throws BaseException {
        return eventItemRepository.getEventItems(pageNum);
    }

    public List<EventItem> getEventItem() throws BaseException {
        return eventItemRepository.getEventItem();
    }
}
