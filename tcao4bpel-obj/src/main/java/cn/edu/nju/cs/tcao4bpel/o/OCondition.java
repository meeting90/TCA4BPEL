package cn.edu.nju.cs.tcao4bpel.o;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * OCondition.java
 */
public class OCondition implements Serializable{

	private static final long serialVersionUID = -6938411595221523596L;
	private Set<OPlace> places= new HashSet<OPlace>();
	
	public Set<OPlace> getPlaces(){
		return this.places;
	}
	
	public void addPlace(OPlace place){
		this.places.add(place);
	}
	
	

}
