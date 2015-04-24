/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.evt.ActivityDisabledEvent;
import org.apache.ode.bpel.evt.ActivityEvent;
import org.apache.ode.bpel.evt.ActivityExecEndEvent;
import org.apache.ode.bpel.evt.ActivityExecStartEvent;
import org.apache.ode.bpel.o.OActivity;

import cn.edu.nju.cs.tcao4bpel.o.OPlace;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-31 2015
 * ProcessStateMonitor.java
 */
public class ProcessStateMonitor {

	private static final Log __log = LogFactory.getLog(ProcessStateMonitor.class);

	/**
	 * @param event
	 * @param activity
	 * @param _aspectFrame
	 */
	public static void routeEvent(ActivityEvent event,
			AspectFrame aspectFrame, OActivity o) {		
		if(aspectFrame == null) {// aspect itself
			__log.debug(event.getActivityName());
			return;
		}
		for(AspectInfo aspectInfo: aspectFrame.getAspectInfos()){	
			for (OPlace oplace: aspectInfo.oaspect.getPointcut().getPreCondition().getPlaces()){
				
				if(oplace.getXpaths().get(0).equals(o.getXpath())){ //matched
					if (event instanceof ActivityExecStartEvent){
						if(oplace.getState() == OPlace.State.READY){
							__log.debug("releaseToken: start"+ event.getActivityName());
							
							aspectInfo.resolvePre(oplace).conditionStatus(true);
						}
					}	
					else if(event instanceof ActivityExecEndEvent){
						if(oplace.getState() == OPlace.State.FINISHED){
							__log.debug("releaseToken: end"+ event.getActivityName());
							aspectInfo.resolvePre(oplace).conditionStatus(true);
						}
					}else if(event instanceof ActivityDisabledEvent){
						__log.debug("releaseToken: disabled"+ event.getActivityName());
						aspectInfo.resolvePre(oplace).conditionStatus(false);
						
					}
				}
			}
			
		}
		
		
	}

	
}
