/**
 * 
 */
package cn.edu.nju.cs.ctao4bpel.runtime;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.evt.ActivityDisabledEvent;
import org.apache.ode.bpel.evt.ActivityEnabledEvent;
import org.apache.ode.bpel.evt.ActivityFinishedEvent;
import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.evt.NewProcessInstanceEvent;
import org.apache.ode.bpel.evt.ProcessInstanceEvent;
import org.apache.ode.bpel.evt.ProcessTerminationEvent;

import cn.edu.nju.cs.ctao4bpel.store.AspectStore;
import cn.edu.nju.cs.ctao4bpel.store.AspectStoreImpl;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-15 2015
 * EventListener.java
 */
public class ProcessStateMonitor {
	
	public static final Log log = LogFactory.getLog(ProcessStateMonitor.class);
	/** thread safe **/
	private  static final ProcessStateMonitor INSTANCE = new ProcessStateMonitor();
	
	private AspectStore aspectStore = AspectStoreImpl.getInstance();
	
	/** thread safe **/
	private Map<Long,ProcessInstanceStateMonitor> pisms = new ConcurrentHashMap<Long, ProcessInstanceStateMonitor> ();
	
	
	public static ProcessStateMonitor getInstance() {
		
		return INSTANCE;
	}
		
	
	public void routeEvent(BpelEvent bpelEvent){
		/** filter processes without aspect **/
		if(bpelEvent instanceof ProcessInstanceEvent){
			ProcessInstanceEvent pie = (ProcessInstanceEvent)bpelEvent;
			if(!aspectStore.hasAspect(pie.getProcessId()))
				return;
		}
		
		if(bpelEvent instanceof NewProcessInstanceEvent){
			NewProcessInstanceEvent npie = (NewProcessInstanceEvent) bpelEvent;
			Long instanceId= npie.getProcessInstanceId();
			ProcessInstanceStateMonitor pism = new ProcessInstanceStateMonitor(npie);
			pisms.put(instanceId, pism);
		}else if(bpelEvent instanceof ProcessTerminationEvent){// Process instance terminated remove the process instance state monitor
			ProcessTerminationEvent pte= (ProcessTerminationEvent) bpelEvent;
			Long instanceId = pte.getProcessInstanceId();
			pisms.remove(instanceId);
		}else if(bpelEvent instanceof ActivityEnabledEvent 
				|| bpelEvent instanceof ActivityFinishedEvent
				|| bpelEvent instanceof ActivityDisabledEvent){ 
			/** collect ActivityEnabledEvent && ActivityFinishedEvent && ActivityDisabledEvent **/
			ProcessInstanceEvent pie = (ProcessInstanceEvent)bpelEvent;
			ProcessInstanceStateMonitor pism =pisms.get(pie.getProcessInstanceId());
			if(pism == null){
				log.debug("Process Instance start before aspect deployed, skip applying aspect for this instance");
				return;
			}
			pism.onEvent(pie);
		}
	}


}
