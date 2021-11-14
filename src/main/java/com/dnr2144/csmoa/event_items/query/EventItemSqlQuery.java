package com.dnr2144.csmoa.event_items.query;

public class EventItemSqlQuery {

    public static String GET_RECOMMENDED_EVENT_ITEMS = "SELECT event_item_id, item_name, item_price," +
            " item_actual_price, item_image_src, item_category, cs_brand, item_event_type FROM event_items WHERE item_category != '생활용품' LIMIT 10;";

    public static String GET_EVENT_ITEMS = "SELECT event_item_id, item_name, item_price, item_actual_price, " +
            "item_image_src, item_category, cs_brand, item_event_type FROM event_items WHERE item_category != '생활용품' LIMIT ?, 14;";

    public static String GET_DETAIL_RECOMMENDED_EVENT_ITEMS = "SELECT event_item_id, item_name, item_price, item_actual_price," +
            " item_image_src, item_category, cs_brand, item_event_type FROM event_items WHERE item_category != '생활용품' LIMIT 20;";

}
