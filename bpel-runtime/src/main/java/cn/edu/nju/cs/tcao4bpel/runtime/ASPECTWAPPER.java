/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import static org.apache.ode.jacob.ProcessUtil.compose;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.ode.bpel.o.OActivity;
import org.apache.ode.bpel.runtime.ACTIVITY;
import org.apache.ode.bpel.runtime.ActivityInfo;
import org.apache.ode.bpel.runtime.ActivityTemplateFactory;
import org.apache.ode.bpel.runtime.CompensationHandler;
import org.apache.ode.bpel.runtime.LinkFrame;
import org.apache.ode.bpel.runtime.ScopeFrame;
import org.apache.ode.bpel.runtime.channels.Termination;
import org.apache.ode.jacob.CompositeProcess;
import org.apache.ode.jacob.JacobRunnable;
import org.apache.ode.jacob.ReceiveProcess;

import cn.edu.nju.cs.tcao4bpel.o.OAspect;
import cn.edu.nju.cs.tcao4bpel.o.OPlace;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-28 2015
 * ACTIVITGROUPwithAspect.java
 */
public class ASPECTWAPPER extends ACTIVITY{
	
	
	private static final ActivityTemplateFactory __activityTemplateFactory = new ActivityTemplateFactory();

	AspectFrame _aspectFrame;
	OActivity _oactivity;
	
	
	
	
	Map<OPlace, Boolean> _postValues = new HashMap<OPlace, Boolean>();
	

	
	public ASPECTWAPPER(ActivityInfo self, ScopeFrame scopeFrame,
			LinkFrame linkFrame, AspectFrame aspectFrame) {
		super(self, scopeFrame, linkFrame, aspectFrame);
		_aspectFrame = aspectFrame;
		_oactivity = self.o;
	}
	
	

	/**
	 * 
	 */
	private static final long serialVersionUID = -1815681035539916941L;



	
	@Override
	public void run() {
		if(_aspectFrame == null){ // aspect itself
			
			instance(createActivity(_self));
			return;
		}
		Map<AspectInfo,OPlace> posts = new HashMap<AspectInfo,OPlace>();
		for(AspectInfo aspectInfo: _aspectFrame.getAspectInfos()){
			OPlace post= getRelatedPostContion(aspectInfo.oaspect);
			if(post != null && post.getState() == OPlace.State.READY) 
				posts.put(aspectInfo, post);
			
		}
		if(posts.isEmpty()){//ready state of this activity is not related to the aspect; no need to wait 
			 instance(createActivity(_self));
		}
		else{ 
			if(_postValues.keySet().containsAll(posts.values())){  
				//check for all aspect if postcondition satisfied, 
				//if yes continue running the activity ,else block until all postcondition satisfied
				instance(createActivity(_self));
			}else{  
				// continue wait  
				waitForAllPostCondition(posts);
			}
		}

	}
	
	

	private ACTIVITY createActivity(ActivityInfo activity) {
		
		return __activityTemplateFactory.createInstance(activity.o, activity, _scopeFrame, _linkFrame, _aspectFrame);
	}



	
	


	private void waitForAllPostCondition(Map<AspectInfo,OPlace> posts){
		CompositeProcess mlset = compose(new ReceiveProcess() {
            private static final long serialVersionUID = 5094153128476008961L;
        }.setChannel(_self.self).setReceiver(new Termination() {
			private static final long serialVersionUID = 1L;
			public void terminate() {
                // Complete immediately, without faulting or registering any comps.
                _self.parent.completed(null, CompensationHandler.emptySet());
                // Dead-path activity
                dpe(_oactivity);
            }
        }));
		
		   for (final AspectInfo aspects : posts.keySet()) {
			   final OPlace places = posts.get(aspects);
                mlset.or(new ReceiveProcess() {
                    private static final long serialVersionUID = 1024137371118887935L;
                }.setChannel(aspects.resolvePost(places)).setReceiver(new AspectConditionStatus() {
					private static final long serialVersionUID = 4584392798481957852L;

					@Override
					public void conditionStatus(boolean value) {
						_postValues.put(places, value);
						instance(ASPECTWAPPER.this);
					}
					
			
                }));
            }

            object(false, mlset);
	
	}
	

	private OPlace getRelatedPostContion(OAspect oaspect){
		Set<OPlace> postConditions = _aspectFrame.getPostConditions(oaspect);
		for(OPlace con: postConditions){
			if(con.getXpaths().get(0).equals(_oactivity.getXpath()))// activity need to block for after aspect ended
				 return con;
		}
		return null;
	}
	

	
	

}
