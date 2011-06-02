package models.wurfl;

import net.sourceforge.wurfl.core.Device;

public class DeviceDecorator {

	private Device device;

	public DeviceDecorator(Device device) {
		this.device = device;
	}

	public Device getDevice() {
		return device;
	}
}
