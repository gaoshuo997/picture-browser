package com.jimmy.jwt;

import com.jimmy.common.JwtCommonKeys;
import com.jimmy.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

  private SecretKey signingKey;

  @Value("${jwt.expire-in:3600000}")
  private long validityInMilliseconds;

  @Resource
  JwtConfig jwtConfig;


  private final String TOKEN_KEY = "Authorization";

  private final String TOKEN_PREFIX = "Bearer ";

  private final String CHECKSUM = "checksum";

  @PostConstruct
  protected void init() {
    signingKey = Keys.hmacShaKeyFor(jwtConfig.getSecretKey().getBytes(StandardCharsets.UTF_8));
  }


  /**
   * 生成 JWT Token（核心：加密逻辑）
   * @param userId 自定义载荷：用户 ID（可扩展其他信息，如用户名、角色）
   * @param extraClaims 额外自定义载荷（可选）
   * @return 加密后的 JWT Token 字符串
   */
  public String generateToken(Long userId, Map<String, Object> extraClaims) {

    Date now = new Date();
    // 2. 构建 Token 并加密返回
    return Jwts.builder()
            // ① 额外自定义载荷（如用户名、角色等）
            .claims(extraClaims)
            // ② 核心载荷：用户 ID（作为 subject，唯一标识）
            .subject(userId.toString())
            // ③ 签发者
            .issuer(jwtConfig.getIssuer())
            // ④ 签发时间（当前时间）
            .issuedAt(now)
            // ⑤ 过期时间（当前时间 + 配置的过期时长）
            .expiration(new Date(now.getTime() + jwtConfig.getExpireTime()))
            // ⑥ 签名算法 + 密钥（加密核心：确保 Token 不被篡改）
            .signWith(signingKey, Jwts.SIG.HS256)
            // ⑦ 构建并转换为字符串
            .compact();
  }

  /**
   * get userId from token.
   *
   * @param token token
   * @return userId
   */
  public Long getUserId(String token) {
    String userId = getClaimsFromToken(token).getSubject();
    return userId == null ? null : Long.parseLong(userId);
  }

  /**
   * get checksum from token
   *
   * @return checksum
   */
  public String getChecksum(String token) {
    Object checksum = getClaimsFromToken(token)
        .get(CHECKSUM);
    return checksum == null ? null : checksum.toString();
  }


  /**
   * get token string from http header map.
   *
   * @param req HttpServletRequest
   * @return token
   */
  public String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader(TOKEN_KEY);
    if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
      return bearerToken.substring(7);
    }
    return null;
  }

  /**
   * valid token.
   *
   * @param token token
   * @return boolean
   */
  public boolean validateToken(String token) {
    try {
//      Jwts.parser()
//          .verifyWith(signingKey)
//          .build()
//          .parseSignedClaims(token);
      getClaimsFromToken(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }

  private Claims getClaimsFromToken(String token) {
      return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
  }

//  private String doGenerateToken(Claims claims) {
//    Date now = new Date();
//    Date validity = new Date(now.getTime() + validityInMilliseconds);
//    return Jwts.builder()
//            .claims(claims)
//            .issuedAt(now)
//            .expiration(validity)
//            .signWith(signingKey, Jwts.SIG.HS256).compact();
//  }

  public String refreshToken(String token) {
    String refreshedToken;
    try {
      final Claims claims = getClaimsFromToken(token);
      Long userId = getUserId(token);
      refreshedToken = generateToken(userId,claims);
    } catch (Exception e) {
      refreshedToken = null;
    }
    return refreshedToken;
  }

//  public static void main(String[] args) {
//    JwtTokenProvider provider = new JwtTokenProvider();
//    String s = Base64.getEncoder().encodeToString("cci-data".getBytes(Charset.forName("UTF-8")));
//    provider.secretKey = s;
//    provider.validityInMilliseconds = 36000000;
//
//    String token = provider.createToken(1L, 1L, "admin", Arrays
//        .asList("/sys/app/update.do", "/score/list", "/event/list"), "abc");
//    System.out.println(token);
//    boolean b = provider.validateToken(token);
//    System.out.println(b);
//
//  }
}
