package cn.edu.nju.cs.ctao4bpel.o;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

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
