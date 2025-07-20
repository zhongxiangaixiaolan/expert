package com.qing.expert.security;

import com.qing.expert.config.properties.ExpertProperties;
import com.qing.expert.common.util.LogUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * JWT Token 提供者
 * 增强安全性，支持黑名单机制
 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final ExpertProperties expertProperties;
    private final RedisTemplate<String, String> redisTemplate;

    private SecretKey secretKey;
    private static final String JWT_BLACKLIST_PREFIX = "jwt:blacklist:";
    private static final String JWT_REFRESH_PREFIX = "jwt:refresh:";

    @PostConstruct
    public void init() {
        ExpertProperties.Jwt jwtConfig = expertProperties.getJwt();

        // 验证JWT密钥强度
        if (jwtConfig.getSecret().length() < 32) {
            throw new IllegalArgumentException("JWT密钥长度不能少于32位");
        }

        // 创建安全密钥
        this.secretKey = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes());

        LogUtil.config("✅ JWT安全密钥初始化完成，密钥长度：{}位", jwtConfig.getSecret().length());
    }

    /**
     * 生成访问令牌
     */
    public String generateAccessToken(Long userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expertProperties.getJwt().getExpiration());

        String jti = UUID.randomUUID().toString(); // JWT ID，用于黑名单机制

        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("username", username)
                .claim("role", role)
                .claim("type", "access")
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expertProperties.getJwt().getExpiration() * 7); // 7倍有效期

        String jti = UUID.randomUUID().toString();
        String refreshToken = Jwts.builder()
                .setSubject(userId.toString())
                .claim("type", "refresh")
                .setId(jti)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        // 将刷新令牌存储到Redis
        redisTemplate.opsForValue().set(
                JWT_REFRESH_PREFIX + jti,
                userId.toString(),
                7 * expertProperties.getJwt().getExpiration(),
                TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    /**
     * 验证令牌
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String jti = claims.getId();

            // 检查是否在黑名单中
            if (isTokenBlacklisted(jti)) {
                LogUtil.warn("⚠️ JWT令牌已被加入黑名单: {}", jti);
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            LogUtil.warn("⚠️ JWT令牌验证失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("username", String.class);
    }

    /**
     * 从令牌中获取角色
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.get("role", String.class);
    }

    /**
     * 获取令牌过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = getClaimsFromToken(token);
        return claims.getExpiration();
    }

    /**
     * 将令牌加入黑名单
     */
    public void addToBlacklist(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            String jti = claims.getId();
            Date expiration = claims.getExpiration();

            if (jti != null && expiration != null) {
                long ttl = expiration.getTime() - System.currentTimeMillis();
                if (ttl > 0) {
                    redisTemplate.opsForValue().set(
                            JWT_BLACKLIST_PREFIX + jti,
                            "blacklisted",
                            ttl,
                            TimeUnit.MILLISECONDS);
                    LogUtil.info("✅ JWT令牌已加入黑名单: {}", jti);
                }
            }
        } catch (Exception e) {
            LogUtil.error("❌ 将JWT令牌加入黑名单失败", e);
        }
    }

    /**
     * 刷新访问令牌
     */
    public String refreshAccessToken(String refreshToken) {
        try {
            Claims claims = getClaimsFromToken(refreshToken);
            String type = claims.get("type", String.class);
            String jti = claims.getId();

            if (!"refresh".equals(type)) {
                throw new IllegalArgumentException("不是有效的刷新令牌");
            }

            // 检查刷新令牌是否存在于Redis中
            String userId = redisTemplate.opsForValue().get(JWT_REFRESH_PREFIX + jti);
            if (userId == null) {
                throw new IllegalArgumentException("刷新令牌已失效");
            }

            // 这里需要从数据库获取用户信息，暂时使用默认值
            return generateAccessToken(Long.parseLong(userId), "user", "USER");

        } catch (Exception e) {
            LogUtil.error("❌ 刷新访问令牌失败", e);
            throw new IllegalArgumentException("刷新令牌失败");
        }
    }

    /**
     * 撤销刷新令牌
     */
    public void revokeRefreshToken(String refreshToken) {
        try {
            Claims claims = getClaimsFromToken(refreshToken);
            String jti = claims.getId();
            redisTemplate.delete(JWT_REFRESH_PREFIX + jti);
            LogUtil.info("✅ 刷新令牌已撤销: {}", jti);
        } catch (Exception e) {
            LogUtil.error("❌ 撤销刷新令牌失败", e);
        }
    }

    /**
     * 从令牌中获取Claims
     */
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 检查令牌是否在黑名单中
     */
    private boolean isTokenBlacklisted(String jti) {
        if (jti == null) {
            return false;
        }
        return redisTemplate.hasKey(JWT_BLACKLIST_PREFIX + jti);
    }
}
