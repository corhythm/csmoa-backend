package com.dnr2144.csmoa.review;

import com.dnr2144.csmoa.config.BaseException;
import com.dnr2144.csmoa.config.BaseResponseStatus;
import com.dnr2144.csmoa.firebase.FirebaseStorageManager;
import com.dnr2144.csmoa.login.domain.GetUserInfoRes;
import com.dnr2144.csmoa.review.domain.PostReviewLikeRes;
import com.dnr2144.csmoa.review.domain.PostReviewReq;
import com.dnr2144.csmoa.review.domain.PostReviewRes;
import com.dnr2144.csmoa.review.domain.model.Comment;
import com.dnr2144.csmoa.review.domain.model.DetailedReview;
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

    // NOTE: 리뷰 등록
    public PostReviewRes postReview(long userId, PostReviewReq postReviewReq) throws BaseException {
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update((PreparedStatementCreator) con -> {
                PreparedStatement preparedStatement = con.prepareStatement(
                        ReviewSqlQuery.INSERT_REVIEW, new String[]{"review_id"}
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
            log.info("생성된 리뷰 아이디 = " + reviewId);

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

    // NOTE: 베스트 리뷰 가져오기
    public List<Review> getBestReviews(long userId) throws BaseException {
        try {
            return jdbcTemplate.query(ReviewSqlQuery.GET_BEST_REVIEWS,
                    (rs, row) -> Review.builder()
                            .reviewId(rs.getLong("review_id"))
                            .userId(rs.getLong("user_id"))
                            .reviewName(rs.getString("item_name"))
                            .price(rs.getString("item_price"))
                            .starScore(rs.getFloat("item_star_score"))
                            .csBrand(rs.getString("cs_brand"))
                            .content(rs.getString("content"))
                            .createdAt(rs.getString("created_at"))
                            .likeNum(rs.getInt("like_num"))
                            .viewNum(rs.getInt("view_num"))
                            .commentNum(rs.getInt("comment_num"))
                            .reviewImageUrls(Arrays.asList(
                                    rs.getString("review_image_urls").split(",", -1))
                            )
                            .isLike(rs.getBoolean("is_like"))
                            .build(), userId, new Random().nextInt(30));
        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, getBestReviews) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 일반 리뷰 가져오기
    public List<Review> getReviews(long userId, String searchWord, int pageNum) throws BaseException {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("pageNum", (pageNum - 1) * 10);

            String query = null;
            if (searchWord == null) { // 일반 리뷰
                log.info("(in ReviewRepository) 일반 리뷰");
                query = ReviewSqlQuery.GET_REVIEWS;

            } else { // 검색된 리뷰
                params.put("searchWord", "%" + searchWord + "%");
                log.info("(in ReviewRepository) 리뷰 검색");
                query = ReviewSqlQuery.GET_REVIEW_SEARCH_RESULTS;
            }

            return namedParameterJdbcTemplate.query(query, params,
                    (rs, row) -> Review.builder()
                            .reviewId(rs.getLong("review_id"))
                            .userId(rs.getLong("user_id"))
                            .reviewName(rs.getString("item_name"))
                            .price(rs.getString("item_price"))
                            .starScore(rs.getFloat("item_star_score"))
                            .csBrand(rs.getString("cs_brand"))
                            .content(rs.getString("content"))
                            .createdAt(rs.getString("created_at"))
                            .likeNum(rs.getInt("like_num"))
                            .viewNum(rs.getInt("view_num"))
                            .commentNum(rs.getInt("comment_num"))
                            .reviewImageUrls(Arrays.asList(
                                    rs.getString("review_image_urls").split(",", -1))
                            )
                            .isLike(rs.getBoolean("is_like"))
                            .build());

        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, getReviews) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 리뷰 검색 결과 가져오기
    public List<Review> getReviewSearchResult(long userId, String searchWord, int pageNum) throws BaseException {
        try {
            log.info("리뷰리포지토리 / userId = " + userId + ", searchWord = " + searchWord + ", pageNum = " + pageNum);
            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("searchWord", "%" + searchWord + "%");
            params.put("pageNum", (pageNum - 1) * 10);

            return namedParameterJdbcTemplate.query(ReviewSqlQuery.GET_REVIEW_SEARCH_RESULTS, params,
                    (rs, row) -> Review.builder()
                            .reviewId(rs.getLong("review_id"))
                            .reviewName(rs.getString("item_name"))
                            .price(rs.getString("item_price"))
                            .starScore(rs.getFloat("item_star_score"))
                            .csBrand(rs.getString("cs_brand"))
                            .commentNum(rs.getInt("comment_num"))
                            .viewNum(rs.getInt("view_num"))
                            .likeNum(rs.getInt("like_num"))
                            .isLike(rs.getBoolean("is_like"))
                            .createdAt(rs.getString("created_at"))
                            .reviewImageUrls(Arrays.asList(
                                    rs.getString("review_image_urls").split(",", -1))
                            )
                            .build());
        } catch (Exception exception) {
            log.error("getReviewSearchResult =  / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 내가 쓴 리뷰 가져오기
    public List<Review> getMyReviews(long userId, int pageNum) throws BaseException {
        try {
            HashMap<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            params.put("pageNum", (pageNum - 1) * 10);

            return namedParameterJdbcTemplate.query(ReviewSqlQuery.GET_MY_REVIEWS, params,
                    (rs, row) -> Review.builder()
                            .reviewId(rs.getLong("review_id"))
                            .userId(rs.getLong("user_id"))
                            .reviewName(rs.getString("item_name"))
                            .price(rs.getString("item_price"))
                            .starScore(rs.getFloat("item_star_score"))
                            .csBrand(rs.getString("cs_brand"))
                            .commentNum(rs.getInt("comment_num"))
                            .content(rs.getString("content"))
                            .viewNum(rs.getInt("view_num"))
                            .likeNum(rs.getInt("like_num"))
                            .isLike(rs.getBoolean("is_like"))
                            .createdAt(rs.getString("created_at"))
                            .reviewImageUrls(Arrays.asList(
                                    rs.getString("review_image_urls").split(",", -1))
                            )
                            .build());

        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, getMyReviews" + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 리뷰 세부정보 가져오기
    public DetailedReview getDetailedReview(long reviewId, long userId) throws BaseException {
        try {
            Map<String, Object> params = new HashMap();
            params.put("userId", userId);
            params.put("reviewId", reviewId);

            // 리뷰 가져오고
            DetailedReview detailedReview =
                    namedParameterJdbcTemplate.queryForObject(ReviewSqlQuery.GET_DETAILED_REVIEW, params,
                            (rs, row) -> DetailedReview.builder()
                                    .reviewId(rs.getLong("review_id"))
                                    .userId(rs.getLong("user_id"))
                                    .itemName(rs.getString("item_name"))
                                    .itemPrice(rs.getString("item_price"))
                                    .itemStarScore(rs.getFloat("item_star_score"))
                                    .csBrand(rs.getString("cs_brand"))
                                    .content(rs.getString("content"))
                                    .createdAt(rs.getString("created_at"))
                                    .likeNum(rs.getInt("like_num"))
                                    .viewNum(rs.getInt("view_num"))
                                    .commentNum(rs.getInt("comment_num"))
                                    .isLike(rs.getBoolean("is_like"))
                                    .build());

            // 프로필 이미지 가져오고
            String getReviewWriterInfoQuery = "SELECT nickname, profile_image_url from users where user_id = ?";
            GetUserInfoRes getUserInfoRes = jdbcTemplate.queryForObject(getReviewWriterInfoQuery,
                    (rs, row) -> GetUserInfoRes.builder()
                            .nickname(rs.getString("nickname"))
                            .userProfileImageUrl(rs.getString("profile_image_url"))
                            .build(), detailedReview.getUserId());

            detailedReview.setNickname(getUserInfoRes.getNickname());
            detailedReview.setUserProfileImageUrl(getUserInfoRes.getUserProfileImageUrl());

            // 이미지 리스트 가져오고
            String getReviewImagesQuery = "SELECT image_src FROM review_images WHERE review_id = ?";
            List<String> reviewImages = jdbcTemplate.queryForList(getReviewImagesQuery, String.class, reviewId);
            detailedReview.setItemImageUrls(reviewImages);

            // 히스토리 추가
            String postReviewHistoryQuery = "INSERT INTO review_histories (review_id, user_id) VALUE (?, ?)";
            jdbcTemplate.update(postReviewHistoryQuery, reviewId, userId);
            detailedReview.setViewNum(detailedReview.getViewNum() + 1); // 모든 게 성공하면 조회수 ++

            return detailedReview;
        } catch (Exception exception) {
            exception.printStackTrace();
            log.info("(in ReviewRepository, getDetailedReview) " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 부모 댓글 가져오기
    public List<Comment> getParentComments(long reviewId, int pageNum) throws BaseException {
        try {
            return jdbcTemplate.query(ReviewSqlQuery.GET_PARENT_COMMENTS, (rs, row) -> Comment.builder()
                    .reviewCommentId(rs.getLong("review_comment_id"))
                    .reviewId(rs.getLong("review_id"))
                    .userId(rs.getLong("user_id"))
                    .nickname(rs.getString("nickname"))
                    .userProfileImageUrl(rs.getString("profile_image_url"))
                    .bundleId(rs.getLong("bundle_id"))
                    .commentContent(rs.getString("comment_content"))
                    .nestedCommentNum(rs.getInt("nested_comment_num"))
                    .createdAt(rs.getString("created_at"))
                    .depth(rs.getInt("depth"))
                    .build(), reviewId, (pageNum - 1) * 5);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, getParentComments" + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 부모 댓글 등록
    public Comment postParentComment(long reviewId, long userId, String content) throws BaseException {
        try {
            String insertParentCommentQuery = "INSERT INTO review_comments " +
                    "(user_id, review_id, bundle_id, depth, comment_content) VALUE (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update((PreparedStatementCreator) con -> {
                PreparedStatement preparedStatement = con.prepareStatement(
                        insertParentCommentQuery, new String[]{"review_comment_id"}
                );
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, reviewId);
                preparedStatement.setLong(3, -1);
                preparedStatement.setInt(4, 1);
                preparedStatement.setString(5, content);

                return preparedStatement;
            }, keyHolder);

            // NOTE: bundleId 업데이트
            String updateCommentBundleIdQuery = "UPDATE review_comments SET bundle_id = review_comment_id WHERE review_comment_id = LAST_INSERT_ID();";
            jdbcTemplate.update(updateCommentBundleIdQuery);

            // Get PK
            long reviewCommentId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            return jdbcTemplate.queryForObject(ReviewSqlQuery.RETURN_INSERTED_COMMENT, (rs, row) -> Comment.builder()
                    .reviewCommentId(rs.getLong("review_comment_id"))
                    .reviewId(rs.getLong("review_id"))
                    .userId(rs.getLong("user_id"))
                    .nickname(rs.getString("nickname"))
                    .userProfileImageUrl(rs.getString("profile_image_url"))
                    .bundleId(rs.getLong("bundle_id"))
                    .commentContent(rs.getString("comment_content"))
                    .createdAt(rs.getString("created_at"))
                    .depth(rs.getInt("depth"))
                    .build(), reviewCommentId);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, postParentComment" + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 해당 commentId는 parent이므로 이걸 bundleId로 사용하면 됨
    public List<Comment> getChildComments(long bundleId, int pageNum) throws BaseException {
        try {
            return jdbcTemplate.query(ReviewSqlQuery.GET_CHILD_COMMENTS, (rs, row) -> Comment.builder()
                    .reviewCommentId(rs.getLong("review_comment_id"))
                    .reviewId(rs.getLong("review_id"))
                    .userId(rs.getLong("user_id"))
                    .nickname(rs.getString("nickname"))
                    .userProfileImageUrl(rs.getString("profile_image_url"))
                    .bundleId(rs.getLong("bundle_id"))
                    .commentContent(rs.getString("comment_content"))
                    .createdAt(rs.getString("created_at"))
                    .depth(rs.getInt("depth"))
                    .build(), bundleId, (pageNum - 1) * 5);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, getChildComments" + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 리뷰 자식 댓글 삽입
    public Comment postChildComment(long reviewId, long bundleId, long userId, String content) throws BaseException {
        try {
            String insertChildCommentQuery = "INSERT INTO review_comments " +
                    "(user_id, review_id, bundle_id, depth, comment_content) VALUE (?, ?, ?, ?, ?)";
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update((PreparedStatementCreator) con -> {
                PreparedStatement preparedStatement = con.prepareStatement(
                        insertChildCommentQuery, new String[]{"review_comment_id"}
                );
                preparedStatement.setLong(1, userId);
                preparedStatement.setLong(2, reviewId);
                preparedStatement.setLong(3, bundleId);
                preparedStatement.setInt(4, 0);
                preparedStatement.setString(5, content);

                return preparedStatement;
            }, keyHolder);

            // Get PK
            long reviewCommentId = Objects.requireNonNull(keyHolder.getKey()).longValue();

            return jdbcTemplate.queryForObject(ReviewSqlQuery.RETURN_INSERTED_COMMENT, (rs, row) -> Comment.builder()
                    .reviewCommentId(rs.getLong("review_comment_id"))
                    .reviewId(rs.getLong("review_id"))
                    .userId(rs.getLong("user_id"))
                    .nickname(rs.getString("nickname"))
                    .userProfileImageUrl(rs.getString("profile_image_url"))
                    .bundleId(rs.getLong("bundle_id"))
                    .commentContent(rs.getString("comment_content"))
                    .createdAt(rs.getString("created_at"))
                    .depth(rs.getInt("depth"))
                    .build(), reviewCommentId);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, postChildComment" + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 리뷰 좋아요 OR 좋아요 취소
    public PostReviewLikeRes postReviewLike(long reviewId, long userId) throws BaseException {
        try {
            log.info("in Repository postReviewLike / reviewId = " + reviewId + ", userId = " + userId);
            Boolean isLike = getReviewLike(reviewId, userId);
            if (isLike == null) {
                String insertReviewLikeQuery = "INSERT INTO review_likes (review_id, user_id, is_like) VALUE (?, ?, ?);";
                jdbcTemplate.update(insertReviewLikeQuery, reviewId, userId, true);
                isLike = true;
            } else {
                // 좋아요 <-> 싫어요
                String updateReviewLikeQuery = "";
                if (Boolean.TRUE.equals(isLike)) {  // 좋아요 -> 싫어요
                    updateReviewLikeQuery = "UPDATE review_likes SET is_like = false, updated_at = CURRENT_TIMESTAMP WHERE review_id = ? AND user_id = ?;";
                } else {  // 싫어요 -> 좋아요
                    updateReviewLikeQuery = "UPDATE review_likes SET is_like = true, updated_at = CURRENT_TIMESTAMP WHERE review_id = ? AND user_id = ?;";
                }
                isLike = !isLike;
                jdbcTemplate.update(updateReviewLikeQuery, reviewId, userId);
            }

            return PostReviewLikeRes.builder()
                    .userId(userId)
                    .reviewId(reviewId)
                    .isLike(isLike)
                    .build();
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, postReviewLike = " + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 해당 리뷰에 해당 bundleId를 가진 부모 댓글이 있는지 체크
    public Integer checkExistsParentCommentInThatReview(long reviewId, long bundleId) throws BaseException {
        try {
            String query = "SELECT EXISTS(SELECT * FROM review_comments WHERE review_id = ? AND bundle_id = ? AND depth = 1)";
            return jdbcTemplate.queryForObject(query, Integer.class, reviewId, bundleId);
        } catch (Exception ex) {
            ex.printStackTrace();
            log.info("In ReviewRepository, checkExistsParentCommentInThatReview" + ex.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 리뷰 있는지 조회
    public Integer checkReviewExists(long reviewId) throws BaseException {
        try {
            String checkReviewExistsQuery = "SELECT EXISTS(SELECT * FROM reviews WHERE review_id = ?)";
            return jdbcTemplate.queryForObject(checkReviewExistsQuery, Integer.class, reviewId);
        } catch (Exception exception) {
            log.error("checkReviewExists / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }

    // NOTE: 리뷰 좋아요 한 이력이 있는지 알아보기
    public Boolean getReviewLike(long reviewId, long userId) throws BaseException {
        try {
            String checkExistsReviewLikeQuery = "SELECT EXISTS(SELECT is_like FROM review_likes WHERE review_id = ? and user_id = ?)";
            int isReviewLikeExists = jdbcTemplate.queryForObject(checkExistsReviewLikeQuery, Integer.class, reviewId, userId);
            if (isReviewLikeExists == 0) return null;

            // 이전 기록이 있으면
            String getReviewLikeQuery = "SELECT is_like FROM review_likes WHERE review_id = ? and user_id = ?";
            return jdbcTemplate.queryForObject(getReviewLikeQuery, Boolean.class, reviewId, userId);
        } catch (Exception exception) {
            log.error("getReviewLike =  / " + exception.getMessage());
            throw new BaseException(BaseResponseStatus.DATABASE_ERROR);
        }
    }
}
