/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.edu.nju.cs.tcao4bpel.o.OAspect;
import cn.edu.nju.cs.tcao4bpel.o.OPlace;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-28 2015
 * AspectPostConInfo.java
 */
public class AspectInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8193976259624518031L;
	OAspect oaspect;
	Map<OPlace, AspectConditionStatus> postConditions= new HashMap<OPlace, AspectConditionStatus>();
	Map<OPlace, AspectConditionStatus> preConditions = new HashMap<OPlace, AspectConditionStatus>();

	
	public AspectInfo(OAspect oaspect){
		this.oaspect = oaspect;
	}
	public AspectConditionStatus resolvePre(OPlace place){
		return preConditions.get(place);
	}
	public AspectConditionStatus resolvePost(OPlace place){
		return postConditions.get(place);
	}
	
	public void setPreConditions(Map<OPlace, AspectConditionStatus> preConditions){
		this.preConditions = preConditions;
	}
	public void setPostConditions(Map<OPlace, AspectConditionStatus> postConditions){
		this.postConditions = postConditions;
	}
	
	
	
	
	
	
	
	
}
