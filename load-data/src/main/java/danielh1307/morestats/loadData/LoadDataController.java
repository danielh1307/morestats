package danielh1307.morestats.loadData;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import danielh1307.morestats.entity.Activity;
import danielh1307.morestats.entity.Athlete;
import danielh1307.morestats.loadData.util.StravaCommunicator;

@Controller
@RequestMapping("/")
public class LoadDataController {

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

	@GetMapping("/morestats/authorize")
	public ModelAndView authorize(ModelMap model) {
		// we have to make a request to
		String redirectUrl = "http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=" + HOST + ":" + port
				+ "/morestats/auth&response_type=" + RESPONSE_TYPE + "&scope=" + SCOPE;

		return new ModelAndView("redirect:" + redirectUrl, model);
	}

	@GetMapping("/morestats/auth")
	@ResponseBody
	public String auth(@RequestParam("code") String code) {
		String accessToken = stravaComm.getAccessToken(code);
		Athlete athlete = stravaComm.getCurrentAthlete(accessToken);
		Set<Activity> activitiesForCurrentAthlete = stravaComm.getActivitiesForCurrentAthlete(accessToken, true);
		return "Loaded data for athlete " + athlete + " and " + activitiesForCurrentAthlete.size() + " activities";

	}

}
