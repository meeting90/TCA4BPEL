package cn.edu.nju.cs.ctao4bpel.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;

import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.compiler.api.CompilationMessageBundle;
import org.apache.ode.bpel.compiler.bom.Activity;
import org.apache.ode.bpel.compiler.bom.BpelObjectFactory;
import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.bpel.o.OProcess;
import org.apache.ode.utils.StreamUtils;
import org.xml.sax.InputSource;

import cn.edu.nju.cs.ctao4bpel.compiler.bom.Advice;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Aspect;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.AspectObjectFactory;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Pointcut;
import cn.edu.nju.cs.ctao4bpel.o.OAspect;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * ActivityFunction.java
 */
public class AspectValidator {
	private File _aspectFile;
	private File _processFile;
	private Aspect _aspect;
	private Process _process;

	protected static final ValidatorMessages _cmsgs = CompilationMessageBundle
			.getMessages(ValidatorMessages.class);

	public AspectValidator(File af, File pf, OAspect oaspect, OProcess oprocess) {
		this._aspectFile = af;
		this._processFile = pf;

	}

	public void initBom() {

	}

	public void preValidate() throws CompilationException {
		try {
			@SuppressWarnings("deprecation")
			InputSource aisrc = new InputSource(new ByteArrayInputStream(
					StreamUtils.read(_aspectFile.toURL())));
			aisrc.setSystemId(_aspectFile.getAbsolutePath());
			this._aspect = AspectObjectFactory.getInstance().parseAspect(aisrc,
					_aspectFile.toURI());

			if (_aspect.getAdvice() == null)
				throw new CompilationException(_cmsgs.msgNullErr(Advice.class
						.toString()));

			if (_aspect.getPointcut() == null)
				throw new CompilationException(_cmsgs.msgNullErr(Pointcut.class
						.toString()));

			if (_processFile == null)
				throw new CompilationException(
						_cmsgs.msgBaseBpelNotFoundErr(_aspect.getBpelUrl()));
			InputSource pisrc = new InputSource(new ByteArrayInputStream(
					StreamUtils.read(_processFile.toURL())));
			pisrc.setSystemId(_processFile.getAbsolutePath());
			this._process = BpelObjectFactory.getInstance().parse(pisrc,
					_processFile.toURI());

		} catch (Exception e) {
			throw new CompilationException(_cmsgs.msgExceptionErr(e), e);
		}

	}

	public Collection<Activity> retriveActivity() {
		return null;
	}

	public void bpel2Pnml() {

	}

	public void addAspectPnml() {

	}

	public void validatePnml() {

	}

}
