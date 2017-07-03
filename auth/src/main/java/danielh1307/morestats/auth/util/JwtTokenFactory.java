package danielh1307.morestats.auth.util;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * 
 * This class is used to generate a JSON Web Token.
 *
 */
@Component
public class JwtTokenFactory {

	public String createToken(String stravaAccessToken, String athleteName) {
		
		byte[] signingKey = "myKey".getBytes(StandardCharsets.UTF_8);

		Claims claims = Jwts.claims();
		claims.put("stravaAccessToken", stravaAccessToken);
		claims.put("athleteName", athleteName);

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime nowPlus5Minutes = now.plusMinutes(5L);
		
		Date nowDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
		Date expDate = Date.from(nowPlus5Minutes.atZone(ZoneId.systemDefault()).toInstant());

		String token = Jwts.builder().setClaims(claims).setIssuer("danielh1307.morestats")
				.setIssuedAt(nowDate).setExpiration(expDate).signWith(SignatureAlgorithm.HS512, signingKey).compact();

		return token;
	}

}
