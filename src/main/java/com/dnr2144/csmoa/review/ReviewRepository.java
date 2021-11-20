package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.firebase.FirebaseStorageManager;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

}
