package cn.edu.nju.cs.tcao4bpel.o;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
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
	





	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((parent == null) ? 0 : parent.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((xpaths == null) ? 0 : xpaths.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OPlace other = (OPlace) obj;
		if (parent == null) {
			if (other.parent != null)
				return false;
		} else if (!parent.equals(other.parent))
			return false;
		if (state != other.state)
			return false;
		if (xpaths == null) {
			if (other.xpaths != null)
				return false;
		} else if (!xpaths.equals(other.xpaths))
			return false;
		return true;
	}






	/**
	 * 
	 */
	private static final long serialVersionUID = 9017676073433154009L;

}
