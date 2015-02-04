/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

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

	/**
	 * @return
	 */
	public static ProcessStateMonitor getInstance() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @param event
	 * @param activity
	 * @param _aspectFrame
	 */
	public void routeEvent(ActivityEvent event,
			AspectFrame aspectFrame, OActivity o) {		
		for(AspectInfo aspectInfo: aspectFrame.getAspectInfos()){	
			for (OPlace oplace: aspectInfo.oaspect.getPointcut().getPreCondition().getPlaces()){
				if(oplace.getXpaths().get(0).equals(o.getXpath())){ //matched
					if (event instanceof ActivityExecStartEvent){
						if(oplace.getState() == OPlace.State.READY)
							aspectInfo.resolvePre(oplace).conditionStatus(true);
					}	
					else if(event instanceof ActivityExecEndEvent){
						if(oplace.getState() == OPlace.State.FINISHED)
							aspectInfo.resolvePre(oplace).conditionStatus(true);
					}else if(event instanceof ActivityDisabledEvent){
						if(oplace.getState() == OPlace.State.READY)
							aspectInfo.resolvePre(oplace).conditionStatus(false);
						else
							aspectInfo.resolvePost(oplace).conditionStatus(false);
					}
				}
			}
			
		}
		
		
	}

	
}
