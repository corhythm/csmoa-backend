package com.dnr2144.csmoa.util;

import com.dnr2144.csmoa.config.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

import static com.dnr2144.csmoa.config.BaseResponseStatus.EMPTY_JWT;
import static com.dnr2144.csmoa.config.BaseResponseStatus.INVALID_JWT;
import static com.dnr2144.csmoa.config.secret.Secret.SIGNED_KEY;

@Service
public class JwtService {

    // accessToken == 유효기간 1일
    public final long ACCESS_TOKEN = 1000L * 60 * 60 * 24;
    // 1000ms = 1초,  60초 60분 24시간 15일 => refreshToken == 유효기간 15일
    public final long REFRESH_TOKEN = 1000L * 60 * 60 * 24 * 15;


    /*
    JWT 생성
    @param userIdx
    @return String
     */
    public String createJwt(long userId, long tokenType) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("userId", userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenType))
                .signWith(SIGNED_KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /*
    Header에서 X-ACCESS-TOKEN 으로 JWT 추출
    @return String
     */
//    public String getJwt() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
//        return request.getHeader("X-ACCESS-TOKEN");
//    }

    /*
    JWT에서 userId 추출
    @return int
    @throws BaseException
    */
    public Long getUserId(String jwtToken) throws BaseException {
        //1. JWT 추출
//        String accessToken = getJwt();
        if (jwtToken == null || jwtToken.length() == 0) {
            throw new BaseException(EMPTY_JWT);
        }

        // 2. JWT parsing
        Jws<Claims> claims;
        try {
            claims = Jwts.parserBuilder().setSigningKey(SIGNED_KEY).build().parseClaimsJws(jwtToken);
        } catch (Exception ignored) {
            throw new BaseException(INVALID_JWT);
        }

        // 3. userIdx 추출
        return claims.getBody().get("userId", Long.class);
    }

}