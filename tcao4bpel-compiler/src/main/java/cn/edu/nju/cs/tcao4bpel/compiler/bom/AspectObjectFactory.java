package cn.edu.nju.cs.tcao4bpel.compiler.bom;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.bom.Bpel11QNames;
import org.apache.ode.bpel.compiler.bom.Bpel20QNames;
import org.apache.ode.bpel.compiler.bom.BpelObject;
import org.apache.ode.bpel.compiler.bom.BpelObjectFactory;
import org.apache.ode.bpel.compiler.bom.DOMBuilderContentHandler;
import org.apache.ode.bpel.compiler.bom.LocalEntityResolver;
import org.apache.ode.utils.DOMUtils;
import org.apache.ode.utils.XMLParserUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectObjectFactory.java
 */
public class AspectObjectFactory extends BpelObjectFactory{
	private static AspectObjectFactory _instance= new AspectObjectFactory();
	private static final Log _log=LogFactory.getLog(BpelObjectFactory.class);
	public AspectObjectFactory(){
		super();
		_mappings.put(CTAO4BPEL20QNames.ASPECT, Aspect.class);
		_mappings.put(CTAO4BPEL20QNames.ADVICE, Advice.class);
		_mappings.put(CTAO4BPEL20QNames.PLACE, Place.class);
		_mappings.put(CTAO4BPEL20QNames.POINTCUT,Pointcut.class);
		_mappings.put(CTAO4BPEL20QNames.POSTCONDITION,PostCondition.class);
		_mappings.put(CTAO4BPEL20QNames.PRECONDITION,PreCondition.class);
		_mappings.put(CTAO4BPEL20QNames.SKIP, Skip.class);
		
		
	}
	public static AspectObjectFactory getInstance(){
		return _instance;
	}
	
	public Aspect parseAspect(InputSource isrc, URI systemURI) throws IOException, SAXException {
		XMLReader _xr=XMLParserUtils.getXMLReader();
		LocalEntityResolver resolver = new LocalEntityResolver();
        resolver.register(Bpel11QNames.NS_BPEL4WS_2003_03, getClass().getResource("/bpel4ws_1_1-fivesight.xsd"));
        resolver.register(Bpel20QNames.NS_WSBPEL2_0, getClass().getResource("/wsbpel_main-draft-Apr-29-2006.xsd"));
        resolver.register(Bpel20QNames.NS_WSBPEL2_0_FINAL_ABSTRACT, getClass().getResource("/ws-bpel_abstract_common_base.xsd"));
        resolver.register(CTAO4BPEL20QNames.NS_CTAO4BPEL2_0_ASPECT, getClass().getResource("/ctao4bpel_aspect.xsd"));
        resolver.register(Bpel20QNames.NS_WSBPEL2_0_FINAL_PLINK, getClass().getResource("/ws-bpel_plnktype.xsd"));
        resolver.register(Bpel20QNames.NS_WSBPEL2_0_FINAL_SERVREF, getClass().getResource("/ws-bpel_serviceref.xsd"));
        resolver.register(Bpel20QNames.NS_WSBPEL2_0_FINAL_VARPROP, getClass().getResource("/ws-bpel_varprop.xsd"));
        resolver.register(XML, getClass().getResource("/xml.xsd"));
        resolver.register(WSDL,getClass().getResource("/wsdl.xsd"));
        resolver.register(Bpel20QNames.NS_WSBPEL_PARTNERLINK_2004_03, getClass().getResource("/wsbpel_plinkType-draft-Apr-29-2006.xsd"));
        _xr.setEntityResolver(resolver);
        Document doc=DOMUtils.newDocument();
        _xr.setContentHandler( new DOMBuilderContentHandler(doc));
        _xr.setFeature("http://xml.org/sax/features/namespaces",true);
        _xr.setFeature("http://xml.org/sax/features/namespace-prefixes", true);
        _xr.parse(isrc);
        BpelObject bpelObject = this.createBpelObject(doc.getDocumentElement(), systemURI);        
        return (Aspect) bpelObject;

	}
	@Override
	public BpelObject createBpelObject(Element el, URI uri) {
		_log.debug("AspectObjectFactory.createBpelObject: " + el);
		QName type = new QName(el.getNamespaceURI(), el.getLocalName());
		Class<? extends BpelObject> cls = _mappings.get(type);
		if(cls == null){
			_log.warn("Unrecognized element in Aspect dom: " + type);
			return new BpelObject(el);
		}
		try{
			Constructor<? extends BpelObject> ctor = cls.getConstructor(__CTOR);
			BpelObject bo = (BpelObject)ctor.newInstance(new Object[]{el});
			bo.setURI(uri);
			return bo;
		}catch(Exception ex){
			throw new RuntimeException("Internal compiler error", ex);
		}
	}
	

}
