package danielh1307.morestats.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * This class represents an athlete.
 * 
 * @see https://strava.github.io/api/v3/athlete/
 *
 */
@JsonIgnoreProperties(ignoreUnknown=true)
public class Athlete extends BaseEntity {

	@JsonProperty("firstname")
	private String firstName;

	@JsonProperty("lastname")
	private String lastName;

	/**
	 * 
	 * @return the first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * 
	 * @return the last name.
	 */
	public String getLastName() {
		return lastName;
	}
	
	@Override
	public String toString() {
		return "Athlete: id = " + addBrackets(id) + ", first name = " + addBrackets(firstName) + ", last name = " + addBrackets(lastName);
	}
	
}
