package cn.edu.nju.cs.ctao4bpel.compiler;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class AspectCompilerTestSuite extends TestCase{
	public static Test suite(){
		TestSuite suite = new TestSuite();
		suite.setName("AspectCompilerTestSuite");
		suite.addTest(new AspectObjectFactoryTestCase("test"));
	
		suite.addTest(new CTAO4BPELAspectCompilerTestCase("test"));
		return suite;
	}
}
