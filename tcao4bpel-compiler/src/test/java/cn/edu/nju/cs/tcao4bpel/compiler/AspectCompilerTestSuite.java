package cn.edu.nju.cs.tcao4bpel.compiler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectCompilerTestSuite.java
 */
public class AspectCompilerTestSuite extends TestCase{
	public static Test suite(){
		TestSuite suite = new TestSuite();
		suite.setName("AspectCompilerTestSuite");
		suite.addTest(new AspectObjectFactoryTestCase("test"));
	
		suite.addTest(new TCAO4BPELAspectCompilerTestCase("test"));
		return suite;
	}
}
