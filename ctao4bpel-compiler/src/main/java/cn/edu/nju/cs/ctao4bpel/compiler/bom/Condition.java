package cn.edu.nju.cs.ctao4bpel.compiler.bom;

import java.util.List;

import org.w3c.dom.Element;

public class Condition extends AspectObject{

	public Condition(Element el) {
		super(el);
	}
	
	public List<Place> getPlaces(){
		return getChildren(Place.class);
	}

}
