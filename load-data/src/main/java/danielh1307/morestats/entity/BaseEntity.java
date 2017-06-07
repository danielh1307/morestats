package danielh1307.morestats.entity;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * Base entity with common attributes and methods.
 *
 */
@MappedSuperclass
public class BaseEntity {

	@Id
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
