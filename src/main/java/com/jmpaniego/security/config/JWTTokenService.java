package com.jmpaniego.security.config;

import com.google.common.collect.ImmutableMap;
import com.jmpaniego.security.services.DateService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import io.jsonwebtoken.impl.compression.GzipCompressionCodec;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import static io.jsonwebtoken.SignatureAlgorithm.HS256;

@Service
public class JWTTokenService implements Clock, TokenService {

  private static final String DOT = ".";
  private static final GzipCompressionCodec COMPRESSION_CODEC = new GzipCompressionCodec();

  @Autowired
  DateService dateService;
  @Value("${jwt.issuer}")
  private String issuer;
  @Value("${jwt.expiration-sec}")
  private int expirationSec;
  @Value("${jwt.clock-skew-sec}")
  private int clockSkewSec;
  @Value("${jwt.secret}")
  private String secretKey;


  private String newToken(final Map<String,String> attributes, final int expiresInSec){
    final DateTime now = dateService.now();
    final Claims claims = Jwts
        .claims()
        .setIssuer(issuer)
        .setIssuedAt(now.toDate());

    if(expiresInSec > 0){
      final DateTime expiresAt = now.plusSeconds(expiresInSec);
      claims.setExpiration(expiresAt.toDate());
    }
    claims.putAll(attributes);

    return Jwts
        .builder()
        .setClaims(claims)
        .signWith(HS256, secretKey)
        .compressWith(COMPRESSION_CODEC)
        .compact();
  }

  private static Map<String, String> parseClaims(final Supplier<Claims> toClaims) {
    try {
      final Claims claims = toClaims.get();
      final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
      for (final Map.Entry<String, Object> e: claims.entrySet()) {
        builder.put(e.getKey(), String.valueOf(e.getValue()));
      }
      return builder.build();
    } catch (final IllegalArgumentException | JwtException e) {
      return ImmutableMap.of();
    }
  }

  @Override
  public String permanent(Map<String, String> attributes) {
    return newToken(attributes,0);
  }

  @Override
  public String expiring(Map<String, String> attributes) {
    return newToken(attributes,expirationSec);
  }

  @Override
  public Map<String, String> untrusted(String token) {
    final JwtParser parser = Jwts
        .parser()
        .requireIssuer(issuer)
        .setClock(this)
        .setAllowedClockSkewSeconds(clockSkewSec)
        .setSigningKey(secretKey);

    final String withoutSignature = StringUtils.substringBeforeLast(token,DOT) + DOT;
    return parseClaims(() -> parser.parseClaimsJws(withoutSignature).getBody());
  }

  @Override
  public Map<String, String> verify(String token) {
    final JwtParser parser = Jwts
        .parser()
        .requireIssuer(issuer)
        .setClock(this)
        .setAllowedClockSkewSeconds(clockSkewSec)
        .setSigningKey(secretKey);

    return parseClaims(() -> parser.parseClaimsJws(token).getBody());
  }

  @Override
  public Date now() {
    final DateTime now = dateService.now();
    return now.toDate();
  }
}
