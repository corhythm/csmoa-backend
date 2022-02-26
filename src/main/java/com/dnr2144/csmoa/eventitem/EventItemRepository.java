package com.dnr2144.csmoa.eventitem;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.eventitem.domain.GetDetailEventItemRes;
import com.dnr2144.csmoa.eventitem.domain.PostEventItemLikeRes;
import com.dnr2144.csmoa.eventitem.model.EventItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class EventItemRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }


    // NOTE: 추천 행사 상품 받아오기
    public List<EventItem> getRecommendedEventItems(long userId, List<String> csBrands,
                                                    List<String> eventTypes, List<String> categories) throws BaseException {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("csBrands", csBrands);
            params.put("eventTypes", eventTypes);
            params.put("categories", categories);
            params.put("pageNum", 0); // new Random().nextInt(10)
            params.put("pageSize", 10);

            return namedParameterJdbcTemplate.query(EventItemSqlQuery.GET_RECOMMENDED_EVENT_ITEMS,
                    params, (rs, row) -> (EventItem.builder()
                            .eventItemId(rs.getLong("event_item_id"))
                            .itemName(rs.getString("item_name"))
                            .itemPrice(rs.getString("item_price"))
                            .itemActualPrice(rs.getString("item_actual_price"))
                            .itemImageSrc(rs.getString("item_image_src"))
                            .itemCategory(rs.getString("item_category"))
                            .csBrand(rs.getString("cs_brand"))
                            .itemEventType(rs.getString("item_event_type"))
                            .viewCount(rs.getInt("view_count"))
                            .likeCount(rs.getInt("like_count"))
                            .isLike(rs.getBoolean("is_like"))
                            .build()));

        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 일반 행사 상품 받아오기
    public List<EventItem> getEventItems(long userId, int pageNum, List<String> csBrands,
                                         List<String> eventTypes, List<String> categories) throws BaseException {

        try {
            // 이벤트 아이템 리스트 전달
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("csBrands", csBrands);
            params.put("eventTypes", eventTypes);
            params.put("categories", categories);
            params.put("pageNum", (pageNum - 1) * 10); // new Random().nextInt(10)
            params.put("pageSize", 10);

            return namedParameterJdbcTemplate.query(EventItemSqlQuery.GET_EVENT_ITEMS,
                    params, (rs, row) -> (EventItem.builder()
                            .eventItemId(rs.getLong("event_item_id"))
                            .itemName(rs.getString("item_name"))
                            .itemPrice(rs.getString("item_price"))
                            .itemActualPrice(rs.getString("item_actual_price"))
                            .itemImageSrc(rs.getString("item_image_src"))
                            .itemCategory(rs.getString("item_category"))
                            .csBrand(rs.getString("cs_brand"))
                            .itemEventType(rs.getString("item_event_type"))
                            .viewCount(rs.getInt("view_count"))
                            .likeCount(rs.getInt("like_count"))
                            .isLike(rs.getBoolean("is_like"))
                            .build()));

        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 세부화면 하단에 추천 상품 가져오기
    public GetDetailEventItemRes getDetailRecommendedEventItem(long userId, long eventItemId) throws BaseException {
        try {

            Integer itemPrice = jdbcTemplate.queryForObject("SELECT item_price FROM event_items " +
                    "WHERE event_item_id = ?", Integer.class, eventItemId);

            if (itemPrice == null) {
                throw new BaseException(BaseResponseStatus.INVALID_EVENT_ITEM_ERROR);
            }

            EventItem detailEventItem = jdbcTemplate.queryForObject(EventItemSqlQuery.GET_DETAIL_EVENT_ITEM,
                    (rs, row) -> EventItem.builder()
                            .eventItemId(rs.getLong("event_item_id"))
                            .itemName(rs.getString("item_name"))
                            .itemPrice(rs.getString("item_price"))
                            .itemActualPrice(rs.getString("item_actual_price"))
                            .itemImageSrc(rs.getString("item_image_src"))
                            .itemCategory(rs.getString("item_category"))
                            .csBrand(rs.getString("cs_brand"))
                            .itemEventType(rs.getString("item_event_type"))
                            .viewCount(rs.getInt("view_count"))
                            .likeCount(rs.getInt("like_count"))
                            .isLike(rs.getBoolean("is_like"))
                            .build(), userId, eventItemId);

            List<EventItem> detailRecommendEventItem = jdbcTemplate.query(EventItemSqlQuery.GET_DETAIL_RECOMMENDED_EVENT_ITEMS,
                    (rs, row) -> (EventItem.builder()
                            .eventItemId(rs.getLong("event_item_id"))
                            .itemName(rs.getString("item_name"))
                            .itemPrice(rs.getString("item_price"))
                            .itemActualPrice(rs.getString("item_actual_price"))
                            .itemImageSrc(rs.getString("item_image_src"))
                            .itemCategory(rs.getString("item_category"))
                            .csBrand(rs.getString("cs_brand"))
                            .itemEventType(rs.getString("item_event_type"))
                            .build()), eventItemId, itemPrice - 938, itemPrice + 1928);

            return GetDetailEventItemRes.builder()
                    .detailEventItem(detailEventItem)
                    .detailRecommendedEventItems(detailRecommendEventItem)
                    .build();

        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 조회수 post
    public boolean postEventItemHistory(long userId, long eventItemId) throws BaseException {
        try {
            String postEventItemHistoryQuery = "INSERT INTO event_item_histories (user_id, event_item_id) VALUE (?, ?);";
            jdbcTemplate.update(postEventItemHistoryQuery, userId, eventItemId);
            return true;
        } catch (Exception exception) {
            log.error("postEventItemHistory / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 좋아요 post
    public PostEventItemLikeRes postEventItemLike(long userId, long eventItemId) throws BaseException {
        // 이건 좋아요 이력이 있는지 확인하고 없으면 insert 있으면, update해야 함
        try {
            // 만약 좋아요 한 이력이 없으면 -> 좋아요 추가
            Boolean isLike = getEventItemLike(userId, eventItemId);
            // 좋아요한 이력이 없다면 -> 좋아요 추가
            if (isLike == null) {
                String insertEventItemLikeQuery = "INSERT INTO event_item_likes (user_id, event_item_id, is_like) VALUE (?, ?, ?);";
                jdbcTemplate.update(insertEventItemLikeQuery, userId, eventItemId, true);
                isLike = true;
            } else {
                // 좋아요 <-> 싫어요
                String updateEventItemLikeQuery = "";
                if (Boolean.TRUE.equals(isLike)) {  // 좋아요 -> 싫어요
                    updateEventItemLikeQuery = "UPDATE event_item_likes SET is_like = false, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND event_item_id = ?;";
                } else {  // 싫어요 -> 좋아요
                    updateEventItemLikeQuery = "UPDATE event_item_likes SET is_like = true, updated_at = CURRENT_TIMESTAMP WHERE user_id = ? AND event_item_id = ?;";
                }
                isLike = !isLike;
                jdbcTemplate.update(updateEventItemLikeQuery, userId, eventItemId);
            }

            return PostEventItemLikeRes.builder()
                    .userId(userId)
                    .eventItemId(eventItemId)
                    .isLike(isLike)
                    .build();

        } catch (Exception exception) {
            log.error("postEventItemHistory / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 행사 상품 있는지 조회
    public Integer checkEventItemExists(long eventItemId) throws BaseException {
        try {
            String checkEventItemExistsQuery = "SELECT EXISTS(SELECT * FROM event_items WHERE event_item_id = ?);";

            return jdbcTemplate.queryForObject(checkEventItemExistsQuery, Integer.class, eventItemId);
        } catch (Exception exception) {
            log.error("checkUserExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 행사 상품 좋아요 한 이력이 있는지, 있다면 가져오기
    public Boolean getEventItemLike(long userId, long eventItemId) throws BaseException {
        try {
            String checkEventItemLikeExistsQuery = "SELECT EXISTS(SELECT is_like FROM event_item_likes WHERE user_id = ? AND event_item_id = ?)";
            int isEventItemLikeExists = jdbcTemplate.queryForObject(checkEventItemLikeExistsQuery, Integer.class, userId, eventItemId);
            if (isEventItemLikeExists == 0) return null;

            String getEventItemLikeQuery = "SELECT is_like FROM event_item_likes WHERE user_id = ? AND event_item_id = ?;";
            return jdbcTemplate.queryForObject(getEventItemLikeQuery, Boolean.class, userId, eventItemId);
        } catch (Exception exception) {
            log.error("checkEventItemLikeExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}
