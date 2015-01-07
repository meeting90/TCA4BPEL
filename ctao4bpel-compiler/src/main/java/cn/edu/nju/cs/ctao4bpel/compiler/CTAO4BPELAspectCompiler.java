package cn.edu.nju.cs.ctao4bpel.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.compiler.BpelCompiler20;
import org.apache.ode.bpel.compiler.DefaultResourceFinder;
import org.apache.ode.bpel.compiler.ResourceFinder;
import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.compiler.api.CompilationMessage;
import org.apache.ode.bpel.compiler.bom.Bpel11QNames;
import org.apache.ode.bpel.compiler.bom.Bpel20QNames;
import org.apache.ode.bpel.compiler.bom.Import;
import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.bpel.compiler.bom.Property;
import org.apache.ode.bpel.compiler.bom.PropertyAlias;
import org.apache.ode.bpel.compiler.wsdl.Definition4BPEL;
import org.apache.ode.bpel.o.OConstantVarType;
import org.apache.ode.bpel.o.OExpressionLanguage;
import org.apache.ode.bpel.o.OProcess;
import org.apache.ode.bpel.o.OScope;
import org.apache.ode.bpel.o.OVarType;
import org.apache.ode.store.ProcessStoreImpl;
import org.apache.ode.utils.GUID;
import org.apache.ode.utils.StreamUtils;
import org.apache.ode.utils.xsl.XslTransformHandler;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cn.edu.nju.cs.ctao4bpel.compiler.bom.Advice;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Aspect;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.AspectObjectFactory;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Condition;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Place;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Pointcut;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.PostCondition;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.PreCondition;
import cn.edu.nju.cs.ctao4bpel.o.OAdvice;
import cn.edu.nju.cs.ctao4bpel.o.OAspect;
import cn.edu.nju.cs.ctao4bpel.o.OCondition;
import cn.edu.nju.cs.ctao4bpel.o.OPlace;
import cn.edu.nju.cs.ctao4bpel.o.OPointcut;
import cn.edu.nju.cs.ctao4bpel.o.OPostCondition;
import cn.edu.nju.cs.ctao4bpel.o.OPreCondition;

public class CTAO4BPELAspectCompiler extends BpelCompiler20{
	
	private ProcessStoreImpl processStore;

	public CTAO4BPELAspectCompiler(ProcessStoreImpl processStore) throws Exception {
		super();
		this.processStore = processStore;
	}
	
	public OAspect compileAspect(URL aspectURL, String scope) {
		File aspectFile = new File(aspectURL.getFile());
		return compileAspect(aspectFile, scope);
		
	}

