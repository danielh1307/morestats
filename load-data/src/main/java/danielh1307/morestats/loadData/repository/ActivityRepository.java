package danielh1307.morestats.loadData.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import danielh1307.morestats.loadData.entity.Activity;

@Repository
public interface ActivityRepository extends CrudRepository<Activity, Long> {

	public List<Activity> findByName(String name);	
}
