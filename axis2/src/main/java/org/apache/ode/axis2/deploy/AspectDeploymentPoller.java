/**
 * 
 */
package org.apache.ode.axis2.deploy;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ode.axis2.ODEServer;
import org.apache.ode.utils.WatchDog;

import cn.edu.nju.cs.tcao4bpel.store.AspectStore;
import cn.edu.nju.cs.tcao4bpel.store.AspectStoreImpl;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-2-4 2015
 * AspectDeploymentPoller.java
 */
public class AspectDeploymentPoller extends DeploymentPoller{

	
	private static Log __log = LogFactory.getLog(AspectDeploymentPoller.class);
	  /** Filter accepting directories containing a aspect dd file. */
    private static final FileFilter _fileFilter = new FileFilter() {
        public boolean accept(File path) {
            return new File(path, "deploy.xml").exists();
        }
    };
    
    /** Filter accepting *.deployed files. */
    private static final FileFilter _deployedFilter = new FileFilter() {
        public boolean accept(File path) {
            return path.isFile() && path.getName().endsWith(".deployed");
        }
    };
	File _aspectDeployDir;
	ODEServer _odeServer;
	
	AspectStore _aspectStore = AspectStoreImpl.getInstance();
	
	

	public AspectDeploymentPoller(File deployDir, ODEServer odeServer) {
		super(deployDir, odeServer);
		_aspectDeployDir =deployDir;
		_odeServer = odeServer;
		
		
	}
	
	
	@Override
	protected void check() {
		File[] files = _aspectDeployDir.listFiles(_fileFilter);

        // Checking for new deployment directories
        if (isDeploymentFromODEFileSystemAllowed() && files != null) {
            for (File file : files) {
                File deployXml = new File(file, "deploy.xml");
                File deployedMarker = new File(_aspectDeployDir, file.getName() + ".deployed");

                if (!deployXml.exists()) {
                    // Skip if deploy.xml is abset
                    if (__log.isDebugEnabled()) {
                        __log.debug("Not deploying " + file + " (missing deploy.xml)");
                    }
                }

                @SuppressWarnings("rawtypes")
				WatchDog ddWatchDog = ensureDeployXmlWatchDog(file, deployXml);

                if (deployedMarker.exists()) {
                    checkDeployXmlWatchDog(ddWatchDog);
                    continue;
                }

                try {
                    boolean isCreated = deployedMarker.createNewFile();
                    if (!isCreated) {
                        __log.error("Error while creating  file "
                                        + file.getName()
                                        + ".deployed ,deployment could be inconsistent");
                    }
                } catch (IOException e1) {
                    __log.error("Error creating deployed marker file, " + file + " will not be deployed");
                    continue;
                }

                try {
                    _aspectStore.undeployAspect(file);
                } catch (Exception ex) {
                    __log.error("Error undeploying " + file.getName());
                }

                try {
                    Collection<QName> deployed = _aspectStore.deployAspect(file, "true", _odeServer.getProcessStore());
                    __log.info("Deployment of artifact " + file.getName() + " successful: " + deployed );
                } catch (Exception e) {
                    __log.error("Deployment of " + file.getName() + " failed, aborting for now.", e);
                }
            }
            // Removing deployments that disappeared
            File[] deployed = _aspectDeployDir.listFiles(_deployedFilter);
            for (File file : deployed) {
                String pkg = file.getName().substring(0, file.getName().length() - ".deployed".length());
                File deployDir = new File(_aspectDeployDir, pkg);
                if (!deployDir.exists()) {
                    Collection<QName> undeployed = _aspectStore.undeployAspect(deployDir);
                    file.delete();
                    disposeDeployXmlWatchDog(deployDir);
                    if (undeployed.size() > 0)
                        __log.info("Successfully undeployed " + pkg);
                }
            }
            
            checkSystemCronConfigWatchDog();
	}
        
       
	}

	

	@Override
	public void markAsDeployed(File file) {
		  File deployedMarker = new File(_aspectDeployDir, file.getName() + ".deployed");
	        try {
	            boolean isCreated = deployedMarker.createNewFile();
	            if (!isCreated) {
	                __log.error("Error while creating  file " + file.getName()
	                        + ".deployed ,deployment could be inconsistent");
	            }
	        } catch (IOException e) {
	            __log.error("Couldn't create marker file for " + file.getName());
	        }
	}
	
	
	@Override
	public void markAsUndeployed(File file) {
		  File deployedMarker = new File(_aspectDeployDir, file.getName() + ".deployed");
	        boolean isDeleted = deployedMarker.delete();
	        if (!isDeleted) {
	            __log
	                    .error("Error while deleting file "
	                            + file.getName()
	                            + ".deployed , please check if file is locked or if it really exist");
	        }
	}

}
