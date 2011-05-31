package models.mobi;

import net.sourceforge.wurfl.core.Device;

public class MobiDevice {

	private Device device;
	public String modelName;

	public MobiDevice(Device device) {
		this.device = device;
	}

	public String getModelName() {
		return device.getCapability("model_name");
	}
}
