package cn.edu.nju.cs.ctao4bpel.store;
import javax.xml.namespace.QName;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * AspectStoreEvent.java
 */
public class AspectStoreEvent {
	public enum Type{
		DEPLOYED,
		UNDEPLOYED
	}
	public final Type type;
	public final QName aid;
	public final String deploymentUnit;
	
	public AspectStoreEvent(Type type, QName aid, String aspectDeploymentUnit){
		this.type=type;
		this.aid=aid;
		this.deploymentUnit=aspectDeploymentUnit;
	}
	
	@Override
    public String toString() {
        return "{AspectStoreEvent#" + type + ":" + aid +"}";
    }
	
	

}
