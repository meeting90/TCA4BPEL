package cn.edu.nju.cs.ctao4bpel.compiler;

import java.net.URL;
import java.util.Set;

import junit.framework.TestCase;
import cn.edu.nju.cs.ctao4bpel.o.OAdvice;
import cn.edu.nju.cs.ctao4bpel.o.OAspect;
import cn.edu.nju.cs.ctao4bpel.o.OPlace;
import cn.edu.nju.cs.ctao4bpel.o.OPointcut;
import cn.edu.nju.cs.ctao4bpel.o.OPostCondition;
import cn.edu.nju.cs.ctao4bpel.o.OPreCondition;

public class CTAO4BPELAspectCompilerTestCase extends TestCase {

	
	private CTAO4BPELAspectCompiler  _compiler;
	private String name;
	private int idx=0;
	private URL _aspectURL;
	
	public CTAO4BPELAspectCompilerTestCase(String name){
		super();
		this.name = name;
	}
	public CTAO4BPELAspectCompilerTestCase(String name, int idx){
		super();
		this.name=name;
		this.idx = idx;
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		_compiler = new CTAO4BPELAspectCompiler(null);
		String filename = name + ((idx >0) ? Integer.toString(idx): "");
		_aspectURL = getClass().getResource(filename + ".aspect");
		
	}
	@Override
	protected void tearDown() throws Exception {
		_compiler=null;
		super.tearDown();
	}
	@Override
	protected void runTest() throws Throwable {

		
		OAspect oaspect = _compiler.compileAspect(_aspectURL, "aspect");
		assertNotNull(oaspect);
		OAdvice oadvice = oaspect.getAdvice();
		assertNotNull(oadvice);
		OPointcut opointcut = oaspect.getPointcut();
		assertNotNull(opointcut);
		OPreCondition opre = opointcut.getPreCondition();
		assertNotNull(opre);
		OPostCondition opost = opointcut.getPostCondition();
		assertNotNull(opost);
		Set<OPlace> p_pre = opre.getPlaces();
		assertTrue(p_pre.size() >0);
		Set<OPlace> p_post = opost.getPlaces();
		assertTrue(p_post.size() >0);
		
	}
	
}
