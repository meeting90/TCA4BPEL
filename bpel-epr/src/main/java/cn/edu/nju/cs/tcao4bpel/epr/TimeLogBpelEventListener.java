package cn.edu.nju.cs.tcao4bpel.epr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.common.evt.DebugBpelEventListener;
import org.apache.ode.bpel.evt.BpelEvent;
import org.apache.ode.bpel.evt.ProcessCompletionEvent;
import org.apache.ode.bpel.evt.ProcessInstanceStartedEvent;

/**
 * 
 */

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-2-11 2015
 * TimeLogBpelEventListener.java
 */
public class TimeLogBpelEventListener extends DebugBpelEventListener {
   private static final Log __log = LogFactory.getLog(TimeLogBpelEventListener.class);

	@Override
	public void onEvent(BpelEvent bpelEvent) {
		if(bpelEvent instanceof ProcessInstanceStartedEvent)
			__log.info(bpelEvent.toString());
		if(bpelEvent instanceof ProcessCompletionEvent)
			__log.info(bpelEvent.toString());	
	}

}
