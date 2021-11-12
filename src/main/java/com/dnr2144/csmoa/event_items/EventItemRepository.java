package com.dnr2144.csmoa.event_items;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.event_items.model.EventItem;
import com.dnr2144.csmoa.event_items.model.GetEventItemsRes;
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


    public GetEventItemsRes getEventItems(int pageNum) throws BaseException {

        try {
            String getEventItemsQuery = "SELECT event_item_id, item_name, item_price, item_actual_price, item_image_src, item_category, cs_brand, item_event_type FROM event_items WHERE item_category != '생활용품' LIMIT 10;";
            String getEventItemsQuery2 = "SELECT event_item_id, item_name, item_price, item_actual_price, item_image_src, item_category, cs_brand, item_event_type FROM event_items WHERE item_category != '생활용품' LIMIT ?, 14;";
            List<EventItem> recommendedEventItemList = null;

            if (pageNum == 1) { // 첫 번째 요청에만 추천 리스트 전달
                recommendedEventItemList = this.jdbcTemplate.query(getEventItemsQuery,
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
            }

            // 이벤트 아이템
            List<EventItem> eventItemList = this.jdbcTemplate.query(getEventItemsQuery2,
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

            return GetEventItemsRes.builder()
                    .recommendedEventItemList(recommendedEventItemList)
                    .eventItemList(eventItemList)
                    .build();
        } catch (Exception exception) {
            log.error(exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<EventItem> getEventItem() throws BaseException {
        try {
            String getEventItemQuery = "SELECT event_item_id, item_name, item_price, item_actual_price, item_image_src, item_category, cs_brand, item_event_type FROM event_items WHERE item_category != '생활용품' LIMIT 20;";
            return this.jdbcTemplate.query(getEventItemQuery,
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
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }


}
