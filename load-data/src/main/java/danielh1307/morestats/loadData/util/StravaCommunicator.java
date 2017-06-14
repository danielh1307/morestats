package danielh1307.morestats.loadData.util;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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
	private WebResource resource;
	private WebResource apiResource;
	
	private Optional<StravaCommunicatorListener> listener;

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
	 * TODO: make a list of listener and this class a singleton.
	 * 
	 * @param listener a listener for events happening during the communication with Strava.
	 */
	public void setListener(StravaCommunicatorListener listener) {
		this.listener = Optional.ofNullable(listener);
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
	 * Returns the {@link Athlete} object of the currently signed in athlete associated with the given access token.
	 * 
	 * @param accessToken
	 *            the access token.
	 * @return the {@link Athlete} which represents the currently signed in
	 *         athlete.
	 */
	public Athlete getCurrentAthlete(String accessToken) {
		String responseString = apiResource.path("athlete").queryParam("access_token", accessToken).get(String.class);
		try {
			return mapper.readValue(responseString, Athlete.class);
		} catch (Exception ex) {
			throw handleCheckedException(ex);
		}
	}

	/**
	 * Returns all the {@link Activity} of the currently signed in athlete associated with the given access token.
	 * 
	 * @param accessToken
	 *            the access token.
	 * @param withSegments
	 *            true if {@link Segment} are added to the {@link Activity}.
	 * @return all the {@link Activity} of the currently signed in athlete.
	 */
	public Set<Activity> getActivitiesForCurrentAthlete(String accessToken, boolean withSegments) {
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
				Set<Activity> newActivities = new HashSet<Activity>(Arrays.asList(readValue));
				activities.addAll(newActivities);
				if (listener.isPresent()) {
					listener.get().activitiesLoaded(newActivities);
				}
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
					Set<Segment> newSegments = new HashSet<Segment>(Arrays.asList(segments));
					activity.addSegments(newSegments);
					if (listener.isPresent()) {
						listener.get().segmentsLoaded(activity, newSegments);
					}
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
