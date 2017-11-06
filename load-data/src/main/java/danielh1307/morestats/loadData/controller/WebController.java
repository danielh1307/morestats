package danielh1307.morestats.loadData.controller;

import danielh1307.morestats.loadData.dto.AthleteDto;
import danielh1307.morestats.loadData.dto.AuthScs;
import danielh1307.morestats.loadData.dto.WriteStoreTotal;
import danielh1307.morestats.loadData.entity.Activity;
import danielh1307.morestats.loadData.entity.Athlete;
import danielh1307.morestats.loadData.repository.ActivityRepository;
import danielh1307.morestats.loadData.util.JwtHandler;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class WebController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebController.class);

    private static final String LOAD_DATA_HOST = "http://localhost:8080";
    private static final String SUCC_AUTH_PATH = "/morestats/loadStravaData";

    @Value("${auth.url}")
    private String authScs;

    @Autowired
    private JwtHandler tokenFactory;

    private final StravaCommunicator stravaComm;

    @Autowired
    private ActivityRepository activityRepository;

    @Autowired
    public WebController(StravaCommunicator stravaComm) {
        this.stravaComm = stravaComm;
    }

    /**
     * Directs the user to the welcome page. To log in, the host of the auth SCS and the origin-url is needed, so
     * this is part of the returned model.
     *
     * @return ModelAndView for the welcome page.
     */
    @RequestMapping("/morestats")
    @ResponseBody
    public ModelAndView home() {
        AuthScs a = new AuthScs(authScs, LOAD_DATA_HOST + SUCC_AUTH_PATH);
        return new ModelAndView("welcome", "authScs", a);
    }

    /**
     * After successful authentication, browser is redirected to /morestats/loaddata, the JWT is part of the URL.
     * This method returns the JWT and the name of the athlete.
     *
     * @param jwt the generated JSON Web Token.
     * @return ModelAndView for loadata page.
     */
    @RequestMapping(SUCC_AUTH_PATH)
    public ModelAndView loadStravaData(@RequestParam String jwt) {
        // get access token
        String stravaAccessToken = tokenFactory.getStravaAccessToken(jwt);

        // load athlete from Strava
        Athlete stravaAthlete = stravaComm.getCurrentAthlete(stravaAccessToken);

        // create the DTO for the view
        AthleteDto dto = new AthleteDto(jwt, stravaAthlete.toString());

        return new ModelAndView("loadStravaData", "athlete", dto);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/morestats/activity/{jwt:.+}")
    public ModelAndView getActivity(@PathVariable String jwt) {
        LOGGER.info("Token is " + jwt);

        // get all activities for the current athlete
        String stravaAccessToken = tokenFactory.getStravaAccessToken(jwt);
        Athlete athlete = stravaComm.getCurrentAthlete(stravaAccessToken);
        LOGGER.info("Get all write store data for athlete " + athlete);

        int numOfSegments = 0;
        Iterable<Activity> activities = activityRepository.findAll();
        for (Activity a : activities) {
            numOfSegments += a.getSegments().size();
        }

        LOGGER.info("Number of segments are " + numOfSegments);
        WriteStoreTotal total = new WriteStoreTotal(activityRepository.count(), numOfSegments, jwt);

        return new ModelAndView("writeStoreTotal", "writeStore", total);
    }

}
