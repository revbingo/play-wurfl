package models.wurfl;

import net.sourceforge.wurfl.core.Device;

public class DeviceDecorator {

	private Device device;
	public String modelName;

	public DeviceDecorator(Device device) {
		this.device = device;
	}

	public String getModelName() {
		return device.getCapability("model_name");
	}

	public Device getDevice() {
		return device;
	}
}
