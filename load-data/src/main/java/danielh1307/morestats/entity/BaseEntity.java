package danielh1307.morestats.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * Base entity with common attributes and methods.
 *
 */
public class BaseEntity {

	@JsonProperty("id")
	protected String id;
	
	/**
	 * 
	 * @param s a string.
	 * @return the string enclosed by brackets [].
	 */
	protected String addBrackets(String s) {
		return "[" + s + "]";
	}
	
}
