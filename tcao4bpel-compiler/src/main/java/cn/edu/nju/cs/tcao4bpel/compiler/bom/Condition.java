package cn.edu.nju.cs.tcao4bpel.compiler.bom;

import java.util.List;

import org.w3c.dom.Element;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * Condition.java
 */
public class Condition extends AspectObject{

	public Condition(Element el) {
		super(el);
	}
	
	public List<Place> getPlaces(){
		return getChildren(Place.class);
	}

}
