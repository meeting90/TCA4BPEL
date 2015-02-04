package cn.edu.nju.cs.tcao4bpel.store;

import java.io.File;

import javax.xml.namespace.QName;

import org.apache.ode.store.DocumentRegistry;
import org.junit.Test;

import cn.edu.nju.cs.tcao4bpel.store.AspectDeploymentUnitDir;

import junit.framework.TestCase;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectDeploymentUnitTest.java
 */

public class AspectDeploymentUnitTest extends TestCase{
	AspectDeploymentUnitDir du;
	
	@Override
	protected void setUp() throws Exception {
		File dir = new File(getClass().getResource("/testdd/deploy.xml").toURI().getPath()).getParentFile();
		du = new AspectDeploymentUnitDir(dir);
	}
	@Test
	public void testRegistry(){
		DocumentRegistry dr = du.getDocRegistry();
		assertNotNull(dr.getDefinitionForPortType(new QName("http://cn.edu.nju.cs/ctao4bpel/aspect/unit-test.wsdl", "HelloPortType")));
	}
	@Test
	public void  testCompile(){
		du.compile("aspect", null);
		
	}
	@Test
	public void testGetCbaInfo(){
		assertNotNull(du.getCBAInfo(new QName("http://cs.nju.edu.cn/tcao4bpel/2.0/aspect","helloworld")));
	}
	

}
