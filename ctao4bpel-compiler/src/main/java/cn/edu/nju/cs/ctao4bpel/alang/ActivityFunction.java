package cn.edu.nju.cs.ctao4bpel.alang;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * ActivityFunction.java
 */
import java.util.Collection;

import org.apache.ode.bpel.compiler.bom.Activity;
import org.apache.ode.bpel.compiler.bom.Process;

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
	 * @param process
	 * @return
	 */
	Collection<Activity> getActivities(ActivityFunctionStruct struct, Process process);

}
