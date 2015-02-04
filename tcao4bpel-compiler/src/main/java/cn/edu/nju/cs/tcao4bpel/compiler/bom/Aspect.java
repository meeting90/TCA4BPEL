package cn.edu.nju.cs.tcao4bpel.compiler.bom;

import org.w3c.dom.Element;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * Aspect.java
 */
public class Aspect extends AspectObject{

	public Aspect(Element el) {
		super(el);
	}
	public String getName(){
		return getAttribute("name");
	}
	public String getTargetNamespace(){
		return getAttribute("targetNamespace",null);
	}
	public String getBpelUrl(){
		return getAttribute("bpelurl");
	}
	public boolean isSkip(){
		return getFirstChild(Skip.class)==null? false : true;
	}
	public Advice getAdvice(){
		return getFirstChild(Advice.class);
	}
	public Pointcut getPointcut(){
		return getFirstChild(Pointcut.class);
	}
	

}
