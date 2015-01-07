package cn.edu.nju.cs.ctao4bpel.store;

import java.io.File;
import java.net.URISyntaxException;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.junit.Test;

import junit.framework.TestCase;

public class AspectStoreTest extends TestCase{
	
	AspectStoreImpl _as;
	private File _testdir;
	
	@Override
	protected void setUp() throws Exception {
		_as = new AspectStoreImpl();
		_testdir=new File(getClass().getResource("/testdd/deploy.xml").toURI().getPath()).getParentFile();
	}
	@Test
	public void testDeployAndUndeploy() throws URISyntaxException{
		
		Collection<QName> qnames = _as.deployAspect(_testdir, "aspect", null);
		assertTrue(qnames.size() ==1);
		assertTrue(qnames.contains(new QName("http://cs.nju.edu.cn/tcao4bpel/2.0/aspect","helloworld")));
		Collection<QName> qnames2 = _as.undeployAspect(_testdir);
		assertTrue(qnames2.size() ==1);
		assertTrue(qnames2.contains(new QName("http://cs.nju.edu.cn/tcao4bpel/2.0/aspect","helloworld")));
		
	}
	

}
