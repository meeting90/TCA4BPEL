package cn.edu.nju.cs.tcao4bpel.compiler.bom;

import javax.xml.namespace.QName;
/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * CTAO4BPEL20QNames.java
 */
public class CTAO4BPEL20QNames {
	public static final String NS_CTAO4BPEL2_0_ASPECT="http://cs.nju.edu.cn/tcao4bpel/2.0/aspect";
	public static final QName ASPECT = newFinalQName("aspect");
	public static final QName ADVICE = newFinalQName("advice");

	public static final QName POINTCUT =newFinalQName("pointcut");
	public static final QName PRECONDITION = newFinalQName("preCondition");
	public static final QName POSTCONDITION = newFinalQName("postCondition");
	public static final QName PLACE = newFinalQName("place");
	public static final QName SKIP = newFinalQName("skip");

	private static QName newFinalQName(String localname){
		return new QName(NS_CTAO4BPEL2_0_ASPECT, localname);
	}
	


}
