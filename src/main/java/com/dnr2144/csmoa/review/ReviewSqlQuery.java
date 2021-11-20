package com.dnr2144.csmoa.review;

public class ReviewSqlQuery {

//    public static String INSERT_REVIEW = "INSERT INTO reviews (user_id, item_name, item_price, " +
//            "item_star_score, cs_brand) VALUE (:userId, :itemName, :itemPrice, :itemStarScore, :csBrand)";

    public static String INSERT_REVIEW = "INSERT INTO reviews (user_id, item_name, item_price, " +
            "item_star_score, cs_brand, content) VALUE (?, ?, ?, ?, ?, ?)";

    public static String INSERT_REVIEW_IMAGES = "INSERT INTO review_images (review_id, image_src) value (:reviewId, :imageSrc)";
}
