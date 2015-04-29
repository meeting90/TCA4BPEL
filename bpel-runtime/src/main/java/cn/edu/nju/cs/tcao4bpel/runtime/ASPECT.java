/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import static org.apache.ode.jacob.ProcessUtil.compose;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.evt.AspectInstanceEndEvent;
import org.apache.ode.bpel.evt.AspectTriggeredEvent;
import org.apache.ode.bpel.evt.NewAspectInstanceEvent;
import org.apache.ode.bpel.o.OScope;
import org.apache.ode.bpel.runtime.ACTIVITY;
import org.apache.ode.bpel.runtime.ActivityInfo;
import org.apache.ode.bpel.runtime.ActivityTemplateFactory;
import org.apache.ode.bpel.runtime.BpelJacobRunnable;
import org.apache.ode.bpel.runtime.CompensationHandler;
import org.apache.ode.bpel.runtime.LinkFrame;
import org.apache.ode.bpel.runtime.ScopeFrame;
import org.apache.ode.bpel.runtime.channels.FaultData;
import org.apache.ode.bpel.runtime.channels.ParentScope;
import org.apache.ode.bpel.runtime.channels.Termination;
import org.apache.ode.jacob.CompositeProcess;
import org.apache.ode.jacob.ReceiveProcess;
import org.apache.ode.jacob.Synch;
import org.w3c.dom.Element;

import cn.edu.nju.cs.tcao4bpel.o.OAdvice;
import cn.edu.nju.cs.tcao4bpel.o.OPlace;
import cn.edu.nju.cs.tcao4bpel.store.AspectConfImpl;
import cn.edu.nju.cs.tcao4bpel.store.AspectStore;
import cn.edu.nju.cs.tcao4bpel.store.AspectStoreImpl;


/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-2-3 2015
 * ASPECT.java
 */
public class ASPECT extends ACTIVITY{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1096879033471455891L;


	/**
	 * @param self
	 * @param scopeFrame
	 * @param linkFrame
	 * @param aspectFrame
	 */
	public ASPECT(ActivityInfo self, ScopeFrame scopeFrame,
			LinkFrame linkFrame, AspectFrame aspectFrame, AspectInfo aspectInfo, Map<OPlace, Boolean> preValues) {
		super(self, scopeFrame, linkFrame, aspectFrame);
		_aspectInfo=aspectInfo;
		_preValues= preValues;
		
	
	}
	public ASPECT(ActivityInfo self, ScopeFrame scopeFrame,
			LinkFrame linkFrame, AspectFrame aspectFrame, AspectInfo aspectInfo) {
		this(self, scopeFrame, linkFrame, aspectFrame, aspectInfo, new HashMap<OPlace, Boolean>());
		NewAspectInstanceEvent event= new NewAspectInstanceEvent();
		sendEvent(event);
	}



	private   AspectInfo _aspectInfo;
	
	private  Map<OPlace, Boolean> _preValues;
	

	
	
	
	
	private static final Log __log = LogFactory.getLog(ASPECT.class);
	private static final ActivityTemplateFactory __activityTemplateFactory = new ActivityTemplateFactory();
	
	
//	public ASPECT(AspectInfo aspectInfo,ScopeFrame processFrame ) {
//		this(aspectInfo, processFrame, new HashMap<OPlace,Boolean>());
//	}
//    public ASPECT(AspectInfo aspectInfo, ScopeFrame processFrame, Map<OPlace, Boolean> preValues){
//    	_aspectInfo = aspectInfo;
//		_oaspect = _aspectInfo.getOaspect();
//		
//		_preValues = preValues;
//		_baseProcessFrame =processFrame;
//    }
//    
//	
	@Override
	public void run() {
			
	        instance(new ADVICE(_self));
	    	object(false, compose(new ReceiveProcess(){
			private static final long serialVersionUID = 1L;
			}.setChannel(_self.self).setReceiver(new Termination(){
	
				private static final long serialVersionUID = -2650456504199710266L;

				public void terminate(){
					releaseTokens();
				}
			})).or(new ReceiveProcess(){

				private static final long serialVersionUID = -3597591633714962681L;

				
				
			}.setChannel(_self.parent).setReceiver(new ParentScope() {
				
				private static final long serialVersionUID = 671144321403252770L;

				@Override
				public void failure(String reason, Element data) {
					releaseTokens();
					
				}
				
				@Override
				public void completed(FaultData faultData,
						Set<CompensationHandler> compensations) {
					releaseTokens();
					
				}
				
				@Override
				public void compensate(OScope scope, Synch ret) {
					releaseTokens();
					
				}
				
				@Override
				public void cancelled() {
					releaseTokens();
					
				}
			})));
			   
	}

