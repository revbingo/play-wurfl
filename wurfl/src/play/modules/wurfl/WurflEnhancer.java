package play.modules.wurfl;

import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;

import org.apache.commons.lang.StringUtils;

import play.classloading.ApplicationClasses.ApplicationClass;
import play.classloading.enhancers.Enhancer;
import play.classloading.enhancers.PropertiesEnhancer;

public class WurflEnhancer extends Enhancer {

	String[] properties = new String[] { "model_name", "model_extra_info" };

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
