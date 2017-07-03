package danielh1307.morestats.auth.controller;

import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import danielh1307.morestats.auth.util.JwtTokenFactory;
import danielh1307.morestats.auth.util.StravaCommunicator;

/**
 * 
 * This is the controller class for the auth SCS. Its main purpose is to
 * authenticate the user against Strava and create a JWT.
 *
 */
@Controller
@RequestMapping
public class AuthController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	private static final String HOST = "http://localhost";

	@Value("${server.port}")
	private String port;

	@Autowired
	private StravaCommunicator stravaComm;

	@Autowired
	private JwtTokenFactory tokenFactory;

	@Autowired
	private HttpSession session;

	/**
	 * This interface /morestats/authorize is called if the user has to be
	 * authenticated. The method delegates the call to the OAuth2 mechanism of
	 * Strava and performs the authentication. In the end, the browser is
	 * redirected to the URL given with the origin-url GET parameter.
	 * 
	 * @param originUrl
	 *            the URL the browser is redirected to after successful
	 *            authentication.
	 * @return Redirects to URL
	 */
	@RequestMapping("/morestats/authorize")
	public ModelAndView authorize(@RequestParam("origin-url") String originUrl) {

		session.setAttribute("origin-url", originUrl);

		// we have to make a request to
		String redirectUrl = "http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=" + HOST + ":" + port
				+ "/morestats/auth&response_type=code&scope=view_private";

		return new ModelAndView("redirect:" + redirectUrl);
	}

	/**
	 * This method takes the authorization grant from Strava and fetches a new
	 * access token. Afterwards, it creates a JWT with the access token and the
	 * user name.
	 * 
	 * @param code
	 *            authorization grant from Strava.
	 * @return Redirect to URL which was passed to the service.
	 */
	@RequestMapping("/morestats/auth")
	public ModelAndView auth(@RequestParam("code") String code) {
		String accessToken = stravaComm.getAccessToken(code);
		LOGGER.info("Successfully logged in with accessToken [" + accessToken + "]");

		// redirect back to calling SCS with the JWT
		String athleteName = stravaComm.getNameOfAthlete(accessToken);
		LOGGER.info("Athlete name is " + athleteName);
		String tokenString = tokenFactory.createToken(accessToken, athleteName);

		String originUrl = String.valueOf(session.getAttribute("origin-url"));
		LOGGER.info("Redirecting to: " + originUrl);
		return new ModelAndView("redirect:" + originUrl + "?jwt=" + tokenString);

	}

}
