package cn.edu.nju.cs.ctao4bpel.store;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.wsdl.Definition;
import javax.wsdl.WSDLException;
import javax.wsdl.xml.WSDLReader;
import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.bpel.compiler.DefaultResourceFinder;
import org.apache.ode.bpel.compiler.WSDLLocatorImpl;
import org.apache.ode.bpel.compiler.wsdl.Definition4BPEL;
import org.apache.ode.bpel.compiler.wsdl.WSDLFactory4BPEL;
import org.apache.ode.bpel.compiler.wsdl.WSDLFactoryBPEL20;
import org.apache.ode.bpel.iapi.ContextException;
import org.apache.ode.store.DocumentRegistry;
import org.apache.ode.store.ProcessStoreImpl;
import org.apache.ode.utils.InternPool;
import org.apache.ode.utils.InternPool.InternableBlock;
import org.apache.ode.utils.fs.FileUtils;
import org.apache.xmlbeans.XmlOptions;

import cn.edu.nju.cs.ctao4bpel.compiler.CTAO4BPELAspectCompiler;
import cn.edu.nju.cs.ctao4bpel.dd.DeployAspectDocument;
import cn.edu.nju.cs.ctao4bpel.o.AspectSerializer;
import cn.edu.nju.cs.ctao4bpel.o.OAspect;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectDeploymentUnitDir.java
 */
