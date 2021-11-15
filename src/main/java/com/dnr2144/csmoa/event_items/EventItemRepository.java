package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.event_items.domain.GetDetailEventItemRes;
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
                            .build()));

        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // 일반 행사 상품 받아오기
    public List<EventItem> getEventItems(long userId, int pageNum) throws BaseException {

        try {

            // 이벤트 아이템 리스트 전달달
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
                            .build()), 14 * pageNum);
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
                            .build(), eventItemId);

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
                            .build()), itemPrice + 1000, itemPrice + 2000);

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


}
