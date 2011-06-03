package controllers.wurfl;

import models.wurfl.DeviceDecorator;
import net.sourceforge.wurfl.core.DefaultDeviceProvider;
import net.sourceforge.wurfl.core.DefaultWURFLManager;
import net.sourceforge.wurfl.core.DefaultWURFLService;
import net.sourceforge.wurfl.core.Device;
import net.sourceforge.wurfl.core.WURFLManager;
import net.sourceforge.wurfl.core.handlers.matchers.MatcherManager;
import net.sourceforge.wurfl.core.resource.DefaultWURFLModel;
import net.sourceforge.wurfl.core.resource.WURFLModel;
import net.sourceforge.wurfl.core.resource.XMLResource;
import play.Play;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.Http;
import play.vfs.VirtualFile;

public class WurflAware extends Controller {

	private static WURFLManager manager;
	private static boolean haveWurflFile = false;

	static {
		VirtualFile wurflFile = Play.getVirtualFile("conf/wurfl.xml");
		if(haveWurflFile = (wurflFile != null && wurflFile.exists())) {
			WURFLModel model = new DefaultWURFLModel(new XMLResource(Play.getVirtualFile("conf/wurfl.xml").getRealFile()));
			manager = new DefaultWURFLManager(new DefaultWURFLService(new MatcherManager(model), new DefaultDeviceProvider(model)));
		}
	}

	@Before
	public static void checkUAHeader() {
		if(!haveWurflFile) return;

		Http.Header uaHeader = request.headers.get("user-agent");
		if(uaHeader != null) {
			String ua = uaHeader.value();
			Device device = manager.getDeviceForRequest(ua);

			if(device.getCapability("is_wireless_device").equals("true")) {
				request.format = "mobi";
				response.contentType = "text/html";
				renderArgs.put("device", new DeviceDecorator(device));
			}

		}
	}
}