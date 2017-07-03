package danielh1307.morestats.loadData.util;

/**
 * 
 * Simple container class which holds the JSON Web Token.
 *
 */
public class TokenContainer {
	
	private String jwt;
	
	/**
	 * Empty constructor, needed for Jackson.
	 */
	public TokenContainer() {
	}

	/**
	 * 
	 * @param jwt the JSON Web Token.
	 */
	public TokenContainer(String jwt) {
		this.jwt = jwt;
	}

	/**
	 * 
	 * @return the JSON Web Token.
	 */
	public String getJwt() {
		return jwt;
	}

	/**
	 * 
	 * @param accessToken the JSON Web Token.
	 */
	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
	
	

}
