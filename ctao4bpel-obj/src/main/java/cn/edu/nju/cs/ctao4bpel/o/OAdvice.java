package cn.edu.nju.cs.ctao4bpel.o;

import org.apache.ode.bpel.o.OProcess;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * OAdvice.java
 */
public class OAdvice extends OProcess{
	
	private static final long serialVersionUID = -4654050246570622492L;
	
	private OAspect oaspect;

	public OAspect getOaspect() {
		return oaspect;
	}
	public void setOaspect(OAspect oaspect) {
		this.oaspect = oaspect;
	}
	
	public OAdvice(String bpelVersion) {
		super(bpelVersion);
	}



}
