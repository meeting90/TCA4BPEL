///**
// * 
// */
//package cn.edu.nju.cs.tcao4bpel.runtime;
//
//import static org.apache.ode.jacob.ProcessUtil.compose;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.apache.ode.bpel.o.OScope;
//import org.apache.ode.bpel.runtime.ACTIVITY;
//import org.apache.ode.bpel.runtime.ActivityInfo;
//import org.apache.ode.bpel.runtime.ActivityTemplateFactory;
//import org.apache.ode.bpel.runtime.BpelJacobRunnable;
//import org.apache.ode.bpel.runtime.CompensationHandler;
//import org.apache.ode.bpel.runtime.LinkFrame;
//import org.apache.ode.bpel.runtime.ScopeFrame;
//import org.apache.ode.bpel.runtime.channels.FaultData;
//import org.apache.ode.bpel.runtime.channels.ParentScope;
//import org.apache.ode.bpel.runtime.channels.Termination;
//import org.apache.ode.jacob.CompositeProcess;
//import org.apache.ode.jacob.ReceiveProcess;
//import org.apache.ode.jacob.Synch;
//import org.w3c.dom.Element;
//
//import cn.edu.nju.cs.tcao4bpel.o.OAdvice;
//import cn.edu.nju.cs.tcao4bpel.o.OPlace;
//
///**
// * @author Mingzhu Yuan @ cs.nju.edu.cn
// * 2015-1-28 2015
// * ASPECT.java
// */
//public class ADVICE extends BpelJacobRunnable{
//	private static final ActivityTemplateFactory __activityTemplateFactory = new ActivityTemplateFactory();
//	private static final Log __log = LogFactory.getLog(ADVICE.class);
//	
//	private Map<OPlace, Boolean> _preValues = new HashMap<OPlace, Boolean>();
//	
//	private final AspectInfo _aspectInfo;
//
//	
//	private final OAdvice _oadvice;
//	
//	private ScopeFrame _scopeFrame;
//	
//	
//
//	
//
//
//	public ADVICE(ActivityInfo activity, AspectInfo aspectInfo,ScopeFrame scopeFrame, OAdvice oadvice) {
//		_scopeFrame = scopeFrame;
//		_aspectInfo = aspectInfo;
//		_oadvice  = oadvice;
//		
//	}
//	
//
//	
//
//	
//	private static final long serialVersionUID = 4615544770314527519L;
//	@Override
//	public void run() {
//		__log.info("aspect is running!");
//		
//		__log.debug("scope initial:"+ _oadvice.procesScope);
//		if(_preValues.values().contains(false)){
//			// one precondition is marked as false (cannot satisfied until the end) in the process instance
//			__log.info("aspect is skipped");
//			releaseTokens();
//		}
//			
//		//all preconditions status are collected
//		if(_preValues.keySet().containsAll(_aspectInfo.preConditions.keySet())){
//			__log.info("aspect is triggered");
//			
//			ActivityInfo activity = new ActivityInfo(genMonotonic(), _oadvice.procesScope, _self.self, newChannel(ParentScope.class));
//			instance(createActivity(activity));
//			instance(new RELEASETOKENS(activity.parent));
//		}else{
//			//don't know the all the precondition status, wait
//			CompositeProcess mlset = compose(new ReceiveProcess() {
//	            private static final long serialVersionUID = 5094153128476008961L;
//	        }.setChannel(_self.self).setReceiver(new Termination() {
//				private static final long serialVersionUID = 1L;
//				public void terminate() {
//	                // Complete immediately, without faulting or registering any comps.
//	                _self.parent.completed(null, CompensationHandler.emptySet());
//	                // advice are terminated relase all tokens need by the base process.
//	                releaseTokens();
//	            }
//				
//	        }));
//			   
//			 for(final OPlace oplace:_aspectInfo.preConditions.keySet()){
//				mlset.or(new ReceiveProcess(){
//	
//					private static final long serialVersionUID = -5408047027722055895L;
//					
//				}.setChannel(_aspectInfo.resolvePre(oplace)).setReceiver(new AspectConditionStatus() {
//					private static final long serialVersionUID = -6559678468467510951L;
//	
//					@Override
//					public void conditionStatus(boolean value) {
//						__log.debug("receive a value: {"+ oplace.toString() +"} -" + value);
//						_preValues.put(oplace, value);
//						OAdvice oadvice= _oadvice;
//						instance(ADVICE.this);
//						
//						//ActivityInfo child = new ActivityInfo(genMonotonic(),
//						//		_oscope,
//					    //       newChannel(Termination.class), newChannel(ParentScope.class));
//					    //instance(new ADVICE(child, _aspectInfo, _scopeFrame, _preValues));
//						
//						//instance(new ADVICE(_root, _aspectInfo, _scopeFrame, _preValues));
//						
//					}
//				}));
//			 }
//			 
//			 object(false, mlset);
//		}
//		   
//		
//		
//	}
//	
//
//	private ACTIVITY createActivity(ActivityInfo activity) {
//		return __activityTemplateFactory.createInstance(activity.o, activity, _scopeFrame, new LinkFrame(null), null);
//	}
//
//	private void releaseTokens() {
//		for ( AspectConditionStatus acs: _aspectInfo.postConditions.values()){
//			acs.conditionStatus(true); // signal waited activities in the base process
//		}
//	}
//	
//	 private class RELEASETOKENS extends BpelJacobRunnable{
//
//		
//		private static final long serialVersionUID = 3257900258770292110L;
//		private ParentScope _in;
//		public RELEASETOKENS(ParentScope parent) {
//			_in = parent;
//		}
//
//		
//		@Override
//		public void run() {
//			 object(new ReceiveProcess() {
//	                private static final long serialVersionUID = 2667359535900385952L;
//	            }.setChannel(_in).setReceiver(new ParentScope() {
//
//					private static final long serialVersionUID = 1036330154863567438L;
//
//					@Override
//					public void compensate(OScope scope, Synch ret) {
//						releaseTokens();
//						
//					}
//
//					@Override
//					public void completed(FaultData faultData,
//							Set<CompensationHandler> compensations) {
//						releaseTokens();
//						
//					}
//
//					@Override
//					public void cancelled() {
//						releaseTokens();
//						
//					}
//
//					@Override
//					public void failure(String reason, Element data) {
//						releaseTokens();
//						
//					}}));
//			
//		} 
//	 }
//
//
//}
