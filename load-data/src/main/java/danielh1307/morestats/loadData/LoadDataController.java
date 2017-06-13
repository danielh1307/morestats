package danielh1307.morestats.loadData;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.loadData.util.ResponseString;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import danielh1307.morestats.loadData.util.TokenContainer;

@Controller
@RequestMapping("/")
public class LoadDataController {

	private static Logger LOGGER = LoggerFactory.getLogger(LoadDataController.class);

	private static final String HOST = "http://localhost";
	private static final String SCOPE = "view_private";
	private static final String RESPONSE_TYPE = "code";

	@Value("${server.port}")
	private String port;

	private final StravaCommunicator stravaComm;

	@Autowired
	public LoadDataController(StravaCommunicator stravaComm) {
		this.stravaComm = stravaComm;
	}

	@RequestMapping("/morestats")
	@ResponseBody
	public String home() {
		return "Welcome to morestats: <a href=\"" + HOST + ":" + port + "/morestats/authorize\">Anmelden</a>";
	}

	@RequestMapping("/morestats/authorize")
	public ModelAndView authorize(ModelMap model) {
		// we have to make a request to
		String redirectUrl = "http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=" + HOST + ":" + port
				+ "/morestats/auth&response_type=" + RESPONSE_TYPE + "&scope=" + SCOPE;

		return new ModelAndView("redirect:" + redirectUrl, model);
	}

	@RequestMapping("/morestats/auth")
	public ModelAndView auth(@RequestParam("code") String code) {
		String accessToken = stravaComm.getAccessToken(code);
		LOGGER.info("Successfully logged in with accessToken [" + accessToken + "]");
		// return "You are now logged in. <a href=\"" + HOST + ":" + port +
		// "/morestats/loaddata?accessToken="
		// + accessToken + "\">Load your data</a>";
		// muss Referenz auf loadData.html zurückschicken
		return new ModelAndView("loadData");

	}

	@MessageMapping("/loaddata")
	@SendTo("/topic/data")
	public ResponseString getData(TokenContainer tokenContainer) {
		LOGGER.info("Method getData called");
		Athlete athlete = stravaComm.getCurrentAthlete(tokenContainer.getAccessToken());
		Set<Activity> activitiesForCurrentAthlete = stravaComm
				.getActivitiesForCurrentAthlete(tokenContainer.getAccessToken(), false);
		LOGGER.info("Returning getData");
		return new ResponseString(
				"Loaded data for athlete " + athlete + " and " + activitiesForCurrentAthlete.size() + " activities");
	}

}
