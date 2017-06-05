package danielh1307.morestats.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * This class represents a segment.
 * 
 * @see https://strava.github.io/api/v3/segments/
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Segment extends BaseEntity {
	
	@JsonProperty("name")
	private String name;
	
	/**
	 * 
	 * @return the name of the segment.
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {		
		// two segments are equal if they have the same id
		
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof Segment)) {
			return false;
		}

		Segment thisObj = (Segment) obj;
		return id.equals(thisObj.id);
	}

	@Override
	public int hashCode() {
		// two segments have the same hash code if they have the same id
		
		int result = 17;
		result = 31 * result + id.hashCode();
		return result;
	}
	
	@Override
	public String toString() {
		return "Segment: id = " + addBrackets(id) + ", name = " + addBrackets(name);
	}
	
}
