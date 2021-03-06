package com.dnr2144.csmoa.eventitem;

public class EventItemSqlQuery {

    // 추천 행사 상품 가져오기 (좋아요 개수 + 조회 수로 상위 10개)
    public static String GET_RECOMMENDED_EVENT_ITEMS =
            "SELECT event_items.event_item_id,\n" +
                    "       item_name,\n" +
                    "       CONCAT(FORMAT(item_price, 0), '원') AS item_price,\n" +
                    "       CONCAT('(개당 ', FORMAT(item_actual_price, 0), '원)') AS item_actual_price,\n" +
                    "       item_image_url,\n" +
                    "       item_category,\n" +
                    "       cs_brand,\n" +
                    "       item_event_type,\n" +
                    "       event_item_histories.view_count,\n" +
                    "       event_item_likes.like_count,\n" +
                    "       (SELECT temp_event_item_likes.is_like\n" +
                    "        FROM event_item_likes as temp_event_item_likes\n" +
                    "        WHERE temp_event_item_likes.event_item_id = event_items.event_item_id\n" +
                    "          AND temp_event_item_likes.user_id = :userId)          AS is_like\n" +
                    "FROM event_items\n" +
                    "         LEFT JOIN (SELECT event_item_id, count(event_item_id) AS view_count\n" +
                    "                     FROM event_item_histories\n" +
                    "                     GROUP BY event_item_id) AS event_item_histories\n" +
                    "                    ON event_item_histories.event_item_id = event_items.event_item_id\n" +
                    "         LEFT JOIN (SELECT event_item_id, count(event_item_id) AS like_count\n" +
                    "                     FROM event_item_likes\n" +
                    "                     WHERE is_like = true\n" +
                    "                     GROUP BY event_item_id) AS event_item_likes\n" +
                    "                    ON event_item_likes.event_item_id = event_items.event_item_id\n" +
                    "WHERE event_items.item_category != '생활용품'\n" +
                    "  AND event_items.cs_brand IN (:csBrands)\n" +
                    "  AND event_items.item_event_type IN (:eventTypes)\n" +
                    "  AND event_items.item_category IN (:categories)" +
                    "ORDER BY  (event_item_likes.like_count + event_item_histories.view_count) DESC\n" +
                    "LIMIT :pageNum, :pageSize;";

    // 일반 행사 상품 가져오기
    public static String GET_EVENT_ITEMS = "SELECT event_items.event_item_id,\n" +
            "       item_name,\n" +
            "       CONCAT(FORMAT(event_items.item_price, 0), '원') AS item_price,\n" +
            "       CONCAT('(개당 ', FORMAT(event_items.item_actual_price, 0), '원)') as item_actual_price,\n" +
            "       item_image_url,\n" +
            "       item_category,\n" +
            "       cs_brand,\n" +
            "       item_event_type,\n" +
            "       event_item_histories.view_count,\n" +
            "       event_item_likes.like_count," +
            "       (SELECT temp_event_item_likes.is_like\n" +
            "        FROM event_item_likes as temp_event_item_likes\n" +
            "        WHERE temp_event_item_likes.event_item_id = event_items.event_item_id\n" +
            "          AND temp_event_item_likes.user_id = :userId)          AS is_like\n" +
            "FROM event_items\n" +
            "         LEFT JOIN (SELECT event_item_id, count(event_item_id) AS view_count\n" +
            "                     FROM event_item_histories\n" +
            "                     GROUP BY event_item_id) AS event_item_histories\n" +
            "                    ON event_item_histories.event_item_id = event_items.event_item_id\n" +
            "         LEFT JOIN (SELECT event_item_id, count(event_item_id) AS like_count\n" +
            "                     FROM event_item_likes\n" +
            "                     WHERE is_like = true\n" +
            "                     GROUP BY event_item_id) AS event_item_likes\n" +
            "                    ON event_item_likes.event_item_id = event_items.event_item_id\n" +
            "WHERE item_category != '생활용품'\n" +
            "  AND event_items.cs_brand IN (:csBrands)\n" +
            "  AND event_items.item_event_type IN (:eventTypes)\n" +
            "  AND event_items.item_category IN (:categories)" +
            "LIMIT :pageNum, :pageSize;";

    // 세부 화면 행사 제품 추천 리스트 가져오기
    public static String GET_DETAIL_EVENT_ITEM = "SELECT event_items.event_item_id,\n" +
            "       item_name,\n" +
            "       CONCAT(FORMAT(event_items.item_price, 0), '원')                 AS item_price,\n" +
            "       CONCAT('(개당 ', FORMAT(event_items.item_actual_price, 0), '원)') AS item_actual_price,\n" +
            "       item_image_url,\n" +
            "       item_category,\n" +
            "       cs_brand,\n" +
            "       item_event_type,\n" +
            "       view_count,\n" +
            "       like_count," +
            "       (SELECT temp_event_item_likes.is_like\n" +
            "        FROM event_item_likes as temp_event_item_likes\n" +
            "        WHERE temp_event_item_likes.event_item_id = event_items.event_item_id\n" +
            "          AND temp_event_item_likes.user_id = ?)          AS is_like\n" +
            "FROM event_items\n" +
            "         LEFT JOIN (SELECT event_item_id, count(event_item_id) AS view_count\n" +
            "                     FROM event_item_histories\n" +
            "                     GROUP BY event_item_id) AS event_item_histories\n" +
            "                    ON event_item_histories.event_item_id = event_items.event_item_id\n" +
            "         LEFT JOIN (SELECT event_item_id, count(event_item_id) AS like_count\n" +
            "                     FROM event_item_likes\n" +
            "                     WHERE is_like = true\n" +
            "                     GROUP BY event_item_id) AS event_item_likes\n" +
            "                    ON event_item_likes.event_item_id = event_items.event_item_id\n" +
            "WHERE event_items.event_item_id = ?;";

    // 세부 화면 행사 제품 정보 하단 추천 행사상품 가져오기
    public static String GET_DETAIL_RECOMMENDED_EVENT_ITEMS = "SELECT event_items.event_item_id,\n" +
            "       item_name,\n" +
            "       CONCAT(FORMAT(event_items.item_price, 0), '원')                 AS item_price,\n" +
            "       CONCAT('(개당 ', FORMAT(event_items.item_actual_price, 0), '원)') AS item_actual_price,\n" +
            "       item_image_url,\n" +
            "       item_category,\n" +
            "       cs_brand,\n" +
            "       item_event_type\n" +
            "FROM event_items\n" +
            "WHERE event_items.event_item_id != ? AND item_category != '생활용품' AND item_price BETWEEN ? AND ?\n" +
            "LIMIT 20;";

}
