package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponse;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.event_items.domain.GetDetailEventItemRes;
import com.dnr2144.csmoa.event_items.domain.PostEventItemHistoryAndLikeReq;
import com.dnr2144.csmoa.event_items.domain.PostEventItemLikeRes;
import com.dnr2144.csmoa.event_items.model.EventItem;
import com.dnr2144.csmoa.util.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.Objects;

@Controller
@RequiredArgsConstructor
@Slf4j
public class EventItemController {

    private final EventItemService eventItemService;
    private final JwtService jwtService;

    // NOTE: 추천 행사 상품 불러오기
    @GetMapping("/recommended-event-items")
    @ResponseBody
    public BaseResponse<List<EventItem>> getRecommendedEventItems(@RequestHeader("Access-Token") String accessToken,
                                                                  @Nullable @RequestParam("cs-brand") List<String> csBrands,
                                                                  @Nullable @RequestParam("event-type") List<String> eventTypes,
                                                                  @Nullable @RequestParam("category") List<String> categories) {
        // accessToken is null
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/recommended-event-items / userId = " + userId);

            System.out.println("csBrands = " + csBrands);
            System.out.println("eventTypes = " + eventTypes);
            System.out.println("categories = " + categories);

            List<EventItem> recommendedEventItems
                    = eventItemService.getRecommendedEventItems(userId, csBrands, eventTypes, categories);
            log.info("recommendedEventItems = " + recommendedEventItems.toString());
            return new BaseResponse<>(recommendedEventItems);
        } catch (BaseException exception) {
            log.error("recommended-event-items: " + exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // NOTE: 메인 행사 상품 불러오기
    @GetMapping("/event-items")
    @ResponseBody
    public BaseResponse<List<EventItem>> getEventItems(@RequestHeader("Access-Token") String accessToken,
                                                       @RequestParam("page") int pageNum) {
        // accessToken is null
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/event-items?page=" + pageNum + " / userId = " + userId);
            return new BaseResponse<>(eventItemService.getEventItems(userId, pageNum));
        } catch (BaseException exception) {
            log.error("event-items: " + exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // NOTE: 특정 행사 상품 클릭 했을 때 -> 추천 행사 상품 리스트 전달
    @GetMapping("/event-items/{eventItemId}")
    @ResponseBody
    public BaseResponse<GetDetailEventItemRes> getDetailRecommendedEventItems(@RequestHeader("Access-Token") String accessToken,
                                                                              @PathVariable Long eventItemId) {
        // accessToken is null
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/event-items/{eventItemId} / userId = " + userId);
            GetDetailEventItemRes getDetailEventItemInfo
                    = eventItemService.getDetailRecommendedEventItems(userId, eventItemId);

            log.info(getDetailEventItemInfo.toString());
            return new BaseResponse<>(getDetailEventItemInfo);
        } catch (BaseException exception) {
            log.error("event-items: " + exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // NOTE: 조회수 Post
    @PostMapping("/event-items/history")
    @ResponseBody
    public BaseResponse<Boolean> postEventItemHistory(@RequestHeader("Access-Token") String accessToken,
                                                      @RequestBody PostEventItemHistoryAndLikeReq postEventItemHistoryAndLikeReq) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/event-items/history || postEventItemHistoryAndLikeReq = " + postEventItemHistoryAndLikeReq);
            Boolean isHistoryInsertSuccess = eventItemService.postEventItemHistory(userId, postEventItemHistoryAndLikeReq);
            return new BaseResponse<>(isHistoryInsertSuccess);
        } catch (BaseException exception) {
            exception.printStackTrace();
            log.error("postEventItemHistory: " + exception.getMessage());
            return new BaseResponse<>(exception.getStatus());
        }
    }

    // NOTE: 좋아요 Post
    @PostMapping("/event-items/like")
    @ResponseBody
    public BaseResponse<PostEventItemLikeRes> postEventItemLike(@RequestHeader("Access-Token") String accessToken,
                                                                @RequestBody PostEventItemHistoryAndLikeReq postEventItemHistoryAndLikeReq) {
        if (accessToken == null) {
            return new BaseResponse<>(BaseResponseStatus.EMPTY_JWT);
        }
        try {
            long userId = jwtService.getUserId(accessToken);
            log.info("/event-items/like || postEventItemHistoryAndLikeReq = " + postEventItemHistoryAndLikeReq);
            PostEventItemLikeRes postEventItemLikeRes = eventItemService.postEventItemLike(userId, postEventItemHistoryAndLikeReq);
            return new BaseResponse<>(postEventItemLikeRes);
        } catch (BaseException exception) {
            exception.printStackTrace();
            log.error("postEventItemLike: " + exception.getMessage());
            exception.printStackTrace();
            return new BaseResponse<>(exception.getStatus());
        }
    }

    @GetMapping("test")
    @ResponseBody
    public void temp(@Nullable @RequestParam("cs-brand") List<String> csBrand, @Nullable @RequestParam("event-type") List<String> eventType,
                     @Nullable @RequestParam List<String> category) {

        if (csBrand != null) {
            log.info(csBrand.toString());
        }

        if (eventType != null) {
            log.info(eventType.toString());
            if (eventType.contains("1+1"))
                log.info("contains 1+1");
        }

        if (category != null) {
            log.info(category.toString());
        }
    }


}

