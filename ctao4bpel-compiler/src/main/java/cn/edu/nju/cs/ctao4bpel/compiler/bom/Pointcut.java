package cn.edu.nju.cs.ctao4bpel.compiler.bom;

import org.w3c.dom.Element;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * Pointcut.java
 */
public class Pointcut extends AspectObject{

	public Pointcut(Element el) {
		super(el);
	}
	
	public String getPointcutType(){
		return getAttribute("type",null);
	}
	public PreCondition getPreCondition(){
		return getFirstChild(PreCondition.class);
	}
	public PostCondition getPostCondition(){
		return getFirstChild(PostCondition.class);
	}
	

}
