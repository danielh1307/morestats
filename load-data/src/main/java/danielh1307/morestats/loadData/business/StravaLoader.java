package danielh1307.morestats.loadData.business;

import danielh1307.morestats.loadData.entity.Activity;
import danielh1307.morestats.loadData.entity.Athlete;
import danielh1307.morestats.loadData.repository.ActivityRepository;
import danielh1307.morestats.loadData.util.JwtHandler;
import danielh1307.morestats.loadData.util.StravaCommunicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.Future;

@Component
public class StravaLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(StravaLoader.class);

    @Autowired
    private JwtHandler tokenFactory;

    @Autowired
    private StravaCommunicator stravaComm;

    @Autowired
    private ActivityRepository activityRepository;

    @Async
    public Future<String> loadStravaDataAsync(String jwtToken) {
        boolean withSegments = true;

        String stravaAccessToken = tokenFactory.getStravaAccessToken(jwtToken);

        Athlete athlete = stravaComm.getCurrentAthlete(stravaAccessToken);
        Set<Activity> activitiesForCurrentAthlete = stravaComm.getActivitiesForCurrentAthlete(stravaAccessToken,
                withSegments);
        int segmentCount = 0;
        // TODO: use lambdas here
        if (withSegments) {
            for (Activity a : activitiesForCurrentAthlete) {
                segmentCount += a.getSegments().size();
            }
        }

        // TODO: fire domain event
        activityRepository.save(activitiesForCurrentAthlete);

        // TODO: return JSON here
        return new AsyncResult<String>("Es wurden " + activitiesForCurrentAthlete.size() + " Aktivitäten mit " +
                segmentCount + " Segmenten in den write store geladen");
    }

    public String loadStravaDataSync(String jwtToken) {
        boolean withSegments = true;

        String stravaAccessToken = tokenFactory.getStravaAccessToken(jwtToken);

        Athlete athlete = stravaComm.getCurrentAthlete(stravaAccessToken);
        Set<Activity> activitiesForCurrentAthlete = stravaComm.getActivitiesForCurrentAthlete(stravaAccessToken,
                withSegments);
        int segmentCount = 0;
        // TODO: use lambdas here
        if (withSegments) {
            for (Activity a : activitiesForCurrentAthlete) {
                segmentCount += a.getSegments().size();
            }
        }

        // TODO: fire domain event
        activityRepository.save(activitiesForCurrentAthlete);

        // TODO: return JSON here, we should always return JSON in the API controller
        return "Es wurden " + activitiesForCurrentAthlete.size() + " Aktivitäten mit " + segmentCount + " Segmenten " +
                "in den write store geladen";
    }
}
