package play.modules.wurfl;

import play.PlayPlugin;
import play.classloading.ApplicationClasses.ApplicationClass;

public class WurflPlugin extends PlayPlugin {

	@Override
	public void enhance(ApplicationClass applicationClass) throws Exception {
		if(applicationClass.name.equals("models.wurfl.DeviceDecorator")) {
			new WurflEnhancer().enhanceThisClass(applicationClass);
		}
	}
}
