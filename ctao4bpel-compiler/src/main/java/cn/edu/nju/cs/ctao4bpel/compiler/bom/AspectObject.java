package cn.edu.nju.cs.ctao4bpel.compiler.bom;

import org.apache.ode.bpel.compiler.bom.BpelObject;
import org.w3c.dom.Element;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectObject.java
 */
public class AspectObject extends BpelObject {

	public AspectObject(Element el) {
		super(el);
	}
	
	@Override
	protected BpelObject createBpelObject(Element element) {
        return AspectObjectFactory.getInstance().createBpelObject(element,_docURI);
    }

}
