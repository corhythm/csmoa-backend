package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.event_items.domain.GetDetailEventItemRes;
import com.dnr2144.csmoa.event_items.domain.PostEventItemLikeRes;
import com.dnr2144.csmoa.event_items.model.EventItem;
import com.dnr2144.csmoa.event_items.query.EventItemSqlQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
@Slf4j
public class EventItemRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 추천 행사 상품 받아오기
    public List<EventItem> getRecommendedEventItems(Long userId) throws BaseException {

        try {
            return jdbcTemplate.query(EventItemSqlQuery.GET_RECOMMENDED_EVENT_ITEMS,
                    (rs, row) -> (EventItem.builder()
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
                            .build()), userId);

        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 일반 행사 상품 받아오기
    public List<EventItem> getEventItems(long userId, int pageNum) throws BaseException {

        try {
            // 이벤트 아이템 리스트 전달
            return jdbcTemplate.query(EventItemSqlQuery.GET_EVENT_ITEMS,
                    (rs, row) -> (EventItem.builder()
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
                            .build()), userId, 14 * pageNum);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 세부화면 하단에 추천 상품 가져오기
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
                            .build()), itemPrice - 938, itemPrice + 1928);

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

    // 조회수 post
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

    // 좋아요 post
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

    // 행사 상품 있는지 조회
    public Integer checkEventItemExists(long eventItemId) throws BaseException {
        try {
            String checkEventItemExistsQuery = "SELECT EXISTS(SELECT * FROM event_items WHERE event_item_id = ?);";
            ;
            return jdbcTemplate.queryForObject(checkEventItemExistsQuery, Integer.class, eventItemId);
        } catch (Exception exception) {
            log.error("checkUserExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 행사 상품 좋아요 한 이력이 있는지, 있다면 가져오기
    public Boolean getEventItemLike(long userId, long eventItemId) throws BaseException {
        try {
            String getEventItemLikeQuery = "SELECT is_like FROM event_item_likes WHERE user_id = ? AND event_item_id = ?;";
            ;
            return jdbcTemplate.queryForObject(getEventItemLikeQuery, Boolean.class, userId, eventItemId);
        } catch (Exception exception) {
            log.error("checkEventItemLikeExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}
