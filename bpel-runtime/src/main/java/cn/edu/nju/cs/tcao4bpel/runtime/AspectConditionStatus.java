/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.runtime;

import org.apache.ode.jacob.Channel;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-28 2015
 * ConditionStatus.java
 */
public interface AspectConditionStatus extends Channel{

	public void conditionStatus(boolean value);
		
	
}
