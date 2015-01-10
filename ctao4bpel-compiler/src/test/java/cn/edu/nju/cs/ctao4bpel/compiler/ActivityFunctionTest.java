/**
 * 
 */
package cn.edu.nju.cs.ctao4bpel.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.bpel.compiler.bom.BpelObjectFactory;
import org.apache.ode.utils.StreamUtils;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.edu.nju.cs.ctao4bpel.alang.ActivityFunctionImpl;
import cn.edu.nju.cs.ctao4bpel.alang.ActivityFunctionStruct;
import junit.framework.TestCase;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn 2015-1-7 2015 ActivityFunctionTest.java
 */
public class ActivityFunctionTest extends TestCase {

	ActivityFunctionImpl afi = null;
	String[] expressions = { "activity(*)", "activity(name=\"helloworld\")",
			"activity(type=\"invoke\")", "activity(operation=\"echo\")",
			"activity(xpath=\"//bpel:sequence\")",
			"activity(partnerlink=\"helloworld\")",
			"activity(type =\"invoke\", partnerlink= \"helloworld\", xpath=\"//bpel:sequence\")" };
	Process process = null;

	@Override
	protected void setUp() throws Exception {

		afi = new ActivityFunctionImpl();

		super.setUp();
	}

	@Test
	public void testInterpreter() throws MalformedURLException, IOException,
			SAXException {
		File bpelFile = new File(getClass().getResource("HelloWorld2.bpel")
				.getFile());
		BpelObjectFactory factory = new BpelObjectFactory();
		@SuppressWarnings("deprecation")
		InputSource isrc = new InputSource(new ByteArrayInputStream(
				StreamUtils.read(bpelFile.toURL())));
		isrc.setSystemId(bpelFile.getAbsolutePath());
		process = factory.parse(isrc, bpelFile.toURI());
	
	
		for (String expression : expressions) {
			ActivityFunctionStruct struct = afi.interpreter(expression);
			afi.getActivities(struct, process);

		}
	}

}
