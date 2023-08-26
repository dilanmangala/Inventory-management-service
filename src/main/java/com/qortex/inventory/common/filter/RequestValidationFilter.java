
package com.qortex.inventory.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.qortex.inventory.common.constant.GlobalErrorConstant;
import com.qortex.inventory.dto.TokenValidateRequest;
import com.qortex.inventory.dto.UserSessionWithModulesAndPermissions;
import com.qortex.inventory.common.token.JWTTokenProps;
import com.qortex.inventory.dto.ErrorResponse;
import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("java:S2259")
@Component
@Slf4j
public class RequestValidationFilter extends OncePerRequestFilter {

	public static final String SESSION_PAYLOAD = "sessionPayload";

	private final RestTemplate restTemplate;
	private final String validationUrl;
	private final JWTTokenProps jwtTokenProps;

	public RequestValidationFilter(RestTemplate restTemplate, @Value("${iam.service.validation.url}") String validationUrl, JWTTokenProps jwtTokenProps) {
		this.restTemplate = restTemplate;
		this.validationUrl = validationUrl;
		this.jwtTokenProps = jwtTokenProps;
	}
	private static final List<String> SWAGGER_URLS = Arrays.asList(
			"/v2/api-docs", "/configuration/ui", "/swagger-resources",
			"/configuration/security",
			"/swagger-ui.html",
			"/webjars/",
			"/swagger-ui/index.html",
			"/swagger-ui/**",
			"/swagger-ui/swagger-ui.css",
			"/swagger-ui/index.css",
			"/swagger-ui/swagger-initializer.js",
			"/swagger-ui/swagger-ui-standalone-preset.js",
			"/swagger-ui/swagger-initializer.js",
			"/swagger-ui/swagger-ui-bundle.js",
			"/openapi/swagger-config",
			"/v2/api-docs",
			"/v3/api-docs",
			"/v3/api-docs/**",
			"swagger-ui/favicon-32x32.png",
			"swagger-ui/favicon-16x16.png"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		log.info("doFilterInternal: Processing request");
		if (shouldSkipTokenValidation(request)) {
			filterChain.doFilter(request, response);
		} else {
			String correlationId = request.getHeader("X-CORRELATION-ID");
			String sourceId = request.getHeader("SOURCE-ID");
			String tokenRecieved = request.getHeader("AUTHORIZATION");
			String token;
			if(tokenRecieved.startsWith("Bearer")){
				String[] strings = tokenRecieved.split(" ");
				token = strings[1];
			}else {
				token = tokenRecieved;
			}

			if (token == null) {
				handleTokenEmpty(response);
				return;
			}

			try {
				Claims claims = Jwts.parser().setSigningKey(jwtTokenProps.getSecret()).parseClaimsJws(token).getBody();
				String userName = (String) claims.get("username");
				log.info("doFilterInternal: User name extracted from token: " + userName);

				TokenValidateRequest tokenValidateRequest = new TokenValidateRequest();
				tokenValidateRequest.setToken(token);
				tokenValidateRequest.setUsername(userName);

				HttpHeaders headers = new HttpHeaders();
				headers.set("X-CORRELATION-ID", correlationId);
				headers.set("SOURCE-ID", sourceId);
				HttpEntity<TokenValidateRequest> requestEntity = new HttpEntity<>(tokenValidateRequest, headers);

				ResponseEntity<UserSessionWithModulesAndPermissions> session = restTemplate.exchange(validationUrl,
						HttpMethod.POST, requestEntity, UserSessionWithModulesAndPermissions.class);
				if (session.getBody() != null && session.getBody().isAuthenticated()) {
					log.info("doFilterInternal: Token is valid. Setting up authentication context.");
					request.setAttribute(SESSION_PAYLOAD, session.getBody());
					Authentication auth = new UsernamePasswordAuthenticationToken(session.getBody().getUsername(), null,
							null);
					SecurityContextHolder.getContext().setAuthentication(auth);
					filterChain.doFilter(request, response);
				} else {
					log.warn("doFilterInternal: Token is invalid or session is not authenticated.");
					handleTokenEmpty(response);
				}
			} catch (ExpiredJwtException e) {
				handleInvalidToken(response, GlobalErrorConstant.TOKEN_EXPIRED);
			} catch (HttpStatusCodeException e){
				handleNotValidToken(response);
			}
			catch (SignatureException | IllegalArgumentException e) {
				handleInvalidToken(response, e.getMessage());
			} catch (MalformedJwtException exception){
				handleMalformedToken(response);
			}
		}

	}

	private void handleNotValidToken(HttpServletResponse response) throws IOException {
		log.warn("handleNotValidToken: Token is empty. Returning UNAUTHORIZED response.");
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
		errorResponse.setErrorDescription(GlobalErrorConstant.TOKEN_EXPIRED);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ObjectMapper mapper = new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(errorResponse));
	}

	private void handleMalformedToken(HttpServletResponse response) throws IOException {
		log.warn("handleTokenEmpty: Token is empty. Returning UNAUTHORIZED response.");
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
		errorResponse.setErrorDescription(GlobalErrorConstant.INCORRECT_TOKEN);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ObjectMapper mapper = new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(errorResponse));
	}

	private void handleTokenEmpty(HttpServletResponse response) throws IOException {
		log.warn("handleTokenEmpty: Token is empty. Returning UNAUTHORIZED response.");
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
		errorResponse.setErrorDescription(GlobalErrorConstant.INVALID_TOKEN);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ObjectMapper mapper = new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(errorResponse));
	}

	private void handleInvalidToken(HttpServletResponse response, String message) throws IOException {
		log.warn("handleTokenEmpty: Token is empty. Returning UNAUTHORIZED response.");
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setErrorCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
		errorResponse.setErrorDescription(message);
		response.setContentType("application/json");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		ObjectMapper mapper = new ObjectMapper();
		response.getWriter().write(mapper.writeValueAsString(errorResponse));
	}

	private boolean shouldSkipTokenValidation(HttpServletRequest request) {
		String requestPath = request.getRequestURI();
		return SWAGGER_URLS.stream().anyMatch(requestPath::contains);
	}
}
