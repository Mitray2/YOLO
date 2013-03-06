package jobs;

import play.jobs.Job;
/*import models.BSphere;
import models.BType;
import models.Country;
import models.ProjectPhase;
import models.Test;
import models.User;
import models.UserLevel;

import play.jobs.OnApplicationStart;
import play.test.Fixtures;*/

//@OnApplicationStart
public class CreateInitialData extends Job {

	/*@Override  NOTE: commented by siarzh; CAUSE: this data is on DB already
	public void doJob() throws Exception {
		super.doJob();
		if (User.count() == 0) {
			Fixtures.loadModels("data.yml");
		}
		if (Test.count() == 0) {
			Fixtures.loadModels("tests.yml");
		}
		if (Country.count() == 0) {
			Fixtures.loadModels("countries.yml");
		}
		if (BSphere.count() == 0) {
			Fixtures.loadModels("spheres.yml");
		}
		if (BType.count() == 0) {
			Fixtures.loadModels("bType.yml");
		}
		if (ProjectPhase.count() == 0) {
			Fixtures.loadModels("phase.yml");
		}
		if(UserLevel.count() == 0) {
			Fixtures.loadModels("levels.yml");
		}
	}*/

}
