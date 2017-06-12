package danielh1307.morestats.loadData.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.entity.Segment;

/**
 * 
 * This class communicates with the REST API of Strava.
 * 
 * Please note you have to call
 * {@link StravaCommunicator#setAccessToken(String, String)} to authorize before
 * this class can be used. This makes the class stateful.
 *
 */
public class StravaCommunicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(StravaCommunicator.class);
	private static final String CLIENT_ID = "18287";

	private ObjectMapper mapper;
	private WebResource resource;
	private WebResource apiResource;

	private String accessToken;

	/**
	 * Default constructor.
	 */
	public StravaCommunicator() {
		Client client = Client.create(new DefaultClientConfig());
		resource = client.resource(UriBuilder.fromUri("https://www.strava.com").build());
		apiResource = resource.path("api").path("v3");
		mapper = new ObjectMapper();
	}


	/**
	 * This method authorizes the user via OAuth2 at Strava.
	 * 
	 * 
	 * @param clientSecret
	 *            the client secret.
	 * @param code
	 *            the code to exchange the access token.
	 * @return the URI to authorize.
	 */
	public void authorize(String clientSecret, String code) {
		// http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=http://localhost/morestats/auth&response_type=code&scope=view_private

		String responseString = resource.path("oauth").path("token").queryParam("client_id", CLIENT_ID)
				.queryParam("client_secret", clientSecret).queryParam("code", code).post(String.class);
		try {
			accessToken = omitQuotes(mapper.readTree(responseString).get("access_token"));
			LOGGER.info("Access token is: " + accessToken);
		} catch (Exception ex) {
			throw handleCheckedException(ex);
		}
	}

	/**
	 * This method can be called if an access token is already available, use
	 * {@link StravaCommunicator#authorize(String, String)} instead.
	 * 
	 * @param accessToken
	 *            the access token.
	 */
	public void authorizeWithAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * Returns the {@link Athlete} object of the currently signed in athlete.
	 * 
	 * @return the {@link Athlete} which represents the currently signed in
	 *         athlete.
	 */
	public Athlete getCurrentAthlete() {
		String responseString = apiResource.path("athlete").queryParam("access_token", accessToken).get(String.class);
		try {
			return mapper.readValue(responseString, Athlete.class);
		} catch (Exception ex) {
			throw handleCheckedException(ex);
		}
	}

	/**
	 * Returns all the {@link Activity} of the currently signed in athlete.
	 * 
	 * @param withSegments
	 *            true if {@link Segment} are added to the {@link Activity}.
	 * @return all the {@link Activity} of the currently signed in athlete.
	 */
	public Set<Activity> getActivitiesForCurrentAthlete(boolean withSegments) {
		Set<Activity> activities = new HashSet<Activity>();

		// pagination
		int startPage = 1;
		int per_page = 50;

		try {
			// first we get all the activities of the user until we get an empty
			// page
			while (true) {
				String responseString = apiResource.path("athlete").path("activities")
						.queryParam("access_token", accessToken).queryParam("per_page", String.valueOf(per_page))
						.queryParam("page", String.valueOf(startPage)).get(String.class);

				if (mapper.readTree(responseString).get(0) == null) {
					break;
				}

				Activity[] readValue = mapper.readValue(responseString, Activity[].class);
				activities.addAll(Arrays.asList(readValue));
				startPage++;
			}

			// next we must request each activity to get the segments
			if (withSegments) {
				for (Activity activity : activities) {
					LOGGER.info("Getting segments for activity ... " + activity);
					String responseString = apiResource.path("activities").path(activity.getId())
							.queryParam("access_token", accessToken).get(String.class);
					JsonNode segmentEfforts = mapper.readTree(responseString).get("segment_efforts");
					Segment[] segments = mapper.readValue(segmentEfforts.toString(), Segment[].class);
					activity.addSegments(new HashSet<Segment>(Arrays.asList(segments)));
				}
			}

			return activities;
		} catch (Exception ex) {
			throw handleCheckedException(ex);
		}
	}

	/**
	 * Removes leading and trailing quotes of a string.
	 * 
	 * @param s
	 * @return
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
