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

import play.Play;
import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;
import play.classloading.enhancers.PropertiesEnhancer;
import play.libs.XML;
import play.libs.XPath;
import play.vfs.VirtualFile;

public class WurflEnhancer extends Enhancer {

	List<String> properties = new ArrayList<String>();

	public WurflEnhancer() {
		VirtualFile wurflFile = Play.getVirtualFile("conf/wurfl.xml");
		if(!wurflFile.exists()) {
			System.out.println("~ Warning: conf/wurfl.xml does not exist, not enhancing");
			return;
		}

		Document document = XML.getDocument(Play.getVirtualFile("conf/wurfl.xml").getRealFile());
		Node genericDeviceNode = XPath.selectNode("/wurfl/devices/device[@id='generic']", document);
		if(genericDeviceNode == null) {
			System.out.println("~ Warning: cannot get generic device node from wurfl.xml, not enhancing");
			return;
		}

		for(Node capabilityNode : XPath.selectNodes("group/capability", genericDeviceNode)) {
			String property = ((Element) capabilityNode).getAttribute("name");
			properties.add(property);
		}
	}

	@Override
	public void enhanceThisClass(ApplicationClass applicationClass) throws Exception {
		CtClass ctClass = makeClass(applicationClass);

		for(String property : properties) {

			CtField field = CtField.make("public String " + property + ";", ctClass);
			ctClass.addField(field);

			CtMethod method = CtMethod.make(String.format("public String get%s() {  return device.getCapability(\"%s\"); }", StringUtils.capitalize(property), property), ctClass);
			ctClass.addMethod(method);
		}

		applicationClass.enhancedByteCode = ctClass.toBytecode();
		ctClass.defrost();

		new PropertiesEnhancer().enhanceThisClass(applicationClass);
	}
}
