package danielh1307.morestats.auth;

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

@Controller
@RequestMapping
public class AuthController {
	
	private static final String HOST = "http://localhost";
	private static final String SCOPE = "view_private";
	private static final String RESPONSE_TYPE = "code";


	@Value("${server.port}")
	private String port;
	
	@Autowired
	private StravaCommunicator stravaComm;
	
	@Autowired
	private JwtTokenFactory tokenFactory;
	

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@RequestMapping("/morestats/authorize")
	public ModelAndView authorize(HttpSession session, @RequestParam("origin-url") String originUrl) {
		
		session.setAttribute("origin-url", originUrl);
		
		// we have to make a request to
		String redirectUrl = "http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=" + HOST + ":" + port
				+ "/morestats/auth&response_type=" + RESPONSE_TYPE + "&scope=" + SCOPE;

		return new ModelAndView("redirect:" + redirectUrl);
	}
	
	@RequestMapping("/morestats/auth")
	public ModelAndView auth(HttpSession session, @RequestParam("code") String code) {
		String accessToken = stravaComm.getAccessToken(code);
		LOGGER.info("Successfully logged in with accessToken [" + accessToken + "]");
	
		
		// redirect back to calling SCS with the JWT
		String tokenString = tokenFactory.createToken(accessToken);
		
		String originUrl = String.valueOf(session.getAttribute("origin-url"));
		LOGGER.info("Redirecting to: " + originUrl);
		return new ModelAndView("redirect:" + originUrl + "?jwt=" + tokenString);

	}
	
}
