package cn.edu.nju.cs.ctao4bpel.store;
import javax.xml.namespace.QName;

import org.apache.ode.store.ProcessStoreImpl;

import java.io.File;
import java.util.Collection;
import java.util.List;

public interface AspectStore {
	Collection<QName> deployAspect(File deploymentUnitdir, String scope, ProcessStoreImpl processStore);
	Collection<QName> undeployAspect(File file);
	Collection<String> getAspectPackages();
	List<QName> listAspects(String packageName);
	List<QName> getAspectList();
	long getCurrentVersion();
	Collection<AspectConfImpl> getAspects();
	AspectConfImpl getAspectConfiguration(QName aspectId);
	
	void registerListener(AspectStoreListener asl);
	void unregisterListner(AspectStoreListener asl);
	
	
	

}
