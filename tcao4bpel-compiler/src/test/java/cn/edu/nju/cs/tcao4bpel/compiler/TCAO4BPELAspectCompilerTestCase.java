package cn.edu.nju.cs.tcao4bpel.compiler;

import java.net.URL;
import java.util.Set;

import junit.framework.TestCase;
import cn.edu.nju.cs.tcao4bpel.compiler.TCAO4BPELAspectCompiler;
import cn.edu.nju.cs.tcao4bpel.o.OAdvice;
import cn.edu.nju.cs.tcao4bpel.o.OAspect;
import cn.edu.nju.cs.tcao4bpel.o.OPlace;
import cn.edu.nju.cs.tcao4bpel.o.OPointcut;
import cn.edu.nju.cs.tcao4bpel.o.OPostCondition;
import cn.edu.nju.cs.tcao4bpel.o.OPreCondition;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * TCAO4BPELAspectCompilerTestCase.java
 */
public class TCAO4BPELAspectCompilerTestCase extends TestCase {

	
	private TCAO4BPELAspectCompiler  _compiler;
	private String name;
	private int idx=0;
	private URL _aspectURL;
	
	public TCAO4BPELAspectCompilerTestCase(String name){
		super();
		this.name = name;
	}
	public TCAO4BPELAspectCompilerTestCase(String name, int idx){
		super();
		this.name=name;
		this.idx = idx;
	}
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		
		_compiler = new TCAO4BPELAspectCompiler(null);
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
