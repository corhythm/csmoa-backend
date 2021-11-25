package com.dnr2144.csmoa.review;

public class ReviewSqlQuery {

//    public static String INSERT_REVIEW = "INSERT INTO reviews (user_id, item_name, item_price, " +
//            "item_star_score, cs_brand) VALUE (:userId, :itemName, :itemPrice, :itemStarScore, :csBrand)";

    public static String INSERT_REVIEW = "INSERT INTO reviews (user_id, item_name, item_price, " +
            "item_star_score, cs_brand, content) VALUE (?, ?, ?, ?, ?, ?)";

    public static String INSERT_REVIEW_IMAGES = "INSERT INTO review_images (review_id, image_src) value (:reviewId, :imageSrc)";

    public static String GET_BEST_REVIEWS =
            "SELECT reviews.review_id,\n" +
                    "       reviews.user_id,\n" +
                    "       reviews.item_name,\n" +
                    "       CONCAT(FORMAT(reviews.item_price, 0), '원') AS item_price,\n" +
                    "       reviews.item_star_score,\n" +
                    "       reviews.cs_brand,\n" +
                    "       reviews.content,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, reviews.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, reviews.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, reviews.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, reviews.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                             AS created_at,\n" +
                    "       review_likes.like_num,\n" +
                    "       review_views.view_num,\n" +
                    "       review_comments.comment_num,\n" +
                    "       review_images.image_src,\n" +
                    "       (SELECT temp_review_likes.is_like\n" +
                    "        FROM review_likes AS temp_review_likes\n" +
                    "        WHERE temp_review_likes.review_id = reviews.review_id\n" +
                    "          AND temp_review_likes.user_id = ?) AS is_like\n" +
                    "FROM reviews\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS like_num FROM review_likes GROUP BY review_id) AS review_likes\n" +
                    "                   ON review_likes.review_id = reviews.review_id\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS view_num FROM review_histories GROUP BY review_id) AS review_views\n" +
                    "                   ON review_views.review_id = reviews.review_id\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS comment_num\n" +
                    "                    FROM review_comments\n" +
                    "                    WHERE depth = 1\n" +
                    "                    GROUP BY review_id) AS review_comments\n" +
                    "                   ON review_comments.review_id = reviews.review_id\n" +
                    "         INNER JOIN (SELECT review_id, MAX(image_src) as image_src\n" +
                    "                     FROM review_images\n" +
                    "                     GROUP BY review_id) AS review_images\n" +
                    "                    ON review_images.review_id = reviews.review_id\n" +
                    "WHERE reviews.created_at BETWEEN DATE_ADD(NOW(), INTERVAL -30 DAY) AND NOW()\n" +
                    "ORDER BY (reviews.item_star_score + like_num + view_num + comment_num) DESC\n" +
                    "LIMIT ?, 15;";

    public static String GET_REVIEWS =
            "SELECT reviews.review_id,\n" +
                    "       reviews.user_id,\n" +
                    "       reviews.item_name,\n" +
                    "       CONCAT(FORMAT(reviews.item_price, 0), '원') AS item_price,\n" +
                    "       reviews.item_star_score,\n" +
                    "       reviews.cs_brand,\n" +
                    "       reviews.content,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, reviews.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, reviews.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, reviews.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, reviews.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                             AS created_at,\n" +
                    "       review_likes.like_num,\n" +
                    "       review_views.view_num,\n" +
                    "       review_comments.comment_num,\n" +
                    "       review_images.image_src,\n" +
                    "       (SELECT temp_review_likes.is_like\n" +
                    "        FROM review_likes AS temp_review_likes\n" +
                    "        WHERE temp_review_likes.review_id = reviews.review_id\n" +
                    "          AND temp_review_likes.user_id = ?) AS is_like\n" +
                    "FROM reviews\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS like_num FROM review_likes GROUP BY review_id) AS review_likes\n" +
                    "                   ON review_likes.review_id = reviews.review_id\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS view_num FROM review_histories GROUP BY review_id) AS review_views\n" +
                    "                   ON review_views.review_id = reviews.review_id\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS comment_num\n" +
                    "                    FROM review_comments\n" +
                    "                    WHERE depth = 1\n" +
                    "                    GROUP BY review_id) AS review_comments\n" +
                    "                   ON review_comments.review_id = reviews.review_id\n" +
                    "         INNER JOIN (SELECT review_id, MAX(image_src) as image_src\n" +
                    "                     FROM review_images\n" +
                    "                     GROUP BY review_id) AS review_images\n" +
                    "                    ON review_images.review_id = reviews.review_id\n" +
                    "WHERE reviews.created_at BETWEEN DATE_ADD(NOW(), INTERVAL -30 DAY) AND NOW()\n" +
                    "ORDER BY reviews.created_at desc\n" +
                    "LIMIT ?, 10";

