package danielh1307.morestats.loadData;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class LoadDataController {

	@RequestMapping("/morestats")
	@ResponseBody
	public String home() {
		return "Welcome to morestats: <a href=\"http://localhost:8080/morestats/authorize\">Anmelden</a>";
	}

	@GetMapping("/morestats/authorize")
	public ModelAndView authorize(ModelMap model) {
		// we have to make a request to
		String redirectUrl = "http://www.strava.com/oauth/authorize?client_id=18287&redirect_uri=http://localhost/morestats/auth&response_type=code&scope=view_private";

		return new ModelAndView("redirect:" + redirectUrl, model);
	}
	
	@GetMapping("/morestats/auth")
	@ResponseBody
	public String auth(@RequestParam("code") String code) {
		return "Code is: " + code;
	}

}
