package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.firebase.FirebaseStorageManager;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
import com.dnr2144.csmoa.review.domain.model.Review;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ReviewRepository {

    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final FirebaseStorageManager firebaseStorageManager;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public PostReviewRes postReview(long userId, PostReviewReq postReviewReq) throws BaseException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update((PreparedStatementCreator) con -> {
                PreparedStatement preparedStatement = con.prepareStatement(
                        ReviewSqlQuery.INSERT_REVIEW, new String[]{"user_id"}
                );
                preparedStatement.setLong(1, userId);
                preparedStatement.setString(2, postReviewReq.getTitle());
                preparedStatement.setInt(3, postReviewReq.getPrice());
                preparedStatement.setFloat(4, postReviewReq.getRating());
                preparedStatement.setString(5, postReviewReq.getCsBrand());
                preparedStatement.setString(6, postReviewReq.getContent());

                return preparedStatement;
            }, keyHolder);

            // Get PK
            long reviewId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            // 파이어베이스에 리뷰 이미지 넣기
            List<String> reviewImagesAbsoluteUrls =
                    firebaseStorageManager.saveReviewImages(userId, postReviewReq.getReviewImages());

            // :reviewId, :imageSrc
            Map<String, Object> params = new HashMap();
            params.put("reviewId", reviewId);

            for (String reviewImageUrl : reviewImagesAbsoluteUrls) {
                params.put("imageSrc", reviewImageUrl);
                namedParameterJdbcTemplate.update(ReviewSqlQuery.INSERT_REVIEW_IMAGES, params);
            }

            return PostReviewRes.builder()
                    .reviewId(reviewId)
                    .userId(userId)
                    .reviewImageUrls(reviewImagesAbsoluteUrls)
                    .build();

        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, postReview) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<Review> getBestReviews(long userId) throws BaseException {
        try {
            return jdbcTemplate.query(ReviewSqlQuery.GET_BEST_REVIEWS,
                    (rs, row) -> Review.builder()
                            .reviewId(rs.getLong("review_id"))
                            .userId(rs.getLong("user_id"))
                            .itemName(rs.getString("item_name"))
                            .itemPrice(rs.getString("item_price"))
                            .itemStarScore(rs.getFloat("item_star_score"))
                            .csBrand(rs.getString("cs_brand"))
                            .content(rs.getString("content"))
                            .createdAt(rs.getString("create_at"))
                            .likeNum(rs.getInt("like_num"))
                            .viewNum(rs.getInt("view_num"))
                            .commentNum(rs.getInt("comment_num"))
                            .itemImageUrl(rs.getString("image_src"))
                            .isLike(rs.getBoolean("is_like"))
                            .build(), userId, new Random().nextInt(30));
        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, getBestReviews) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    public List<Review> getReviews(long userId, int pageNum) throws BaseException {
        try {
            return jdbcTemplate.query(ReviewSqlQuery.GET_REVIEWS,
                    (rs, row) -> Review.builder()
                            .reviewId(rs.getLong("review_id"))
                            .userId(rs.getLong("user_id"))
                            .itemName(rs.getString("item_name"))
                            .itemPrice(rs.getString("item_price"))
                            .itemStarScore(rs.getFloat("item_star_score"))
                            .csBrand(rs.getString("cs_brand"))
                            .content(rs.getString("content"))
                            .createdAt(rs.getString("create_at"))
                            .likeNum(rs.getInt("like_num"))
                            .viewNum(rs.getInt("view_num"))
                            .commentNum(rs.getInt("comment_num"))
                            .itemImageUrl(rs.getString("image_src"))
                            .isLike(rs.getBoolean("is_like"))
                            .build(), userId, (pageNum - 1) * 5);
        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, getReviews) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }



}
