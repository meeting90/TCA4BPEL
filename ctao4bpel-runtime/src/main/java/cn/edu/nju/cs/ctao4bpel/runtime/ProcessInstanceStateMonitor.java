/**
 * 
 */
package cn.edu.nju.cs.ctao4bpel.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.evt.ActivityDisabledEvent;
import org.apache.ode.bpel.evt.ActivityEnabledEvent;
import org.apache.ode.bpel.evt.ActivityFinishedEvent;
import org.apache.ode.bpel.evt.NewProcessInstanceEvent;
import org.apache.ode.bpel.evt.ProcessInstanceEvent;
import org.apache.ode.bpel.o.OActivity;

import cn.edu.nju.cs.ctao4bpel.o.OAspect;
import cn.edu.nju.cs.ctao4bpel.o.OPlace;
import cn.edu.nju.cs.ctao4bpel.store.AspectConfImpl;
import cn.edu.nju.cs.ctao4bpel.store.AspectStore;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-15 2015
 * ProcessInstanceStateMonitor.java
 */
public class ProcessInstanceStateMonitor {

	private AspectStore aspectStore;
	
	private NewProcessInstanceEvent event;
	
	private List<AspectConfImpl> _aspects;
	
	
	
	
	private AspectExecuteManager  executeManager;
	public ProcessInstanceStateMonitor(NewProcessInstanceEvent npie) {
		this.event  = npie;
	
		
		
	}


	public void onEvent(ProcessInstanceEvent bpelEvent) {
				
		
	}

	private void collectTokensForPreCon(ProcessInstanceEvent bpelEvent){
		
	}
	
	private void checkCollectedTokensForPostCon(ProcessInstanceEvent bpelEvent){
		
	}
	
	private void collectDisabledTokensForPreCon(ProcessInstanceEvent bpelEvent){
		
	}

	
	public Long getInstanceId() {
		
		return this.event.getProcessInstanceId();
	}
	
	public List<AspectConfImpl> getRelatedAspects(){
		if(_aspects == null){
			_aspects = new ArrayList<AspectConfImpl>();
			Collection<AspectConfImpl> allAspects = aspectStore.getAspects();
			for(AspectConfImpl aspectConf: allAspects){
				if(aspectConf.getOaspect().getProcessId().equals(event.getProcessId()))
					_aspects.add(aspectConf);	
				
			}
		}
		return _aspects;
	}

	
	

}
