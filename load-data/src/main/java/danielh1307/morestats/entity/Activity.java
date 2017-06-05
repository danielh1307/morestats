package danielh1307.morestats.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * This class represents an activity. 
 * 
 * @see https://strava.github.io/api/v3/activities/
 * 
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Activity extends BaseEntity {

	@JsonProperty("name")
	private String name;

	@JsonProperty("segment_efforts")
	private Set<Segment> segments;
	
	/**
	 * Default constructor.
	 */
	public Activity() {
		segments = new HashSet<Segment>();
	}

	/**
	 * 
	 * @param segments list of {@link Segment} of this activity.
	 */
	public void addSegments(Set<Segment> segments) {
		this.segments = segments;
	}

	/**
	 * 
	 * @return the id of this activity.
	 */
	public String getId() {
		return id;
	}

	/**
	 * 
	 * @return the name of the activity.
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @return unmodifiable list of segments of this activity.
	 */
	public Set<Segment> getSegments() {
		return Collections.unmodifiableSet(segments);
	}

	@Override
	public boolean equals(Object obj) {		
		// two activities are equal if they have the same id
		
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Activity)) {
			return false;
		}

		Activity thisObj = (Activity) obj;
		return id.equals(thisObj.getId());
	}

	@Override
	public int hashCode() {
		// two activities have the same hash code if they have the same id
		
		int result = 17;
		result = 31 * result + id.hashCode();
		return result;
	}

	@Override
	public String toString() {
		return "Activity: id = " + addBrackets(id) + ", name = " + addBrackets(name) + ", number of segments = "
				+ addBrackets(String.valueOf(segments.size()));
	}
}
