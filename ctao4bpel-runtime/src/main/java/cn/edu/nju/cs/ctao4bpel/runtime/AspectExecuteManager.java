/**
 * 
 */
package cn.edu.nju.cs.ctao4bpel.runtime;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.namespace.QName;

import cn.edu.nju.cs.ctao4bpel.o.OAspect;
import cn.edu.nju.cs.ctao4bpel.o.OPlace;
import cn.edu.nju.cs.ctao4bpel.store.AspectConfImpl;
import cn.edu.nju.cs.ctao4bpel.store.AspectStore;
import cn.edu.nju.cs.ctao4bpel.store.AspectStoreImpl;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-15 2015
 * SyncManager.java
 */
public class AspectExecuteManager{
	enum TokenState{
		UNKONWN,
		TRUE,
		FALSE
	}
	

	private QName aspectId;
	
	private OAspect oaspect;
	
	private  AspectStore aspectStore =AspectStoreImpl.getInstance();

	private  Map<OPlace,TokenState> tokenCollector=null;
	
	protected AspectExecuteManager(QName aspectId){
		this.aspectId = aspectId;
		
		
	}
	
	private OAspect getOAspect(){
		if(oaspect == null){
			AspectConfImpl aspectConf = aspectStore.getAspectConfiguration(this.aspectId);
			oaspect = aspectConf.getOaspect();
		}
		return oaspect;
		
	}
	public Map<OPlace,TokenState> initPreConTokenCollector(){
		if(tokenCollector == null){
			tokenCollector = new ConcurrentHashMap<OPlace, AspectExecuteManager.TokenState>();
			for(OPlace oplace : getOAspect().getPointcut().getPreCondition().getPlaces()){
				tokenCollector.put(oplace, TokenState.UNKONWN);
			}
		}
		return tokenCollector;
		
	}

	public  Set<OPlace> getPostCondition(){
		return getOAspect().getPointcut().getPostCondition().getPlaces();
	}
	
	public void evaluatePreCondition(Map<OPlace,TokenState> preTokens){
		
	}
	
	public void activeAspect(){
		
	}
	public void deactiveAspect(){
		
	}
	
	public void blockACTIVITY(){
		
	}
	
	public void relaseACTIVITY(){
		
	}
	
	
	
	
	



	


}
