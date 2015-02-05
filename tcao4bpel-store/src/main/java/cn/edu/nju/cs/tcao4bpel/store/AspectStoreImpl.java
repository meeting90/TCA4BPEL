package cn.edu.nju.cs.tcao4bpel.store;

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
import org.apache.ode.bpel.iapi.ProcessStore;
import org.apache.ode.store.Messages;
import org.apache.ode.utils.msg.MessageBundle;

import cn.edu.nju.cs.tcao4bpel.dd.DeployAspectDocument;
import cn.edu.nju.cs.tcao4bpel.dd.TDeploymentAspect;
import cn.edu.nju.cs.tcao4bpel.o.OAspect;
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
	/** thread safe **/
	private static final AspectStore INSTANCE= new AspectStoreImpl();
	
	private File _deployDir;
	
	public AspectStoreImpl(){
		
	}
	
	public static AspectStore getInstance(){
		return INSTANCE;
	}
	@Override
	public Collection<QName> deployAspect(File deploymentUnitdir, String scope,
			ProcessStore processStore) {
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
		_log.debug("deploying aspects defined in DD: " + dd.getDeployAspect().getAspectArray() );
		for(TDeploymentAspect.Aspect aspectDD: dd.getDeployAspect().getAspectArray()){
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

	@Override
	public boolean hasAspect(QName processId) {
		for(AspectConfImpl aspect: _aspects.values()){
			if(aspect.getOaspect().getProcessId().equals(processId))
				return true;
		}
		return false;
	}
	
	private void fireEvent(AspectStoreEvent ase) {
		_log.debug("firing event : " + ase);
		for(AspectStoreListener asl: _lisListeners){
			asl.onApsectStoreEvent(ase);
		}
	}


	@Override
	public Collection<AspectConfImpl> getAspects(QName processId) {
		Collection<AspectConfImpl> aspects= new ArrayList<AspectConfImpl>();
		for(AspectConfImpl aspect: _aspects.values()){
			if(aspect.getOaspect().getProcessId().equals(processId))
				aspects.add(aspect);
		}
		return aspects;
	}

	
	@Override
	public File getDeployDir() {
		return _deployDir;
	}

	
	@Override
	public void setDeployDir(File deployDir) {
		
		if (deployDir != null) {
            if( !deployDir.exists() ) {
            	deployDir.mkdirs();
                _log.warn("Deploy directory: " + deployDir.getAbsolutePath() + " does not exist; created it.");
            } else if(!deployDir.isDirectory()) {
                throw new IllegalArgumentException("Deploy directory is not a directory:  " + deployDir);
            }
        }

        _deployDir = deployDir;
		
		
	}



}
