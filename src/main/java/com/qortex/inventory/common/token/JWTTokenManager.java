package com.qortex.inventory.common.token;

import com.qortex.inventory.common.constant.ServiceConstant;
import com.qortex.inventory.dto.UserTokenRequest;
import com.qortex.inventory.dto.UserTokenResponse;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JWTTokenManager {

	private static final String USERNAME = "username";

	@Autowired
	JWTTokenProps jwtProps;

	public String generateToken(String username) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put("sub", username);
		claims.put(USERNAME, username);
		claims.put(ServiceConstant.SESSION_ID, "12345");
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtProps.getJwtExpirationInMs());
		return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtProps.getSecret()).compact();
	}

	public UserTokenResponse validateToken(UserTokenRequest validateTokenRequest) {
		UserTokenResponse staffTokenResponse = new UserTokenResponse();
		try {
			Claims claims = Jwts.parser().setSigningKey(jwtProps.getSecret())
					.parseClaimsJws(validateTokenRequest.getToken()).getBody();
			String userName = (String) claims.get(USERNAME);
			if (!userName.equalsIgnoreCase(validateTokenRequest.getUsername())) {
				staffTokenResponse.setAuthorized(false);
			} else {
				staffTokenResponse.setAuthorized(true);
			}
		} catch (Exception ex) {
		}
		return staffTokenResponse;
	}

	public String geTRefreshToken(String username, String sessionId) {
		Claims claims = Jwts.claims().setSubject(username);
		claims.put(USERNAME, username);
		claims.put(ServiceConstant.REFRESH_TOKEN_SECRET, jwtProps.getRefreshTokenSecret());
		claims.put(ServiceConstant.TYPE_OF_TOKEN, ServiceConstant.REFRESH_TOKEN);
		claims.put(ServiceConstant.SESSION_ID, sessionId);
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + jwtProps.getRefreshTokenExpirationInMs());
		String refreshToken = Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiryDate)
				.signWith(SignatureAlgorithm.HS512, jwtProps.getSecret()).compact();
		return refreshToken;
	}

}