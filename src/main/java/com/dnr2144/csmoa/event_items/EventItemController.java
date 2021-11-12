package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.event_items.model.EventItem;
import com.dnr2144.csmoa.event_items.model.GetEventItemsRes;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventItemController {

    private final EventItemService eventItemService;

    // TODO: 나중에 accessToken 받아야 함.
    // 이벤트 아이템 불러오기
    @GetMapping("/event-items")
    @ResponseBody
    public BaseResponse<GetEventItemsRes> getEventItems(@RequestParam("page") int pageNum) {

        log.info("/event-items?page=" + pageNum);
        try {
            return new BaseResponse<>(eventItemService.getEventItems(pageNum));
        } catch (BaseException exception) {
            log.error("event-items: " + exception.getStatus().toString());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // TODO: 나중에 accessToken 받아야 함
    // 특정 이벤트 아이템 클릭 했을 때 -> 추천 아이템 리스트 전달
     @GetMapping("/event-items/{eventItemId}")
     @ResponseBody
    public BaseResponse<List<EventItem>> getEventItem(@PathVariable Long eventItemId) {

        log.info("/event-items/{eventItemId}");
        try {

            List<EventItem> eventItemList = eventItemService.getEventItem();
            log.info(eventItemList.toString());
             return new BaseResponse<>(eventItemList);
         } catch (BaseException exception) {
             log.error("event-items: " + exception.getStatus().toString());
             return new BaseResponse<>(exception.getStatus());
         }
     }
}

