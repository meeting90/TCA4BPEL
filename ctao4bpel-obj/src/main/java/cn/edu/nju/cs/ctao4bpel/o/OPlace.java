package cn.edu.nju.cs.ctao4bpel.o;

import java.io.Serializable;

public class OPlace implements Serializable{
    
	private OCondition parent;
	private String state;
	private String expression;
	
	
	public OPlace(OCondition condition){
		this.parent = condition;
	}
	
	public OCondition getParent(){
		return this.parent;
	}
	public String getState(){
		return state;
	}
	
	public void setState(String state){
		this.state = state;
	}
	
	public String getExpression() {
		return expression;
	}
	public void setExpression(String expression) {
		this.expression = expression;
	}









	/**
	 * 
	 */
	private static final long serialVersionUID = 9017676073433154009L;

}
