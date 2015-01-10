package cn.edu.nju.cs.ctao4bpel.alang;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.ode.bpel.compiler.bom.Bpel20QNames;

import org.apache.ode.bpel.compiler.bom.Process;
import org.w3c.dom.Element;

/**
 * 
 * @author Mingzhu Yuan @ cs.nju.edu.cn
 * 2015-1-7 2015
 * ActivityFunctionStruct.java 
 */
public final class ActivityFunctionStruct {
	public static final String XPATH_KEY="xpath"; 
	public static final String NAME_KEY="name";
	public static final String TYPE_KEY="type";
	public static final String OPERATION_KEY="operation";
	public static final String PARTNERLINK_KEY="partnerlink";
	
	
	

	private final Xpath xpath;
	private final NamePattern name;
	private final TypePattern type;
	private final OperationPattern operation;
	private final PartnerlinkPattern partnerlink;
	
	private List<ActivityFunctionStruct.StructElement>  _result = new ArrayList<ActivityFunctionStruct.StructElement>();
	
	public static class StructElement{
		final String key;
		final String expression;
		public StructElement(String key, String expression){
			this.key = key;
			this.expression = expression;
		}
		/**
		 * default is regular expression syntax validate
		 * @throws Exception
		 */
		public void validateExpression() throws Exception{
			Pattern.compile(expression);
		}
	}
	
	public  static  class Xpath extends StructElement{
		
		
		
		private NamespaceContext ctx = new NamespaceContext() {
			public String getNamespaceURI(String prefix) {		    				
				if("bpel".equals(prefix))
					return Bpel20QNames.NS_WSBPEL2_0_FINAL_EXEC;
				return null;
			}
			// not used
			@SuppressWarnings("rawtypes")
			public Iterator getPrefixes(String val) {
				return null;
			}
			// not used
			public String getPrefix(String uri) {
				return null;
			}
		};
		private XPath xpath = XPathFactory.newInstance().newXPath();
		
		public Xpath(String expression) {
			super(XPATH_KEY,expression);
			xpath.setNamespaceContext(ctx);
			
		}
		/**
		 * xpath expression syntax validate
		 */
		@Override
		public void validateExpression() throws Exception {
			
			xpath.compile(expression);
			
		}
		
		public Element getElement(Process process) throws XPathExpressionException{
			return (Element) xpath.evaluate(expression, process.getElement(), XPathConstants.NODE);
		}

	}
	public  static class NamePattern extends StructElement{

		public NamePattern(String expression) {
			super(NAME_KEY,expression);
		}
	
	}
	public  static   class TypePattern extends StructElement{

		public TypePattern(String expression) {
			super(TYPE_KEY,expression);
		}
		
	}
	public static  class OperationPattern extends StructElement{

		public OperationPattern(String expression) {
			super(OPERATION_KEY,expression);
		}
		
	}
	public static  class PartnerlinkPattern extends StructElement{
		public PartnerlinkPattern(String expression) {
			super(PARTNERLINK_KEY,expression);
		}
		
	}
	public ActivityFunctionStruct(Xpath xpath, NamePattern name, TypePattern type, OperationPattern operation, PartnerlinkPattern partnerlink ){
		this.xpath=xpath;
		this.name = name;
		this.type = type;
		this.operation= operation;
		this.partnerlink= partnerlink;
		if(xpath!=null)
			_result.add(xpath);
		if(name!=null)
			_result.add(name);
		if(type !=null)
			_result.add(type);
		if(operation!=null)
			_result.add(operation);
		if(partnerlink!=null)
			_result.add(partnerlink);
	}
	public Xpath getXpath() {
		return xpath;
	}
	public NamePattern getName() {
		return name;
	}
	public TypePattern getType() {
		return type;
	}
	public OperationPattern getOperation() {
		return operation;
	}
	public PartnerlinkPattern getPartnerlink() {
		return partnerlink;
	}
	
	public void syntaxValidate() throws Exception {
		if(this.xpath !=null)
			xpath.validateExpression();
		if(this.name!=null){
			name.validateExpression();
		if(this.type!=null)
			type.validateExpression();
		if(this.operation!=null)
			operation.validateExpression();
		if(this.partnerlink!=null)
			partnerlink.validateExpression();
		}
	}
	@Override
	public String toString() {
		return "ActivityFunctionStruct [xpath=" + xpath.expression + ", name=" + name.expression
				+ ", type=" + type.expression + ", operation=" + operation.expression
				+ ", partnerlink=" + partnerlink.expression + "]";
	}
	
	List<ActivityFunctionStruct.StructElement> getStructElement(){
		return _result;
	}

	
	
	
}
