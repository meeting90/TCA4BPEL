/**
 * 
 */
package cn.edu.nju.cs.ctao4bpel.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import cn.edu.nju.cs.ctao4bpel.store.AspectStore;
import cn.edu.nju.cs.ctao4bpel.store.AspectStoreEvent;
import cn.edu.nju.cs.ctao4bpel.store.AspectStoreImpl;
import cn.edu.nju.cs.ctao4bpel.store.AspectStoreListener;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-16 2015
 * SynchronizeManager.java
 */
public class SynchronizeManager implements AspectStoreListener {
	
	
	private AspectStore aspectStore= AspectStoreImpl.getInstance(); 
	private  Map<QName,AspectExecuteManager> executeManagers = new ConcurrentHashMap<QName, AspectExecuteManager>();
	
	
	public SynchronizeManager(){
		initManager();
	}
	private  void addInstance(QName aspectId){
		AspectExecuteManager manager = new AspectExecuteManager(aspectId);
		executeManagers.put(aspectId, manager);
	}
	private  void removeInstance(QName aspectId){
		executeManagers.remove(aspectId);
	}
	private  void initManager(){
		for (QName aspectId:aspectStore.getAspectList()){
			addInstance(aspectId);
		}
	}

	@Override
	public void onApsectStoreEvent(AspectStoreEvent event) {
		switch(event.type){
		case DEPLOYED:
			addInstance(event.aid);
			break;
		case UNDEPLOYED:
			removeInstance(event.aid);
			break;
		}
	}

}
