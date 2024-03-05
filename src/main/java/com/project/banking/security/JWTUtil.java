package com.project.banking.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class JWTUtil {
	@Value("${jwt_secret}")
	private String secretKey;

	public String generateToken(String username) {
		Instant expirationDate = Instant.now().plus(1, ChronoUnit.HOURS);
		return JWT.create()
				.withSubject("User details")
				.withClaim("username", username)
				.withClaim("test_claim", "qwerty")
				.withIssuedAt(Instant.now())
				.withIssuer("BankingAPI")
				.withExpiresAt(expirationDate)
				.sign(Algorithm.HMAC256(secretKey));
	}

	public String validateTokenAndRetrieveClaim(String token) throws JWTVerificationException {
		JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(secretKey))
				.withSubject("User details")
				.withIssuer("BankingAPI")
				.build();
		DecodedJWT jwt = jwtVerifier.verify(token);
		return jwt.getClaim("username").asString();
	}
}
