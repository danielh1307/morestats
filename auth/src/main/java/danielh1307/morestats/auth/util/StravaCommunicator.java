package danielh1307.morestats.auth.util;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

/**
 * 
 * This class communicates with the REST API of Strava.
 * 
 * Please note the client secret must be known before instantiating is possible,
 * so call {@link StravaCommunicator#setClientSecret(String)} before using this
 * class.
 */
@Component
public class StravaCommunicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(StravaCommunicator.class);
	private static final String CLIENT_ID = "18287";
	private static String clientSecret;

	private ObjectMapper mapper;
	private WebResource apiResource;
	private WebResource resource;

	/**
	 * Default constructor. {@link StravaCommunicator#setClientSecret(String)}
	 * must be called, otherwise the class cannot be instantiated.
	 * 
	 */
	public StravaCommunicator() {
		if (clientSecret == null) {
			String errMsg = "client secret is null, set this first before using this class";
			LOGGER.error(errMsg);
			throw new RuntimeException(errMsg);
		}

		Client client = Client.create(new DefaultClientConfig());
		resource = client.resource(UriBuilder.fromUri("https://www.strava.com").build());
		apiResource = resource.path("api").path("v3");
		mapper = new ObjectMapper();
	}

	/**
	 * 
	 * @param clientSecret
	 *            sets the client secret. The value cannot be changed later.
	 */
	public static void setClientSecret(String cs) {
		if (clientSecret != null) {
			String errMsg = "client secret is already set: [" + clientSecret + "], it is not possible to change it";
			LOGGER.error(errMsg);
			throw new RuntimeException(errMsg);
		}
		clientSecret = cs;
	}

	/**
	 * This method authorizes the user with a given code against Strava. The
	 * access token, which can be used for all further communication, is
	 * returned.
	 * 
	 * 
	 * @param code
	 *            the code to exchange the access token.
	 * @return the access token.
	 */
	public String getAccessToken(String code) {
		String responseString = resource.path("oauth").path("token").queryParam("client_id", CLIENT_ID)
				.queryParam("client_secret", clientSecret).queryParam("code", code).post(String.class);
		try {
			String accessToken = omitQuotes(mapper.readTree(responseString).get("access_token"));
			LOGGER.info("Access token is: " + accessToken);
			return accessToken;
		} catch (Exception ex) {
			throw handleCheckedException(ex);
		}
	}

	/**
	 * 
	 * @param accessToken
	 *            the Strava access token.
	 * @return the name of the athlete the token is assigned to.
	 */
	public String getNameOfAthlete(String accessToken) {
		String responseString = apiResource.path("athlete").queryParam("access_token", accessToken).get(String.class);
		try {
			JsonNode node = mapper.readTree(responseString);
			return node.get("username").asText();
		} catch (Exception ex) {
			throw handleCheckedException(ex);
		}

	}

	/**
	 * Removes leading and trailing quotes of a string.
	 * 
	 * @param s
	 * @return the given string without leading and trailing quotes.
	 */
	private String omitQuotes(JsonNode node) {
		String s = node.toString();
		if (s.startsWith("\"")) {
			s = s.substring(1);
		}
		if (s.endsWith("\"")) {
			s = s.substring(0, s.length() - 1);
		}

		return s;
	}

	/**
	 * Handles the checked exceptions thrown in this class. We do not want to
	 * pass them to the caller so we change them into {@link RuntimeException}.
	 * 
	 * @param ex
	 *            the checked exception.
	 * @return the {@link RuntimeException} (it contains the original checked
	 *         exception).
	 */
	private RuntimeException handleCheckedException(Exception ex) {
		LOGGER.error(ex.getMessage(), ex);
		return new RuntimeException(ex);
	}

}
