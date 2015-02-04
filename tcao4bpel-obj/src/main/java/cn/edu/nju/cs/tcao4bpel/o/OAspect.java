package cn.edu.nju.cs.tcao4bpel.o;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * OAspect.java
 */
public class OAspect implements Serializable{

	private static final long serialVersionUID = -1028076817167548777L;
	private QName processId;
	
	private String targetNamespace;
	private String aspectName;
	private OPointcut pointcut;
	private boolean skip;
	private OAdvice advice;
	private String scope;
	List<String> skippedActivities=new ArrayList<String>();
	
	public QName getProcessId() {
		return processId;
	}

	public void setProcessId(QName processId) {
		this.processId = processId;
	}

	
	public String getScope() {
		return scope;
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public OAspect() {
		
	}
	
	public String getTargetNamespace() {
		return targetNamespace;
	}

	public void setTargetNamespace(String targetNamespace) {
		this.targetNamespace = targetNamespace;
	}

	public String getAspectName() {
		return aspectName;
	}

	public void setAspectName(String aspectName) {
		this.aspectName = aspectName;
	}

	public OPointcut getPointcut() {
		return pointcut;
	}
	public void setPointcut(OPointcut pointcut) {
		this.pointcut = pointcut;
	}
	public boolean isSkip() {
		return skip;
	}
	public void setSkip(boolean skip) {
		this.skip = skip;
	}
	public OAdvice getAdvice() {
		return advice;
	}

	public void setAdvice(OAdvice advice) {
		this.advice = advice;
	}

	public QName getQName(){
		return new QName(targetNamespace,aspectName);
	}
	public void addSkippedActivities(String xpathExpression){
		this.skippedActivities.add(xpathExpression);
	}
	
	

}
