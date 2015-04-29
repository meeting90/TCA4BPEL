/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ode.bpel.runtime;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.evt.ProcessInstanceStartedEvent;
import org.apache.ode.bpel.o.OBase;
import org.apache.ode.bpel.o.OFailureHandling;
import org.apache.ode.bpel.o.OProcess;
import org.apache.ode.bpel.o.OScope;
import org.apache.ode.bpel.o.OScope.Variable;
import org.apache.ode.bpel.runtime.channels.FaultData;
import org.apache.ode.bpel.runtime.channels.ParentScope;
import org.apache.ode.bpel.runtime.channels.ReadWriteLock;
import org.apache.ode.bpel.runtime.channels.Termination;
import org.apache.ode.jacob.ReceiveProcess;
import org.apache.ode.jacob.Synch;
import org.w3c.dom.Element;

import cn.edu.nju.cs.tcao4bpel.o.OAdvice;
import cn.edu.nju.cs.tcao4bpel.o.OPlace;
import cn.edu.nju.cs.tcao4bpel.runtime.ASPECT;
import cn.edu.nju.cs.tcao4bpel.runtime.AspectConditionStatus;
import cn.edu.nju.cs.tcao4bpel.runtime.AspectFrame;
import cn.edu.nju.cs.tcao4bpel.runtime.AspectInfo;
import cn.edu.nju.cs.tcao4bpel.store.AspectConfImpl;
import cn.edu.nju.cs.tcao4bpel.store.AspectStore;
import cn.edu.nju.cs.tcao4bpel.store.AspectStoreImpl;

public class PROCESS extends BpelJacobRunnable {
    private static final long serialVersionUID = 1L;
    private OProcess _oprocess;
    private InstanceGlobals _globals;
    
    private List<AspectInfo> _aspectInfos;
    
    

    public PROCESS(OProcess process) {
        _oprocess = process;
    }

    public void run() {
        BpelRuntimeContext ntive = getBpelRuntimeContext();
        
        
        Long scopeInstanceId = ntive.createScopeInstance(null, _oprocess.procesScope);

        createGlobals();
        ProcessInstanceStartedEvent evt = new ProcessInstanceStartedEvent();
        evt.setRootScopeId(scopeInstanceId);
        evt.setScopeDeclarationId(_oprocess.procesScope.getId());
        ntive.sendEvent(evt);

        ActivityInfo child = new ActivityInfo(genMonotonic(),
            _oprocess.procesScope,
            newChannel(Termination.class), newChannel(ParentScope.class));
        ScopeFrame processFrame = new ScopeFrame(_oprocess.procesScope, scopeInstanceId, null, null,_globals);
        // create aspect
        AspectFrame aspectFrame= createAspects(processFrame);
        instance(new SCOPE(child, processFrame, new LinkFrame(null), aspectFrame));
        

        object(new ReceiveProcess() {
            private static final long serialVersionUID = -8564969578471906493L;
        }.setChannel(child.parent).setReceiver(new ParentScope() {
            public void compensate(OScope scope, Synch ret) {
                assert false;
            }

            public void completed(FaultData fault, Set<CompensationHandler> compensations) {
                BpelRuntimeContext nativeAPI = (BpelRuntimeContext)getExtension(BpelRuntimeContext.class);
                if (fault == null) {
                    nativeAPI.completedOk();
                } else {
                    nativeAPI.completedFault(fault);
                }
            }

            public void cancelled() {
                this.completed(null, CompensationHandler.emptySet());
            }

            public void failure(String reason, Element data) {
                FaultData faultData = createFault(OFailureHandling.FAILURE_FAULT_NAME, _oprocess, reason);
                this.completed(faultData, CompensationHandler.emptySet());
            }
        }));
    }

    private void createGlobals() {
        _globals = new InstanceGlobals();
        
        // For each variable, we create a lock.
        for (OBase child : _oprocess.getChildren()) {
            if (child instanceof OScope.Variable) {
                OScope.Variable var = (Variable) child;
                ReadWriteLock vlock = newChannel(ReadWriteLock.class);
                instance(new READWRITELOCK(vlock));
                _globals._varLocks.put(var, vlock);
            }
        }
    }


    //for create aspect instance of _oprocess
    private AspectFrame createAspects(ScopeFrame scopeFrame){
    	AspectStore aspectStore = AspectStoreImpl.getInstance();
    	
    	Collection<AspectConfImpl> aspects= aspectStore.getAspects(_oprocess.getQName());
    	_aspectInfos =new ArrayList<AspectInfo>(); 
    	for(AspectConfImpl aspect: aspects){
    		
    		AspectInfo aspectInfo = new AspectInfo(aspect.getOaspect());
    		Map<OPlace, AspectConditionStatus> pres= new HashMap<OPlace, AspectConditionStatus>();
    		Map<OPlace, AspectConditionStatus> posts= new HashMap<OPlace, AspectConditionStatus>();
    		for(OPlace pre :aspect.getOaspect().getPointcut().getPreCondition().getPlaces()){
    			AspectConditionStatus acs =newChannel(AspectConditionStatus.class);
    			pres.put(pre, acs);
    		}
    		
    		for(OPlace post :aspect.getOaspect().getPointcut().getPostCondition().getPlaces()){
    			AspectConditionStatus acs=newChannel(AspectConditionStatus.class);
    			posts.put(post, acs);
    		}
    		aspectInfo.setPreConditions(pres);
    		aspectInfo.setPostConditions(posts);
    		
    		
    		
    		_aspectInfos.add(aspectInfo);
    		
    	 
	      
	        ActivityInfo child = new ActivityInfo(genMonotonic(),
	        		aspect.getOaspect().getAdvice().procesScope,
	            newChannel(Termination.class), newChannel(ParentScope.class));
	      
	    
			
    		instance(new ASPECT(child, scopeFrame, new LinkFrame(null), null, aspectInfo));
    	}
    	
    	AspectFrame aspectFrame =new AspectFrame(_aspectInfos);
    	return aspectFrame;
    }
}
