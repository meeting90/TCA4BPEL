package cn.edu.nju.cs.ctao4bpel.compiler.bom;

import org.w3c.dom.Element;

public class Place extends AspectObject {

	public Place(Element el) {
		super(el);
	}
	public String getState(){
		return getAttribute("state");
	}
	public String expressionlanguage(){
		return getAttribute("expressionlanguage");
	}
	
	public String getExpression(){
		return getTextValue();
	}
}
