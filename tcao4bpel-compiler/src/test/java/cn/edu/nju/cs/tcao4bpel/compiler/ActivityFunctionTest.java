/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;

import org.apache.ode.bpel.compiler.bom.BpelObjectFactory;
import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.utils.StreamUtils;
import org.junit.Test;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.edu.nju.cs.tcao4bpel.alang.ActivityFunctionImpl;
import cn.edu.nju.cs.tcao4bpel.alang.ActivityFunctionStruct;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn 2015-1-7 2015 ActivityFunctionTest.java
 */
public class ActivityFunctionTest extends TestCase {

	ActivityFunctionImpl afi = null;
	String[] expressions = { "activity(*)",
			"activity(name=\"start\")",
			"activity(type=\"receive\")", 
			"activity(operation=\"hello\")",
			"activity(xpath=\"//bpel:sequence\")",
			"activity(partnerLink=\"helloPartnerLink\")",
			"activity(type =\"receive\", partnerLink= \"helloPartnerLink\", xpath=\"//bpel:receive\")" };
	Process process = null;

	@Override
	protected void setUp() throws Exception {
		File bpelFile = new File(getClass().getResource("HelloWorld2.bpel")
				.getFile());
		BpelObjectFactory factory = new BpelObjectFactory();
		@SuppressWarnings("deprecation")
		InputSource isrc = new InputSource(new ByteArrayInputStream(
				StreamUtils.read(bpelFile.toURL())));
		isrc.setSystemId(bpelFile.getAbsolutePath());
		process = factory.parse(isrc, bpelFile.toURI());

		afi = new ActivityFunctionImpl(process);

		super.setUp();
	}

	@Test
	public void testInterpreter() throws MalformedURLException, IOException,
			SAXException {
		
	
	
		for (String expression : expressions) {
			ActivityFunctionStruct struct = afi.interpreter(expression);
			afi.getActivities(struct);
			
			

		}
	}

}
