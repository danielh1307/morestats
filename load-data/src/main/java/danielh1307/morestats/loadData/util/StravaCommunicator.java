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

import danielh1307.morestats.loadData.entity.Activity;
import danielh1307.morestats.loadData.entity.Athlete;
import danielh1307.morestats.loadData.entity.Segment;

/**
 * 
 * This class communicates with the REST API of Strava.
 *
 */
@Component
public class StravaCommunicator {

	private static final Logger LOGGER = LoggerFactory.getLogger(StravaCommunicator.class);

	private ObjectMapper mapper;
	private WebResource resource;
	private WebResource apiResource;
	
	private Optional<StravaCommunicatorListener> listener;

	/**
	 * Default constructor.
	 * 
	 */
	public StravaCommunicator() {		
		Client client = Client.create(new DefaultClientConfig());
		resource = client.resource(UriBuilder.fromUri("https://www.strava.com").build());
		apiResource = resource.path("api").path("v3");
		mapper = new ObjectMapper();
		listener = Optional.empty();
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
