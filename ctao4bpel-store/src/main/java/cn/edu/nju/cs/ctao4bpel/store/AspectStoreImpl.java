package cn.edu.nju.cs.ctao4bpel.store;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.store.Messages;
import org.apache.ode.store.ProcessStoreImpl;
import org.apache.ode.utils.msg.MessageBundle;

import cn.edu.nju.cs.ctao4bpel.dd.DeployAspectDocument;
import cn.edu.nju.cs.ctao4bpel.dd.TDeploymentAspect;
import cn.edu.nju.cs.ctao4bpel.o.OAspect;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectStoreImpl.java
 */
public class AspectStoreImpl  implements AspectStore{
	
	private static final Log _log = LogFactory.getLog(AspectStoreImpl.class);
	private static final Messages _msgs= MessageBundle.getMessages(Messages.class);
	private Map<QName, AspectConfImpl> _aspects = new HashMap<QName, AspectConfImpl>();
	private Map<String,AspectDeploymentUnitDir> _deploymentUnits = new HashMap<String, AspectDeploymentUnitDir>();
	private final CopyOnWriteArrayList<AspectStoreListener> _lisListeners = new CopyOnWriteArrayList<AspectStoreListener>();
	public AspectStoreImpl(){
		
	}
	@Override
	public Collection<QName> deployAspect(File deploymentUnitdir, String scope,
			ProcessStoreImpl processStore) {
		_log.debug("Deploying Aspect package: " + deploymentUnitdir.getName());
		final Date deployDate = new Date();
		final AspectDeploymentUnitDir du = new AspectDeploymentUnitDir(deploymentUnitdir);
		try{
			_log.debug("Compiling deployment unit");
			du.compile(scope, processStore);
		}catch(CompilationException ce){
			String errmsg = _msgs.msgDeployFailCompileErrors(ce);
			_log.error(errmsg,ce);
			throw new ContextException(errmsg,ce);
			
		}
		_log.debug("Scanning for compiled aspects");
		du.scan();
		final DeployAspectDocument dd = du.getDeplymentDescriptor();
		final ArrayList<AspectConfImpl> aspects = new ArrayList<AspectConfImpl>();
		if(_deploymentUnits.containsKey(du.getName())){
			String errmsg = _msgs.msgDeployFailDuplicateDU(du.getName());
			_log.error(errmsg);
			throw new ContextException(errmsg);
		}
		_log.debug("deploying aspects defined in DD: " + dd.getDeployAspect().getAspectList() );
		for(TDeploymentAspect.Aspect aspectDD: dd.getDeployAspect().getAspectList()){
			QName aid = aspectDD.getName();
			if(_aspects.containsKey(aid)){
				String errmsg= _msgs.msgDeployFailDuplicatePID(aid, du.getName());
				_log.error(errmsg);
				throw new ContextException(errmsg);
			}
			CBAInfo cbaInfo = du.getCBAInfo(aid);
			if(cbaInfo == null){
				String errmsg = _msgs.msgDeployFailedProcessNotFound(aspectDD.getName(), du.getName());
				_log.error(errmsg);
				throw new ContextException(errmsg);
			}
			OAspect oaspect = du.getAspect(aid);
			AspectConfImpl aconf= new AspectConfImpl(aid, aspectDD.getName(), du, aspectDD, deployDate, oaspect);
			aspects.add(aconf);
		}
		_deploymentUnits.put(du.getName(), du);
		for(AspectConfImpl aspect: aspects){
			_log.info("Aspect deployed succcessfully : " + du.getDeployDir() + ","  + aspect.get_aid());
			_aspects.put(aspect.get_aid(), aspect);
		}
		return _aspects.keySet();
	}
	@Override
	public Collection<QName> undeployAspect(File file) {
		_log.debug("AspectStore before undeployment : " + _aspects);
		String duName = file.getName();
		Collection<QName> undeployed = Collections.emptyList();
		AspectDeploymentUnitDir du;
		du = _deploymentUnits.remove(duName);
		if(du != null){
			undeployed = du.getAspectNames();
		}
		for(QName aid: undeployed){
			fireEvent(new AspectStoreEvent(AspectStoreEvent.Type.UNDEPLOYED, aid, du.getName()));
			_log.info("Aspect " + aid.toString() + "has been undeployed!");
		}
		_log.debug("Undeployed: " + undeployed);
		_aspects.keySet().removeAll(undeployed);
		_log.debug("AspectStore after undeployment: " + _aspects);
		
		return undeployed;
	}

	@Override
	public Collection<String> getAspectPackages() {
		return new ArrayList<String>(_deploymentUnits.keySet());
	}
	@Override
	public List<QName> listAspects(String packageName) {
		AspectDeploymentUnitDir du = _deploymentUnits.get(packageName);
		if(du == null)
			return null;
		return du.getAspectNames();
	}
	@Override
	public List<QName> getAspectList() {
		return new ArrayList<QName>(_aspects.keySet());
	}
	@Override
	public long getCurrentVersion() {
		return 0;
	}
	@Override
	public Collection<AspectConfImpl> getAspects() {
		return _aspects.values();
	}
	@Override
	public AspectConfImpl getAspectConfiguration(QName aspectId) {
		return _aspects.get(aspectId);
	}
	@Override
	public void registerListener(AspectStoreListener asl) {
		_lisListeners.add(asl);
		
	}
	@Override
	public void unregisterListner(AspectStoreListener asl) {
		_lisListeners.remove(asl);
	}
	private void fireEvent(AspectStoreEvent ase) {
		_log.debug("firing event : " + ase);
		for(AspectStoreListener asl: _lisListeners){
			asl.onApsectStoreEvent(ase);
		}
	}



}
