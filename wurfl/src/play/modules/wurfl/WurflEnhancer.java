package play.modules.wurfl;

import java.util.ArrayList;
import java.util.List;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import play.Logger;
import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;
import play.classloading.enhancers.PropertiesEnhancer;
import play.libs.XML;
import play.libs.XPath;
import play.vfs.VirtualFile;

public class WurflEnhancer extends Enhancer {

	List<Property> properties = new ArrayList<Property>();

	private abstract class Property {
		public String name;
		public String getDelegateMethod() {
			return String.format("public " + getReturnType() + " get%s() {  " + getMethodBody("device.getCapability(\"%s\")") + " }", StringUtils.capitalize(name), name);
		}
		abstract String getMethodBody(String inner);
		abstract String getReturnType();
	}

	private class StringProperty extends Property {
		@Override
		public String getMethodBody(String inner) {
			return "return " + inner + ";";
		}

		@Override
		public String getReturnType() {
			return String.class.getSimpleName();
		}
	}

	private class IntProperty extends Property {
		@Override
		public String getMethodBody(String inner) {
			return "return Integer.parseInt(" + inner + ");";
		}

		@Override
		public String getReturnType() {
			return "int";
		}
	}

	private class BooleanProperty extends Property {

		@Override
		public String getMethodBody(String inner) {
			return "return Boolean.parseBoolean(" + inner + ");";
		}

		@Override
		public String getReturnType() {
			return "boolean";
		}
	}

	public WurflEnhancer() {
		VirtualFile wurflFile = Play.getVirtualFile("conf/wurfl.xml");
		if(wurflFile == null || !wurflFile.exists()) {
			Logger.warn("WARNING: conf/wurfl.xml does not exist, not enhancing");
			Logger.warn("Try play wurfl:install to install the file");
			return;
		}

		Document document = XML.getDocument(Play.getVirtualFile("conf/wurfl.xml").getRealFile());
		Node genericDeviceNode = XPath.selectNode("/wurfl/devices/device[@id='generic']", document);
		if(genericDeviceNode == null) {
			Logger.warn("WARNING: cannot get generic device node from wurfl.xml, not enhancing");
			Logger.warn("Try play wurfl:install to install the file");
			return;
		}

		for(Node capabilityNode : XPath.selectNodes("group/capability", genericDeviceNode)) {
			String propertyName = ((Element) capabilityNode).getAttribute("name");
			String defaultValue = ((Element) capabilityNode).getAttribute("value");
			Property property = null;
			if(defaultValue.equalsIgnoreCase("true") || defaultValue.equalsIgnoreCase("false")) {
				property = new BooleanProperty();
			} else {
				try {
					Integer.parseInt(defaultValue);
					property = new IntProperty();
				} catch(NumberFormatException e) {
						property = new StringProperty();
				}
			}
			property.name = propertyName;
			properties.add(property);
		}
	}

	@Override
	public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
		CtClass ctClass = makeClass(applicationClass);

		for(Property property : properties) {

			CtField field = CtField.make("public String " + property.name + ";", ctClass);
			ctClass.addField(field);

			CtMethod method = CtMethod.make(property.getDelegateMethod(), ctClass);
			ctClass.addMethod(method);
		}

		applicationClass.enhancedByteCode = ctClass.toBytecode();
		ctClass.defrost();

		new PropertiesEnhancer().enhanceThisClass(applicationClass);
	}
}
