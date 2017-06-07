package danielh1307.morestats.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import danielh1307.morestats.entity.Activity;

@Component
public class RepositoryController {

	@Autowired
	private ActivityRepository activityRepo;

	@Transactional
	public void save(Activity activity) {
		activityRepo.save(activity);
	}

	public List<Activity> findByName(String name) {
		return activityRepo.findByName(name);
	}

}
