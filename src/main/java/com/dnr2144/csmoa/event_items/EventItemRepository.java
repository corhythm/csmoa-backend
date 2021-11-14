package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
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
            return this.jdbcTemplate.query(EventItemSqlQuery.GET_RECOMMENDED_EVENT_ITEMS,
                    (rs, row) -> new EventItem(
                            rs.getLong("event_item_id"),
                            rs.getString("item_name"),
                            rs.getInt("item_price"),
                            rs.getInt("item_actual_price"),
                            rs.getString("item_image_src"),
                            rs.getString("item_category"),
                            rs.getString("cs_brand"),
                            rs.getString("item_event_type")
                    ));
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
           return this.jdbcTemplate.query(EventItemSqlQuery.GET_EVENT_ITEMS,
                    (rs, row) -> new EventItem(
                            rs.getLong("event_item_id"),
                            rs.getString("item_name"),
                            rs.getInt("item_price"),
                            rs.getInt("item_actual_price"),
                            rs.getString("item_image_src"),
                            rs.getString("item_category"),
                            rs.getString("cs_brand"),
                            rs.getString("item_event_type")
                    ), 14 * pageNum);
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<EventItem> getDetailRecommendedEventItem(long userId, long eventItemId) throws BaseException {
        try {

            return this.jdbcTemplate.query(EventItemSqlQuery.GET_DETAIL_RECOMMENDED_EVENT_ITEMS,
                    (rs, row) -> new EventItem(
                            rs.getLong("event_item_id"),
                            rs.getString("item_name"),
                            rs.getInt("item_price"),
                            rs.getInt("item_actual_price"),
                            rs.getString("item_image_src"),
                            rs.getString("item_category"),
                            rs.getString("cs_brand"),
                            rs.getString("item_event_type")
                    ));
        } catch (Exception exception) {
            log.error(exception.getMessage());
            exception.printStackTrace();
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}
