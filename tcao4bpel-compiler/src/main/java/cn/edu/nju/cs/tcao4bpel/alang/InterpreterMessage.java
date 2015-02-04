/**
 * 
 */
package cn.edu.nju.cs.tcao4bpel.alang;

import org.apache.ode.utils.msg.MessageBundle;

/**
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-8 2015
 * InterpreterMessage.java
 */
public class InterpreterMessage extends MessageBundle {
	/**
	 * 
	 * @param a
	 * @return
	 */
	String msgSyntaxErr(){
		return format("Activity function syntax error!");
	}
	String msgParamErr(){
		return format("Activity function parameter key cannot be recognized!");
	}
	
	String msgAttrNotSpecified(String key, String e){
		return format("{0} not specified for element {1}", key, e);
	}
	String msgMatchFailed(ActivityFunctionStruct.StructElement struct, String e){
		return format("Match {0} \"{1}\" failed for element {2}", struct.key,struct.expression, e);
	}
	String msgMatched(ActivityFunctionStruct struct,int matchCount){
		return format("{0} matched {1} activities", struct.toString(), matchCount);
	}
	String msgNotMatched(ActivityFunctionStruct struct){
		return format("{0} do not match any  activity", struct.toString());
	}
}
