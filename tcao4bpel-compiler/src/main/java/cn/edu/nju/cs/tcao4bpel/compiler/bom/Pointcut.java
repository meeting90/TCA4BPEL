package cn.edu.nju.cs.tcao4bpel.compiler.bom;

import org.w3c.dom.Element;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * Pointcut.java
 */
public class Pointcut extends AspectObject{
	public static final String PTYPE= "pattern";
	public enum PointcutType{
		PATTERN,
		OTHER
	}
	public Pointcut(Element el) {
		super(el);
	}
	
	public PointcutType getPointcutType(){
		if(PTYPE.equalsIgnoreCase(getAttribute("type",null)))
			return PointcutType.PATTERN;
		else
			return PointcutType.OTHER;
	}
	public PreCondition getPreCondition(){
		return getFirstChild(PreCondition.class);
	}
	public PostCondition getPostCondition(){
		return getFirstChild(PostCondition.class);
	}
	

}
