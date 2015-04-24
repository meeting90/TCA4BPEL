/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import cn.edu.nju.cs.tcao4bpel.o.OAspect;
import cn.edu.nju.cs.tcao4bpel.o.OPlace;

/**
 * @author M`ingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-28 2015
 * AspectFrame.java
 */
public class AspectFrame implements Serializable{

	
	private static final long serialVersionUID = -3809645749871560083L;

	private List<AspectInfo> aspectInfos; 
	
	public AspectFrame(List<AspectInfo> aspectInfos){
		this.aspectInfos = aspectInfos;
	}

	public List<AspectInfo> getAspectInfos() {
		return aspectInfos;
	}

	public Set<OPlace> getPostConditions(OAspect oaspect){
		return oaspect.getPointcut().getPostCondition().getPlaces();
	}
	
	public Set<OPlace> getPreConditions(OAspect oaspect){
		
		return oaspect.getPointcut().getPreCondition().getPlaces();
	}
	



	

}
