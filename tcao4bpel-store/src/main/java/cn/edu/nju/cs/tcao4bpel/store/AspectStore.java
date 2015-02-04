package cn.edu.nju.cs.tcao4bpel.store;
import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.iapi.ProcessStore;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectStore.java
 */
public interface AspectStore {
	
	
	File getDeployDir();
	void setDeployDir(File deployDir);
	Collection<QName> deployAspect(File deploymentUnitdir, String scope, ProcessStore processStore);
	Collection<QName> undeployAspect(File file);
	Collection<String> getAspectPackages();
	List<QName> listAspects(String packageName);
	List<QName> getAspectList();
	long getCurrentVersion();
	Collection<AspectConfImpl> getAspects();
	AspectConfImpl getAspectConfiguration(QName aspectId);
	
	boolean hasAspect(QName processId);
	
	Collection<AspectConfImpl> getAspects(QName processId);
	
	void registerListener(AspectStoreListener asl);
	void unregisterListner(AspectStoreListener asl);
	
	
	

}