	private OAdvice getOAdvice(){
		AspectStore aspectStore = AspectStoreImpl.getInstance();
		AspectConfImpl aspect =aspectStore.getAspectConfiguration(_aspectInfo.getOaspect().getQName());
		return aspect.getOaspect().getAdvice();
	}
	
	

//	private void createGlobals(OAdvice oadvice) {
//		   _globals = new InstanceGlobals();
//		 
//	        // For each variable, we create a lock.
//	        for (OBase child : oadvice.getChildren()) {
//	            if (child instanceof OScope.Variable) {
//	                OScope.Variable var = (Variable) child;
//	                __log.debug("var:" +var);
//                	ReadWriteLock vlock = newChannel(ReadWriteLock.class);
//                	instance(new READWRITELOCK(vlock));
//                	_globals._varLocks.put(var, vlock);
//	                
//	            }
//	        }
//		
//	}


	//private static final long serialVersionUID = 4173936819493621541L;

	private class ADVICE extends BpelJacobRunnable{
		
		

		/**
		 * 
		 */
		private static final long serialVersionUID = 6393113295806738491L;

        private  ActivityInfo _child;
        
       
        
		public ADVICE(ActivityInfo child) {
			_child = child;
		}

		
		@Override
		public void run() {
			__log.info("aspect is running!");
			__log.info("_child:"+ _child);
			
			
			_child.o = getOAdvice().procesScope;
			
			
			
		
		
			if(_preValues.values().contains(Boolean.FALSE)){
				// one precondition is marked as false (cannot satisfied until the end) in the process instance
				__log.info("aspect is skipped");
				releaseTokens();
			}
				
			//all preconditions status are collected
			if(_preValues.keySet().containsAll(_aspectInfo.preConditions.keySet())){
				__log.info("aspect is triggered");
				__log.info("_child:"+ _child.o);
				AspectTriggeredEvent event = new AspectTriggeredEvent();
				sendEvent(event);
				instance(createActivity(_child));
				
			}else{
				//don't know the all the precondition status, wait
				CompositeProcess mlset = compose(new ReceiveProcess() {
		            private static final long serialVersionUID = 5094153128476008961L;
		        }.setChannel(_child.self).setReceiver(new Termination() {
					
					
					private static final long serialVersionUID = -5446131273793481657L;

					public void terminate() {
		                // Complete immediately, without faulting or registering any comps.
		                _child.parent.completed(null, CompensationHandler.emptySet());
		                // advice are terminated relase all tokens need by the base process.
		                releaseTokens();
		            }
					
		        }));
				   
				 for(final OPlace oplace:_aspectInfo.preConditions.keySet()){
					mlset.or(new ReceiveProcess(){
		
						private static final long serialVersionUID = -5408047027722055895L;
						
					}.setChannel(_aspectInfo.resolvePre(oplace)).setReceiver(new AspectConditionStatus() {
						private static final long serialVersionUID = -6559678468467510951L;
		
						@Override
						public void conditionStatus(boolean value) {
							__log.debug("receive a value: {"+ oplace.toString() +"} -" + value);
							_preValues.put(oplace, value);
							instance(ADVICE.this);
							
						}
					}));
				 }
				 
				 object(false, mlset);
			}
			
		
			
		}
	}
	
	
	private ACTIVITY createActivity(ActivityInfo activity) {
		
	 	//ScopeFrame newFrame = new ScopeFrame(getOAdvice().procesScope, _scopeInstanceId, _scopeFrame, null);
	    
		return __activityTemplateFactory.createInstance(activity.o, activity, _scopeFrame, new LinkFrame(null), null);
	}

	private void releaseTokens() {
		
		for ( AspectConditionStatus acs: _aspectInfo.postConditions.values()){
			acs.conditionStatus(true); // signal waited activities in the base process
		}
		sendEvent(new AspectInstanceEndEvent());
	}
	
	
	
	
}
