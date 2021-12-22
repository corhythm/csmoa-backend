package com.dnr2144.csmoa.eventitems;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.eventitems.domain.GetDetailEventItemRes;
import com.dnr2144.csmoa.eventitems.domain.PostEventItemHistoryAndLikeReq;
import com.dnr2144.csmoa.eventitems.domain.PostEventItemLikeRes;
import com.dnr2144.csmoa.eventitems.model.EventItem;
import com.dnr2144.csmoa.login.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventItemService {

    private final EventItemRepository eventItemRepository;
    private final UserRepository userRepository;

    // 추천 행사 상품 가져오기
    @Transactional
    public List<EventItem> getRecommendedEventItems(long userId, List<String> csBrands,
                                                    List<String> eventTypes, List<String> categories) throws BaseException {
        // 존재하는 사용자인지 체크
        if (userRepository.checkUserExists(userId) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return eventItemRepository.getRecommendedEventItems(userId, csBrands, eventTypes, categories);
    }

    // 행사 상품 가져오기
    @Transactional
    public List<EventItem> getEventItems(long userId, int pageNum, List<String> csBrands,
                                         List<String> eventTypes, List<String> categories) throws BaseException {
        // 존재하는 사용자인지 체크
        if (userRepository.checkUserExists(userId) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        return eventItemRepository.getEventItems(userId, pageNum, csBrands, eventTypes, categories);
    }

    // 세부 행사 제품 정보 + 추천 행사 상품
    public GetDetailEventItemRes getDetailRecommendedEventItems(long userId, long eventItemId) throws BaseException {
        // 존재하는 사용자인지 체크
        if (userRepository.checkUserExists(userId) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        // eventItemId 체크
        if (eventItemRepository.checkEventItemExists(eventItemId) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_EVENT_ITEM_ERROR);
        }
        return eventItemRepository.getDetailRecommendedEventItem(userId, eventItemId);
    }

    // 조회 post
    public Boolean postEventItemHistory(long userId, PostEventItemHistoryAndLikeReq postEventItemHistoryAndLikeReq) throws BaseException {
        if (userRepository.checkUserExists(userId) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        // null 체크
        if (postEventItemHistoryAndLikeReq == null || postEventItemHistoryAndLikeReq.getEventItemId() == null) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        // eventItemId 체크
        if (eventItemRepository.checkEventItemExists(postEventItemHistoryAndLikeReq.getEventItemId()) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_EVENT_ITEM_ERROR);
        }
        return eventItemRepository.postEventItemHistory(userId, postEventItemHistoryAndLikeReq.getEventItemId());
    }

    // 좋아요 post
    public PostEventItemLikeRes postEventItemLike(long userId, PostEventItemHistoryAndLikeReq postEventItemHistoryAndLikeReq) throws BaseException {
        if (userRepository.checkUserExists(userId) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_ACCOUNT_ERROR);
        }
        // null 체크
        if (postEventItemHistoryAndLikeReq == null || postEventItemHistoryAndLikeReq.getEventItemId() == null) {
            throw new BaseException(BaseResponseStatus.REQUEST_ERROR);
        }
        // eventItemId 체크
        if (eventItemRepository.checkEventItemExists(postEventItemHistoryAndLikeReq.getEventItemId()) != 1) {
            throw new BaseException(BaseResponseStatus.INVALID_EVENT_ITEM_ERROR);
        }
        return eventItemRepository.postEventItemLike(userId, postEventItemHistoryAndLikeReq.getEventItemId());
    }
}