    public static String GET_DETAILED_REVIEW =
            "SELECT reviews.review_id,\n" +
                    "       reviews.user_id,\n" +
                    "       reviews.item_name,\n" +
                    "       CONCAT(FORMAT(reviews.item_price, 0), '원') AS item_price,\n" +
                    "       reviews.item_star_score,\n" +
                    "       reviews.cs_brand,\n" +
                    "       reviews.content,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, reviews.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, reviews.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, reviews.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(reviews.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, reviews.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                                    AS created_at,\n" +
                    "       review_likes.like_num,\n" +
                    "       review_views.view_num,\n" +
                    "       review_comments.comment_num,\n" +
                    "       (SELECT temp_review_likes.is_like\n" +
                    "        FROM review_likes AS temp_review_likes\n" +
                    "        WHERE temp_review_likes.review_id = reviews.review_id\n" +
                    "          AND temp_review_likes.user_id = :userId)      AS is_like\n" +
                    "FROM reviews\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS like_num FROM review_likes GROUP BY review_id) AS review_likes\n" +
                    "                   ON review_likes.review_id = reviews.review_id\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS view_num FROM review_histories GROUP BY review_id) AS review_views\n" +
                    "                   ON review_views.review_id = reviews.review_id\n" +
                    "         LEFT JOIN (SELECT review_id, COUNT(*) AS comment_num\n" +
                    "                    FROM review_comments\n" +
                    "                    WHERE depth = 1\n" +
                    "                    GROUP BY review_id) AS review_comments\n" +
                    "                   ON review_comments.review_id = reviews.review_id\n" +
                    "WHERE reviews.review_id = :reviewId;";

    public static String GET_PARENT_COMMENTS =
            "SELECT review_comment_id,\n" +
                    "       review_comments.user_id,\n" +
                    "       users.nickname,\n" +
                    "       users.profile_image_url,\n" +
                    "       bundle_id,\n" +
                    "       comment_content,\n" +
                    "       (SELECT COUNT(*)\n" +
                    "        FROM review_comments AS child_comments\n" +
                    "        WHERE child_comments.bundle_id = review_comments.bundle_id\n" +
                    "          AND child_comments.depth = 0) AS nested_comment_num,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, review_comments.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(review_comments.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, review_comments.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(review_comments.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, review_comments.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(review_comments.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, review_comments.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END                          AS created_at,\n" +
                    "       review_comments.depth\n" +
                    "FROM review_comments\n" +
                    "         INNER JOIN users ON users.user_id = review_comments.user_id\n" +
                    "WHERE review_id = ? AND depth = 1\n" +
                    "LIMIT ?, 5";

    public static String GET_CHILD_COMMENTS =
            "SELECT review_comment_id,\n" +
                    "       review_comments.user_id,\n" +
                    "       users.nickname,\n" +
                    "       users.profile_image_url,\n" +
                    "       bundle_id,\n" +
                    "       comment_content,\n" +
                    "       CASE\n" +
                    "           WHEN (TIMESTAMPDIFF(YEAR, review_comments.created_at, NOW()) > 1) # 일 년 지나면\n" +
                    "               THEN DATE_FORMAT(review_comments.created_at, '%y.%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(HOUR, review_comments.created_at, NOW()) > 24) # 하루 지나면\n" +
                    "               THEN DATE_FORMAT(review_comments.created_at, '%m.%d %H:%i')\n" +
                    "           WHEN (TIMESTAMPDIFF(MINUTE, review_comments.created_at, NOW()) > 60) # 한 시간 지나면\n" +
                    "               THEN DATE_FORMAT(review_comments.created_at, '%H:%i')\n" +
                    "           ELSE CONCAT(TIMESTAMPDIFF(MINUTE, review_comments.created_at, NOW()), '분 전') # 한 시간 안 지났으면\n" +
                    "           END AS created_at,\n" +
                    "       review_comments.depth\n" +
                    "FROM review_comments\n" +
                    "         INNER JOIN users ON users.user_id = review_comments.user_id\n" +
                    "WHERE bundle_id = ? AND depth = 0\n" +
                    "ORDER BY review_comments.review_comment_id\n" +
                    "LIMIT ?, 5";
}
