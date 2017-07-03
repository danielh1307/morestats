package danielh1307.morestats.loadData.util;

import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

/**
 * 
 * This class is responsible to deal with the JSON Web Token.
 *
 */
@Component
public class JwtTokenFactory {
	
	private byte[] signingKey;
	
	public JwtTokenFactory() {
		// TODO: put this into a resource file
		signingKey = "myKey".getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * 
	 * @param rawJwtToken the JSON Web Token.
	 * @return the coded Strava access token.
	 */
	public String getStravaAccessToken(String rawJwtToken) {
		return getClaimBody(rawJwtToken, "stravaAccessToken");
	}
	
	/**
	 * 
	 * @param rawJwtToken the JSON Web Token.
	 * @return the name of the athlete the token is assigned to.
	 */
	public String getAthleteName(String rawJwtToken) {
		return getClaimBody(rawJwtToken, "athleteName");
	}
	
	private String getClaimBody(String rawJwtToken, String key) {
		Jws<Claims> jwsClaims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(rawJwtToken);
		return jwsClaims.getBody().get(key, String.class);
	}
	
}