public class AspectDeploymentUnitDir {
	private static Log log = LogFactory.getLog(AspectDeploymentUnitDir.class);
	private File _duDir;
	private volatile DeployAspectDocument _dd;
	private volatile DocumentRegistry _docRegistry;
	public static final FileFilter _aspectFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".aspect") && pathname.isFile();
		}
	};
	public static final FileFilter _cbaFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".cba") && pathname.isFile();
		};
	};
	public static final FileFilter _wsdlFilter = new FileFilter(){
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".wsdl") && pathname.isFile();
		};
	};
	private HashMap<QName, CBAInfo> _aspects = new HashMap<QName, CBAInfo>();

	public AspectDeploymentUnitDir(File dir) {
		_duDir = dir;
		scan();
	}

	public void compile(String scope, ProcessStoreImpl processStore) {
		List<File> aspects = FileUtils.directoryEntriesInPath(_duDir,
				_aspectFilter);
		if (aspects.size() == 0)
			throw new IllegalArgumentException("Directory " + _duDir.getName()
					+ "does not contain any aspects!");
		for (File aspect : aspects) {
			String path = aspect.getAbsolutePath();
			File cba = new File(path.substring(0, path.lastIndexOf(".aspect"))
					+ ".cba");
			if (!cba.exists() || cba.lastModified() < aspect.lastModified()) {
				log.debug("compiling " + aspect);
				compile(aspect, scope, processStore);
			} else {
				log.debug("skipping comiplation of " + aspect + "cba found: "
						+ cba);
			}
		}
	}

	private void compile(final File aspectFile, final String scope,
			final ProcessStoreImpl aspectStore) {
		InternPool.runBlock(new InternableBlock() {

			@Override
			public void run() {
				try {
					CTAO4BPELAspectCompiler compiler = new CTAO4BPELAspectCompiler(
							aspectStore);
					OAspect oaspect = compiler.compileAspect(aspectFile, scope);

					AspectSerializer serializer = new AspectSerializer();

					String cbaPath = aspectFile.getPath().substring(0,
							aspectFile.getPath().lastIndexOf(".aspect"))
							+ ".cba";
					OutputStream cbaOut = new BufferedOutputStream(
							new FileOutputStream(cbaPath));

					serializer.writeOApsect(oaspect, cbaOut);
					
				} catch (IOException e) {
					log.error("Compile error in " + aspectFile, e);
					e.printStackTrace();
				} catch (Exception e) {
					log.error("Compile error in " + aspectFile, e);
					e.printStackTrace();
				}

			}
		});

	};

	protected void scan() {
		HashMap<QName, CBAInfo> aspects = new HashMap<QName, CBAInfo>();
		List<File> cbas = FileUtils.directoryEntriesInPath(_duDir, _cbaFilter);
		for (File file : cbas) {
			CBAInfo cbaInfo = loadCBAInfo(file);
			aspects.put(cbaInfo.aspectName, cbaInfo);
		}
		_aspects = aspects;
	}

	public CBAInfo getCBAInfo(QName aspectName) {
        return _aspects.get(aspectName);
    }
	private CBAInfo loadCBAInfo(File file) {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			AspectSerializer serializer = new AspectSerializer();
			OAspect oaspect = serializer.readOAspect(is);
			QName aspectName = new QName(oaspect.getTargetNamespace(),
					oaspect.getAspectName());
			CBAInfo info = new CBAInfo(aspectName, file);
			return info;
		} catch (Exception e) {
			throw new ContextException("Couldn't read compiled Aspect "
					+ file.getAbsolutePath(), e);
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public OAspect getAspect(QName aspectName) {
		CBAInfo cbainfo = _aspects.get(aspectName);
		try {
			InputStream is = new FileInputStream(cbainfo.cba);
			AspectSerializer serializer = new AspectSerializer();
			OAspect oaspect = serializer.readOAspect(is);
			return oaspect;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public List<QName> getAspectNames() {
		
		return new ArrayList<QName>(_aspects.keySet());
	}
	
	public String getName() {
        return _duDir.getName();
    }
	
	public long getVersion(){
		return 1L;
	}
	public File getDeployDir() {
		return _duDir;
	}

	public DeployAspectDocument getDeplymentDescriptor(){
		if(_dd == null){
			File ddLocation = new File(_duDir, "deploy.xml");
			try{
				XmlOptions options = new XmlOptions();
				HashMap<String, String> otherNs = new HashMap<String,String>();
				 otherNs.put("http://ode.fivesight.com/schemas/2006/06/27/dd",
	                        "http://cn.nju.edu.cs/ctao4bpel/schemas/dd/2015/01");
				 options.setLoadSubstituteNamespaces(otherNs);
				 _dd = DeployAspectDocument.Factory.parse(ddLocation, options);
				
			}catch(Exception e){
				throw new ContextException("Couldn't read deployment descriptor at location "
                        + ddLocation.getAbsolutePath(), e);
			}
		}
		return _dd;
	}
	
	public DocumentRegistry getDocRegistry(){
		if(_docRegistry == null) {
			_docRegistry = new DocumentRegistry();
			WSDLFactory4BPEL wsdlFactory = (WSDLFactory4BPEL) WSDLFactoryBPEL20.newInstance();
			WSDLReader r = wsdlFactory.newWSDLReader();
			DefaultResourceFinder rf = new DefaultResourceFinder(_duDir, _duDir);
			URI basedir = _duDir.toURI();
			List<File> wsdls = FileUtils.directoryEntriesInPath(_duDir, _wsdlFilter);
			for (File file: wsdls){
				URI uri = basedir.relativize(file.toURI());
				try{
					_docRegistry.addDefinition( (Definition4BPEL) r.readWSDL(new WSDLLocatorImpl(rf, uri)));
				}catch(WSDLException e){
					throw new ContextException("Couldn't read WSDL document at " + uri, e);
				}
			}
		}
		return _docRegistry;
	}
	public Definition getDefinitionForService(QName name){
		return getDocRegistry().getDefinition(name);

	}
	public Definition getDefinitionForPortType(QName name){
		return getDocRegistry().getDefinitionForPortType(name);
	}
	public Collection<Definition> getDefinitions(){
		 Definition4BPEL defs[] = getDocRegistry().getDefinitions();
	        ArrayList<Definition> ret = new ArrayList<Definition>(defs.length);
	        for (Definition4BPEL def : defs)
	            ret.add(def);
	        return ret;
	}
}
