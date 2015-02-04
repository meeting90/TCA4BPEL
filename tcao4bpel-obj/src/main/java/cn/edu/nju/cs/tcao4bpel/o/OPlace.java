package cn.edu.nju.cs.tcao4bpel.o;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.ode.bpel.o.OActivity;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * OPlace.java
 */
public class OPlace implements Serializable{
    
	private OCondition parent;
	private State state;
	
	
	public enum State{
		READY,
		FINISHED,
	}
	
	List<String> xpaths = new ArrayList<String>();
	
	
	
	public OPlace(OCondition condition){
		this.parent = condition;
	}
	
	public OCondition getParent(){
		return this.parent;
	}
	
	public void setParent(OCondition parent) {
		this.parent = parent;
	}

	public State getState(){
		return state;
	}
	
	public void setState(State state){
		this.state = state;
	}

	public List<String> getXpaths() {
		return xpaths;
	}

	public void setXpaths(List<String> xpaths) {
		this.xpaths = xpaths;
	}
	
	public void addXpath(String xpath){
		this.xpaths.add(xpath);
	}
	





	/**
	 * 
	 */
	private static final long serialVersionUID = 9017676073433154009L;

}
