/**
 * 
 */
package cn.edu.nju.cs.ctao4bpel.alang;

import org.apache.ode.utils.msg.MessageBundle;
import org.w3c.dom.Element;

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
	
	String msgAttrNotSpecified(String key, Element e){
		return format("{0} not specified for element {1}", key, e.toString());
	}
	String msgMatchFailed(ActivityFunctionStruct.StructElement struct, Element e){
		return format("Match {0} {1} failed for element {2}", struct.key,struct.expression, e.toString());
	}
	String msgMatched(ActivityFunctionStruct struct,int matchCount){
		return format("{0} matched {1} activities", struct.toString(), matchCount);
	}
	String msgNotMatched(ActivityFunctionStruct struct){
		return format("{0} do not match any  activity", struct.toString());
	}
}
