/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import org.apache.ode.bpel.o.OBase;
import org.apache.ode.bpel.o.OScope;
import org.apache.ode.bpel.o.OScope.Variable;
import org.apache.ode.bpel.runtime.ActivityInfo;
import org.apache.ode.bpel.runtime.BpelJacobRunnable;
import org.apache.ode.bpel.runtime.BpelRuntimeContext;
import org.apache.ode.bpel.runtime.InstanceGlobals;
import org.apache.ode.bpel.runtime.READWRITELOCK;
import org.apache.ode.bpel.runtime.ScopeFrame;
import org.apache.ode.bpel.runtime.channels.ParentScope;
import org.apache.ode.bpel.runtime.channels.ReadWriteLock;
import org.apache.ode.bpel.runtime.channels.Termination;

import cn.edu.nju.cs.tcao4bpel.o.OAdvice;
import cn.edu.nju.cs.tcao4bpel.o.OAspect;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-2-3 2015
 * ASPECT.java
 */
public class ASPECT extends BpelJacobRunnable{
	private OAspect _oaspect;
	
	private OAdvice _oadvice;
	
	private ScopeFrame _baseProcessFrame;
	private InstanceGlobals _globals;
	
	private AspectInfo _aspectInfo;
	public ASPECT(AspectInfo aspectInfo,ScopeFrame processFrame ) {
		_aspectInfo = aspectInfo;
		_oaspect = _aspectInfo.oaspect;
		_oadvice = _oaspect.getAdvice();
		_baseProcessFrame =processFrame;
	}

	
	@Override
	public void run() {
		 	BpelRuntimeContext ntive = getBpelRuntimeContext();
	        Long scopeInstanceId = ntive.createScopeInstance(_baseProcessFrame.scopeInstanceId, _oadvice.procesScope);
	        createGlobals();
	        ActivityInfo child = new ActivityInfo(genMonotonic(),
	            _oadvice.procesScope,
	            newChannel(Termination.class), newChannel(ParentScope.class));
			ScopeFrame adviceFrame = new ScopeFrame(_oadvice.procesScope, scopeInstanceId, _baseProcessFrame, null,_globals);
	        instance(new ADVICE(child, _aspectInfo, adviceFrame));
	}


	/**
	 * 
	 */
	private void createGlobals() {
		   _globals = new InstanceGlobals();
	        
	        // For each variable, we create a lock.
	        for (OBase child : _oadvice.getChildren()) {
	            if (child instanceof OScope.Variable) {
	                OScope.Variable var = (Variable) child;
	                ReadWriteLock vlock = newChannel(ReadWriteLock.class);
	                instance(new READWRITELOCK(vlock));
	                _globals._varLocks.put(var, vlock);
	            }
	        }
		
	}


	private static final long serialVersionUID = 4173936819493621541L;

}
