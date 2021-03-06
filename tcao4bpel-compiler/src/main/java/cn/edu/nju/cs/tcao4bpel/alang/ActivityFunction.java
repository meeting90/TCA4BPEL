package cn.edu.nju.cs.tcao4bpel.alang;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * ActivityFunction.java
 */
import java.util.List;

import org.apache.ode.bpel.compiler.bom.Activity;

public interface ActivityFunction {
	
	
	/**
	 * interpreter expression to ActivityFunctionStruct
	 * 
	 * @param expression
	 * @return
	 * @throws InterpreterException
	 */
	ActivityFunctionStruct interpreter(String expression) throws InterpreterException;
	
	
	
	/**
	 * get activities of struct  from process 
	 * @param struct
	 * @return
	 */
	List<Activity> getActivities(ActivityFunctionStruct struct);

}
