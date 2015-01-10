package cn.edu.nju.cs.ctao4bpel.compiler;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.compiler.api.CompilationException;
import org.apache.ode.bpel.compiler.api.CompilationMessageBundle;
import org.apache.ode.bpel.compiler.bom.Activity;
import org.apache.ode.bpel.compiler.bom.BpelObject;
import org.apache.ode.bpel.compiler.bom.BpelObjectFactory;
import org.apache.ode.bpel.compiler.bom.Process;
import org.apache.ode.bpel.o.OProcess;
import org.apache.ode.utils.StreamUtils;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import cn.edu.nju.cs.ctao4bpel.alang.ActivityFunction;
import cn.edu.nju.cs.ctao4bpel.alang.ActivityFunctionImpl;
import cn.edu.nju.cs.ctao4bpel.alang.ActivityFunctionStruct;
import cn.edu.nju.cs.ctao4bpel.alang.InterpreterException;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Advice;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Aspect;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.AspectObjectFactory;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Place;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.Pointcut;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.PostCondition;
import cn.edu.nju.cs.ctao4bpel.compiler.bom.PreCondition;
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

	
	@SuppressWarnings("deprecation")
	public void preValidate() throws CompilationException {
		try {
			if (_processFile == null)
				throw new CompilationException(
						_cmsgs.msgBaseBpelNotFoundErr(_aspect.getBpelUrl()));
			InputSource pisrc = new InputSource(new ByteArrayInputStream(
					StreamUtils.read(_processFile.toURL())));
			pisrc.setSystemId(_processFile.getAbsolutePath());
			this._process = BpelObjectFactory.getInstance().parse(pisrc,
					_processFile.toURI());
			
			InputSource aisrc = new InputSource(new ByteArrayInputStream(
					StreamUtils.read(_aspectFile.toURL())));
			aisrc.setSystemId(_aspectFile.getAbsolutePath());
			this._aspect = AspectObjectFactory.getInstance().parseAspect(aisrc,
					_aspectFile.toURI());
			
			validateAdvice();
			validatePointCut();

		

		} catch (Exception e) {
			throw new CompilationException(_cmsgs.msgExceptionErr(e), e);
		}

	}

	public void validateAdvice(){
		validateNotNull(_aspect.getAdvice(), Advice.class);
	}
	private void validateNotNull(BpelObject bo, Class<?> boClass){
		if(bo == null)
			throw new CompilationException(_cmsgs.msgNullErr(boClass
					.toString()));
		
	}
	public void validatePointCut(){
		Pointcut pointcut = _aspect.getPointcut();
		validateNotNull(pointcut,Pointcut.class);
		if(pointcut.getPointcutType()==Pointcut.PointcutType.PATTERN)
			validatePatternPointcut(pointcut);
		else
			validateNonPatternPointCut(pointcut);
	
	}
	


	/**
	 * @param pointcut 
	 * 
	 */
	private void validatePatternPointcut(Pointcut pointcut) {
		PreCondition pre = pointcut.getPreCondition();
		PostCondition post = pointcut.getPostCondition();
		validateNotNull(pre,PreCondition.class);
		validateNotNull(post,PostCondition.class);
		List<Place> pre_place = pre.getPlaces();
		List<Place> post_place = post.getPlaces();
		if(pre_place.size() > 1)
			throw new CompilationException(_cmsgs.msgPreSizeLimitErr(pre.getPlaces().size()));
		if(post_place.size() >1)
			throw new CompilationException(_cmsgs.msgPostSizeLimitErr(post.getPlaces().size()));
		if(post_place.size() == 0 && pre_place.size() == 0)
			throw new CompilationException(_cmsgs.msgPrePostNotDefinedErr());
		if(post_place.size() == 1 && pre_place.size() ==1){
			String pre_exp = pre_place.get(0).getExpression();
			String post_exp= post_place.get(0).getExpression();
			if(!pre_exp.equalsIgnoreCase(post_exp))
				throw new CompilationException(_cmsgs.msgNotSameExpressionErr(pre_exp, post_exp));
		}
		if(pre_place.size() ==1)
			validateExpression(pre_place.get(0));
		if(post_place.size() == 1)
			validateExpression(post_place.get(0));
	
	}
	
	/**
	 * @param pre_exp
	 */
	private void validateExpression(Place place) {
		try{
			ActivityFunction af = new ActivityFunctionImpl(_process);
			ActivityFunctionStruct struct = af.interpreter(place.getExpression());
			List<Activity> result = af.getActivities(struct);
			place.setActivities(result);
		}catch(InterpreterException e){
			throw new CompilationException(_cmsgs.msgExceptionErr(e));
		}
	}

	private void validateNonPatternExpr(Place place){
		validateExpression(place);
		if(place.getActivities().size()!=1)
			throw new CompilationException(_cmsgs.msgNotUniqueActivityErr(place.getExpression(), new QName( _process.getTargetNamespace(), _process.getName())));
		
	}
	/**
	 * @param pointcut 
	 * 
	 */
	private void validateNonPatternPointCut(Pointcut pointcut) {
		PreCondition pre = pointcut.getPreCondition();
		PostCondition post = pointcut.getPostCondition();
		validateNotNull(pre,PreCondition.class);
		validateNotNull(post, PostCondition.class);
		if(_aspect.isSkip()){
			if(pre.getPlaces().size() !=1 && post.getPlaces().size() != 1)
				throw new CompilationException(_cmsgs.msgSkipPrePostSizeErr(pre.getPlaces().size(), post.getPlaces().size()));
		}
		for(Place place : pre.getPlaces()){
			validateNonPatternExpr(place);
		}
		for(Place place: post.getPlaces()){
			validateNonPatternExpr(place);
		}
		if(_aspect.isSkip()){
			Activity start= pre.getPlaces().get(0).getActivities().get(0);
			Activity end = post.getPlaces().get(0).getActivities().get(0);
			validateIsRegion(start, end);
		}
	}
	
	private void validateIsRegion(Activity start, Activity end){
		//TODO 

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