	public OAspect compileAspect(File aspectFile, String scope) {
		Aspect aspect =null;
		try {
			@SuppressWarnings("deprecation")
			InputSource isrc= new InputSource(new ByteArrayInputStream(StreamUtils.read(aspectFile.toURL())));
			isrc.setSystemId(aspectFile.getAbsolutePath());
			aspect = AspectObjectFactory.getInstance().parseAspect(isrc, aspectFile.toURI());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
		assert aspect != null;
		assert aspect.getAdvice() != null;
		
		ResourceFinder rf;
		File suDir= aspectFile.getParentFile();
		rf = new DefaultResourceFinder(aspectFile.getAbsoluteFile().getParentFile(), suDir.getAbsoluteFile());
		this.setResourceFinder(rf);
		OAspect oaspect;
		
		oaspect = compile(aspect,rf,scope, aspectFile.getAbsoluteFile());
		
		return oaspect;
	}
	public OAspect compile(final Aspect aspect,ResourceFinder rf, String scope, File aspectFile){
		OAspect oaspect = new OAspect();
		oaspect.setAspectName(aspect.getName());
		if(aspect.getTargetNamespace() ==null){
			oaspect.setTargetNamespace("--UNSPECIFIED--");
		}else{
			oaspect.setTargetNamespace(aspect.getTargetNamespace());
		}
		oaspect.setSkip(aspect.isSkip());
		oaspect.setScope(scope);
		QName processId= this.getProcessId(aspect.getBpelUrl());
		assert processId != null;
		oaspect.setProcessId(processId);
		OPointcut pointcut = compile(aspect.getPointcut());
		oaspect.setPointcut(pointcut);
		Advice advice = aspect.getAdvice();
		OAdvice oadvice =  compile(advice, rf, getVersion(aspectFile.getParent()));
		oaspect.setAdvice(oadvice);
		return oaspect;
		
	}
	
	
    public OAdvice compile(final Advice advice, ResourceFinder rf, long version) throws CompilationException {
        if (advice == null)
            throw new NullPointerException("Null process parameter");

        setResourceFinder(rf);
        _processURI = advice.getURI();
        _processDef = advice;
        _generatedDate = new Date();
        _structureStack.clear();

        String bpelVersionUri = null;
        switch (advice.getBpelVersion()) {
        case BPEL11:
            bpelVersionUri = Bpel11QNames.NS_BPEL4WS_2003_03;
            break;
        case BPEL20_DRAFT:
            bpelVersionUri = Bpel20QNames.NS_WSBPEL2_0;
            break;
        case BPEL20:
            bpelVersionUri = Bpel20QNames.NS_WSBPEL2_0_FINAL_EXEC;
            break;
        default:
            throw new IllegalStateException("Bad bpel version: " + advice.getBpelVersion());
        }

        _oprocess = new OAdvice(bpelVersionUri);
        _oprocess.guid = null;
        _oprocess.constants = makeConstants();
        _oprocess.debugInfo = createDebugInfo(advice, "process");
        _oprocess.namespaceContext = advice.getNamespaceContext();
        
        if (advice.getTargetNamespace() == null) {
            _oprocess.targetNamespace = "--UNSPECIFIED--";
            recoveredFromError(advice, new CompilationException(__cmsgs.errProcessNamespaceNotSpecified()));
        } else {
            _oprocess.targetNamespace = _processDef.getTargetNamespace();
        }

        if (advice.getName() == null) {
            _oprocess.processName = "--UNSPECIFIED--";
            recoveredFromError(advice, new CompilationException(__cmsgs.errProcessNameNotSpecified()));
        } else {
            _oprocess.processName = _processDef.getName();
        }

        _oprocess.compileDate = _generatedDate;

        _konstExprLang = new OExpressionLanguage(_oprocess, null);
        _konstExprLang.debugInfo = createDebugInfo(_processDef, "Constant Value Expression Language");
        _konstExprLang.expressionLanguageUri = "uri:www.fivesight.com/konstExpression";
        _konstExprLang.properties.put("runtime-class",
                "org.apache.ode.bpel.runtime.explang.konst.KonstExpressionLanguageRuntimeImpl");
        _oprocess.expressionLanguages.add(_konstExprLang);

        // Process the imports. Note, we expect all processes (Event BPEL 1.1)
        // to have an import declaration. This should be automatically generated
        // by the 1.1 parser.
        for (Import imprt : _processDef.getImports()) {
            try {
                compile(_processURI, imprt);
            } catch (CompilationException bce) {
                // We try to recover from import problems by continuing
                recoveredFromError(imprt, bce);
            }
        }

        _expressionValidatorFactory.getValidator().bpelImportsLoaded(_processDef, this);

        switch (_processDef.getSuppressJoinFailure()) {
        case NO:
        case NOTSET:
            _supressJoinFailure = false;
            break;
        case YES:
            _supressJoinFailure = true;
            break;
        }
        // compile ALL wsdl properties; needed for property extraction
        Definition4BPEL[] defs = _wsdlRegistry.getDefinitions();
        for (Definition4BPEL def : defs) {
            for (Property property : def.getProperties()) {
                compile(property);
            }
        }
        // compile ALL wsdl property aliases
        for (Definition4BPEL def1 : defs) {
            for (PropertyAlias propertyAlias : def1.getPropertyAliases()) {
                compile(propertyAlias);
            }
        }

        OScope procesScope = new OScope(_oprocess, null);
        procesScope.name = "__PROCESS_SCOPE:" + advice.getName();
        procesScope.debugInfo = createDebugInfo(advice, null);
        _oprocess.procesScope = compileScope(procesScope, advice, new Runnable() {
            public void run() {
                if (advice.getRootActivity() == null) {
                    throw new CompilationException(__cmsgs.errNoRootActivity());
                }
                // Process custom properties are created as variables associated
                // with the top scope
                if (_customProcessProperties != null) {
                    for (Map.Entry<QName, Node> customVar : _customProcessProperties.entrySet()) {
                        final OScope oscope = _structureStack.topScope();
                        OVarType varType = new OConstantVarType(_oprocess, customVar.getValue());
                        OScope.Variable ovar = new OScope.Variable(_oprocess, varType);
                        ovar.name = customVar.getKey().getLocalPart();
                        ovar.declaringScope = oscope;
                        ovar.debugInfo = createDebugInfo(null, "Process custom property variable");
                        oscope.addLocalVariable(ovar);
                        if (__log.isDebugEnabled())
                            __log.debug("Compiled custom property variable " + ovar);
                    }
                }
                _structureStack.topScope().activity = compile(advice.getRootActivity());
            }
        });

        assert _structureStack.size() == 0;

        boolean hasErrors = false;
        StringBuffer sb = new StringBuffer();
        for (CompilationMessage msg : _errors) {
            if (msg.severity >= CompilationMessage.ERROR) {
                hasErrors = true;
                sb.append('\t');
                sb.append(msg.toErrorString());
                sb.append('\n');
            }
        }

        XslTransformHandler.getInstance().clearXSLSheets(_oprocess.getQName());

        _expressionValidatorFactory.getValidator().bpelCompilationCompleted(_processDef);

        if (hasErrors) {
            throw new CompilationException(__cmsgs.errCompilationErrors(_errors.size(), sb.toString()));
        }

        {
            String digest = "version:" + version + ";" + _oprocess.digest();
            _oprocess.guid = GUID.makeGUID(digest);
            if (__log.isDebugEnabled()) {
                __log.debug("Compiled process digest: " + digest + "\nguid: " + _oprocess.guid);
            }
        }
        return (OAdvice)_oprocess;
    }

	private QName getProcessId(String bpelUrl) {
		if(processStore ==null)
			return null;
		List<QName> processIds= processStore.getProcesses();
		for(QName processId: processIds){
			if(processId.getLocalPart().equals( bpelUrl))
				return processId;
		}
		return null;
	}
	private OPointcut compile(Pointcut pointcut) {
		OPointcut opointcut= new OPointcut();
		OPreCondition opre=compile(pointcut.getPreCondition());
		OPostCondition opost = compile(pointcut.getPostCondition());
		opointcut.setPreCondition(opre);
		opointcut.setPostCondition(opost);
		return opointcut;
	}
	private OPreCondition compile(PreCondition condition) {
		OPreCondition ocondition = new OPreCondition();
		for(Place place: condition.getPlaces()){
			OPlace oplace =compile(place,ocondition);
			ocondition.addPlace(oplace);
		}
		return ocondition;
	
	}
	private OPostCondition compile(PostCondition condition) {
		OPostCondition ocondition = new OPostCondition();
		for(Place place: condition.getPlaces()){
			OPlace oplace =compile(place,ocondition);
			ocondition.addPlace(oplace);
		}
		return ocondition;
	
	}
	private OPlace compile(Place place, OCondition con) {
		OPlace oplace = new OPlace(con);
		oplace.setState(place.getState());
		oplace.setExpression(place.getExpression());
		return oplace;
	}
}
	
