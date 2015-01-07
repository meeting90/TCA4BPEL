package cn.edu.nju.cs.ctao4bpel.o;

import java.io.Serializable;

public class OPointcut implements  Serializable{


	private static final long serialVersionUID = -7074843474226939064L;
	
	private OPreCondition preCondition;
	private OPostCondition postCondition;
	
	public void setPreCondition(OPreCondition pre){
		this.preCondition=pre;
	}
	public OPreCondition getPreCondition(){
		return this.preCondition;
	}
	public void setPostCondition(OPostCondition post){
		this.postCondition = post;
	}
	public OPostCondition getPostCondition(){
		return this.postCondition;
	}
	
}
