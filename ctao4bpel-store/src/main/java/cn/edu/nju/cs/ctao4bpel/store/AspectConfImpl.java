package cn.edu.nju.cs.ctao4bpel.store;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.activityRecovery.FailureHandlingDocument.FailureHandling;
import org.apache.ode.bpel.dd.TInvoke;
import org.apache.ode.bpel.dd.TService;
import org.apache.ode.bpel.iapi.Endpoint;
import org.apache.ode.bpel.iapi.ProcessConf.PartnerRoleConfig;
import org.apache.ode.bpel.o.OFailureHandling;

import cn.edu.nju.cs.ctao4bpel.dd.TDeploymentAspect;
import cn.edu.nju.cs.ctao4bpel.o.OAspect;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectConfImpl.java
 */
public class AspectConfImpl {
	private static final Log log = LogFactory.getLog(AspectConfImpl.class);
	private final Date _deployDate; 
	private final HashMap<String, Endpoint> _partnerRoleInitialValues = new HashMap<String, Endpoint>();
	private final HashMap<String, PartnerRoleConfig> _partnerRoleConfig = new HashMap<String, PartnerRoleConfig>();
	private final TDeploymentAspect.Aspect _ainfo;
	private AspectDeploymentUnitDir _du;
	private QName _aid;
	private QName _name;
	private OAspect oaspect;
	public AspectConfImpl(QName aid, QName name, AspectDeploymentUnitDir du, 
			TDeploymentAspect.Aspect ainfo, Date deployDate, OAspect oaspect) {
		this._aid= aid;
		this._name = name;
		this._du = du;
		this._ainfo = ainfo;
		this._deployDate= deployDate;
		this.oaspect = oaspect;
		initPartnerLinks();
	}
	private void initPartnerLinks() {
		if(_ainfo.getInvokeList() != null){
			for(TInvoke invoke : _ainfo.getInvokeList()){
				String plinkName = invoke.getPartnerLink();
				TService service = invoke.getService();
				if(service == null)
					continue;
				log.debug("Processing <invoke> element for aspect " + _ainfo.getName() + ": partnerlink " + plinkName + " --> "
                        + service);
				_partnerRoleInitialValues.put(plinkName, new Endpoint(service.getName(), service.getPort()));
				OFailureHandling g =null;
				if(invoke.isSetFailureHandling()){
					FailureHandling f = invoke.getFailureHandling();
					g =new OFailureHandling();
					if(f.isSetFaultOnFailure()) g.faultOnFailure = f.getFaultOnFailure();
					if(f.isSetRetryDelay()) g.retryDelay = f.getRetryDelay();
					if(f.isSetRetryFor()) g.retryFor = f.getRetryFor();
					
				}
				PartnerRoleConfig c = new PartnerRoleConfig(g, invoke.getUsePeer2Peer());
				_partnerRoleConfig.put(plinkName, c);
			}
		}
	}
	
	public Map<String, Endpoint> getInvokeEndpoints() {
        return Collections.unmodifiableMap(_partnerRoleInitialValues);
    }
	public Date get_deployDate() {
		return _deployDate;
	}
	public AspectDeploymentUnitDir get_du() {
		return _du;
	}
	public QName get_aid() {
		return _aid;
	}
	public QName get_name() {
		return _name;
	}
	public OAspect getOaspect() {
		return oaspect;
	}
	public String toString() {
		return "AspectContImpl: " + oaspect.toString();
	}
	
	

}
