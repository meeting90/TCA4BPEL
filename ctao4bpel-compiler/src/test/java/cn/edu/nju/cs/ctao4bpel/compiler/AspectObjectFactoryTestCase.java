package cn.edu.nju.cs.ctao4bpel.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URL;

import org.apache.ode.utils.StreamUtils;
import org.xml.sax.InputSource;

import cn.edu.nju.cs.ctao4bpel.compiler.bom.Advice;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Aspect;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.AspectObjectFactory;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Pointcut;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.PostCondition;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.PreCondition;

import junit.framework.TestCase;
public class AspectObjectFactoryTestCase extends TestCase{
	
	private AspectObjectFactory _factory;
	private String name;
	private int idx=0;
	private URL _aspectURL;
	
	public AspectObjectFactoryTestCase(String name){
		super();
		this.name = name;
	}
	public AspectObjectFactoryTestCase(String name, int idx){
		super();
		this.name=name;
		this.idx = idx;
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		_factory = AspectObjectFactory.getInstance();
		String filename = name + ((idx >0) ? Integer.toString(idx): "");
		_aspectURL = getClass().getResource(filename + ".aspect");
	}
	@Override
	protected void tearDown() throws Exception {
		_factory=null;
		super.tearDown();
	}
	
	@Override
	protected void runTest() throws Throwable {
		File aspectFile = new File(_aspectURL.getFile());
		@SuppressWarnings("deprecation")
		InputSource isrc = new InputSource(new ByteArrayInputStream(StreamUtils.read(aspectFile.toURL())));
		isrc.setSystemId(aspectFile.getAbsolutePath());
		Aspect  aspect = _factory.parseAspect(isrc, aspectFile.toURI());
		assertNotNull(aspect);
		Advice advice= aspect.getAdvice();
		assertNotNull(advice);
		Pointcut pointcut = aspect.getPointcut();
		assertNotNull(pointcut);
		PreCondition precondition = pointcut.getPreCondition();
		assertNotNull(precondition);
		PostCondition postcondition = pointcut.getPostCondition();
		assertNotNull(postcondition);
		assertNotNull(precondition.getPlaces());
		assertNotNull(postcondition.getPlaces());
	}
	

}
