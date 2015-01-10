package cn.edu.nju.cs.ctao4bpel.compiler;

import javax.xml.namespace.QName;

import org.apache.ode.bpel.compiler.api.CompilationMessage;
import org.apache.ode.bpel.compiler.api.CompilationMessageBundle;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * ValidatorMessages.java
 */
public class ValidatorMessages extends CompilationMessageBundle {
	
	
	CompilationMessage msgExceptionErr(Exception e){
		return formatCompilationMessage(e.getMessage());
	}
	CompilationMessage msgNullErr(String type){
		return formatCompilationMessage("null for element {0}", type);
	}
	
	CompilationMessage msgBaseBpelNotFoundErr(String bpelurl){
		return formatCompilationMessage("Unable to find bpel process: {0}", bpelurl);
	}
	
	CompilationMessage msgExpressionUndefined(String expression){
		return formatCompilationMessage("Expression {0} cannot be understand!", expression);
	}
	CompilationMessage msgActivityNotFoundErr(String expression,QName pid){
		return formatCompilationMessage("Unable to match any activity of expression {0} in process {1}!", expression, pid.toString());
	}
	//no pattern each expression of activity should refer to one one activity
	CompilationMessage msgNotUniqueActivityErr(String expression, QName pid){
		return formatCompilationMessage("expression {0} refers to more than one activity in process {1}");
	}
	//pattern type pre size and post size should be 0-1
	//if pre size == post size ==1 
	//then pre expression  should equal to post expression
	CompilationMessage msgPrePostNotDefinedErr(){
		return formatCompilationMessage("PreCondition and PostCondition are all emptysets!");
	}
	CompilationMessage msgPreSizeLimitErr(int size){
		return formatCompilationMessage("Precondition size is {0}, more than one in patten type!", size);
	}
	CompilationMessage msgPostSizeLimitErr(int size){
		return formatCompilationMessage("Postcondition size is {0}, more than one in patten type!", size);
	}
	CompilationMessage msgNotSameExpressionErr(String preExp ,String postExp){
		return formatCompilationMessage("preExp {0} and postExp {1} in not same in patten type!", preExp,postExp);
	}
	// N=skip no patten type, pre size =1 post size =1 , preExp refer to one activity and postExp refer to one activity
	// preExp ~ postExp should refer to a region or one activity.
	CompilationMessage msgSkipPrePostSizeErr(int preSize ,int postSize){
		return formatCompilationMessage("preCondition size =  {0} and postCondition size =  {1} in not allowed when skip = ture!", preSize,postSize);
	}
	CompilationMessage msgNotRegionErr(String preExp, String postExp){
		return formatCompilationMessage("pre expression {0} and post expression {1} do not refer to a region", preExp, postExp);
	}
	CompilationMessage msgOrderErr(String preExp, String postExp){
		return formatCompilationMessage("activity related to post expression {1} happened before activity related to pre expression {0} ", preExp, postExp);
	}
	
	
	//boundedness
	CompilationMessage msgBoundednessErr(){
		return formatCompilationMessage("Petri net after adding aspect semantics is unbounded!");
		
	}
	//reachability
	CompilationMessage msgDeadlockErr(){
		return formatCompilationMessage("Petri net after adding aspect semantics is deadlock, confilct to control dependencies of the base process");
	}
	CompilationMessage msgAspectUnreachableErr(){
		return formatCompilationMessage("Aspect activity is unreachable due to  unproper definition");
	}
	CompilationMessage msgBaseActivityUnreachableErr(String name){
		return formatCompilationMessage("Base activity {0} is unreachable!", name);
	}

}
