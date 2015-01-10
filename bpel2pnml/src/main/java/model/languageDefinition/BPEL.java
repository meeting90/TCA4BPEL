/**
 * BPEL.java
 * Purpose: Specifies relevant (with respect to the BABEL project) properties of the BPEL
 * 			language.
 * 			IMPORTANT REMARK.
 * 				This class could be used in the Future for specification-driven translations.
 * 				e.g. : the relevant information and mapping rules are captured in a class.
 * 						OR we have a text-file e.g. written in XML which does the job.
 * 				(Idea: LanguageSpec Interface, class BPEL implements LanguageSpec)
 * 				
 * @author Stephan Breutel
 * @version 1.0
 * @created 6/04/2005
 *  Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package model.languageDefinition;

import tools.StringTools;

import java.util.List;
import java.util.ArrayList;
import org.jdom.Element;
import org.jdom.Attribute;



public class BPEL
   {
    public static String [] topActivity					= {"process"};
   	public static String [] structuredActivities		= {"flow","pick","scope","sequence","switch","while","process"}; 
   	public static String [] structuredActivitiesAbbr	= {"flw","pck","scp","seq","swh","whl","ps"};
   	public static String [] basicActivities     		= {"invoke","receive","wait","reply","assign","throw","empty","terminate","exit","compensate"};
   	public static String [] basicActivitiesAbbrevation 	= {"inv","rec","wait","rep","ass","thr","emp","tmt","ext","cps"};
   	/* 02.09.05: BPEL1.1. modification: source,target added */
   	public static String [] controlLinks				= {"sources","targets","source","target"};
   	public static String [] linkConditions				= {"joinCondition","transitionCondition"};
   	public static String [] dataAttributes				= {"partnerLink","portType","name","operation","variable",
   	      											   		"inputVariable","outputVariable"};   	
   	public static String [] dataAttributesAbbrevation	= {"pL","pT","name","op","var","iVar","oVar"};
   	public static String [] dataActivity				= {"copy","from","to"};
   	public static String [] fromAttr					= {"expression","variable","part"};
   	public static String [] toAttr						= {"expression","variable","part"};
   	public static String [] events						= {"onMessage","onAlarm","onEvent"};
   	public static String [] eventsAbbr					= {"onMsg","onAlarm","onEv"};
   	public static String [] dataItem					= {"links"};
   	public static String [] dataParts					= {"partnerLinks","variables","correlations"};
   	public static String [] handlers					= {"eventHandlers","faultHandlers","compensationHandler"};
   	public static String [] catches						= {"catch","catchAll"};
   	public static String [] catchAbbreviation			= {"cth","cthA"};
   	public static String throwActivity					= "throw";   
   	public static String [] throwActivityAbr			= {"thr"};
   	public static String eventHandlers					= "eventHandlers";
   	public static String faultHandlers					= "faultHandlers";
   	public static String faultName						= "faultName";
   	public static String faultVariable					= "faultVariable";
   	public static String faultType						= "faultMessageType";
   	public static String [] faultAttr					= {"faultName","faultVariable","faultMessageType"};
   	public static String compensationHandler			= "compensationHandler";   	
   	public static String[] exitCommands					= {"terminate","exit"}; /* old: terminate, new: exit */   	
   	public static String[] exitCommandsAbr				= {"tmt","ext"};
   	public static String[] compensateCommand			= {"compensate"}; // declared as array because of its usuage.
   	public static String[] varTypes						= {"type","messageType","element"};
   	
   	public static String[] receiveNames				    = {"receive","rcv","rec"};
   	public static String[] invokeNames					= {"invoke","inv"};
   	
   
   	
   	/* Extended for SPM2BPEL and BPMN2BEPL */
   	
   	public static String  myRole 		= "myRole";
   	public static String  partnerRole	= "partnerRole";  
    public static String  sequence		= "sequence";
    public static String  pick			= "pick";
    public static String  receive		= "receive";
    public static String  invoke 		= "invoke";
    public static String  assign		= "assign";
    
    public static String partnerLinks	= "partnerLinks";
    public static String partnerLink	= "partnerLink";
    public static String portType		= "portType";
    public static String operation		= "operation"; 
    public static String kW_varibales	= "variables";
    public static String attr_variable	= "variable";
    public static String flow			= "flow";
    public static String onEvent		= "onEvent";
    
    public static String keyWord_switch		= "switch";
    public static String keyWord_case		= "case";
    public static String attr_condition		= "condition";
    public static String keyWord_otherwise	= "otherwise";
    
    public static String defaultPL			= "local";
    public static String defaultPT			= "localPT"; 
    public static String attrName			= "name";
   
    
    /* Extended for BPMN 2 BPEL */
    public static String attrLinkName	= "linkName";
    public static String _switch		= "switch";   
    public static String _case			= "case";
    public static String _while			= "while";
    public static String doWhile		= "doWhile";
    public static String repeatWhile	= "repeatWhile";
    public static String otherwise		= "otherwise";
   	public static String scope 			= "scope";
   	public static String condition		= "condition";
   	public static String joinCondition	= "joinCondition";
   	public static String transitionCondition = "transitionCondition";   
   	public static String links			= "links";
   	public static String link			= "link";
   	public static String and			= "and";
   	public static String or				= "or";
   	public static String not			= "not";
   	public static String empty			= "empty";
   	public static String linkStatus		= "LinkStatus";
   	public static String source			= "source";
   	public static String target			= "target";
   	public static String onMsg			= BPEL.events[0];
   	public static String onAlarm		= BPEL.events[1];
    
   	public static Element mkPartnerLinkElement()
   	   {
   	    Element e = new Element("partnerLink");
   	    e.setAttribute("name","local");
   	    e.setAttribute("partnerLinkType","localPT");   	    
   	    return e;
   	   }
   	
   	public static Element mkPartnerLinksElement()
   	   {
   	    Element e = new Element("partnerLinks");   	    
   	    return e;
   	   }
   	
   	public static Element mkVarDeclElement()
   	   {
   	    return new Element("variables");
   	   }
   	
   	public static Element mkEHElement()
   	   {
   	    return new Element("EventHandlers");
   	   }
   	
   	
   	
      /**
       * MethodName : BPEL
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a BPEL object has been created.
       * 
       */
      public BPEL()
         {
            super();
            // TODO Auto-generated constructor stub
         }

      
      public static boolean isStructuredActivity(String s)
         {
           	return StringTools.isInStringArray(s,BPEL.structuredActivities);	
         }
      
      public static boolean isBasicActivity(String s)
         {
          	return StringTools.isInStringArray(s,BPEL.basicActivities); 
         }
      
      public static boolean isControlLink(String s)
         {
         	return StringTools.isInStringArray(s,BPEL.controlLinks);
         }
      
      public static boolean isDataActivity(String s)
         {
         	return StringTools.isInStringArray(s,BPEL.dataActivity);
         }
      
      public static boolean isDataAttribute(String s)
         {
         	return StringTools.isInStringArray(s,dataAttributes);
         }
      
      public static boolean isEvent(String s)
         {
         	return StringTools.isInStringArray(s,BPEL.events);
         }
      
      public static boolean hasJoinCondition(Element e)
         {
            // 02.09.05: || BPEL.controlLinks[3]==0
         	if (e.getName().compareTo(BPEL.controlLinks[1])==0
         	      || e.getName().compareTo(BPEL.controlLinks[3])==0)
         	      return true;
         	return false;
         	         
         }
      
      /**
       * 
       * MethodName	: controlLinkElementTo20
       * Purpose    	: change BPEL 1.1 control-link elements into 2.0 elements. 
       * Pre		  	: e is a BPEL1.1 controlLink element
       * Post		  	: e is transformed into a semantically equivalent BPEL2.0 element.
       * 
       * @param e
       * @return	e20, element according to BPEL2.0
       */
      public static Element controlLinkElementTo20(Element e)
         {
          Element e20;
          if (e.getName().compareTo(BPEL.controlLinks[2])==0)
             e20 = new Element(BPEL.controlLinks[0]); // sources
          else
             e20 = new Element(BPEL.controlLinks[1]); // targets
          // Marlons List 2+3: join/transition conditions are attributes in BPEL1.1, but childs in BPEL2.0
          for (int i=0;i<BPEL.linkConditions.length;i++)
             {
              Attribute a = e.getAttribute(BPEL.linkConditions[i]);
              if (a!=null)
                {
                 Element eA = new Element(BPEL.linkConditions[i]);
                 eA.addContent(a.getValue());
                }
             }      
          // BugFix 22.06.06 -- see Marija Petkovic email 14.06                                 
          e20.addContent((Element) e.clone());          
          return e20;
         }
      /**
       * 
       * MethodName		: buildLinkName
       * Purpose    	: given a <target linkName="name"> structure. Extract name and "wrap" it
       * 				  using the BPEL convention for a link Status. 
       * Pre		  	: the element is of the structure <target linkName="name">, i.e. has 
       * 				  a linkName attribute.
       * Post		  	: 
       * 
       * @param e
       * @return 
       */
      public static String buildLinkName(Element e)
         {
          String linkName = e.getAttributeValue("linkName");
          System.out.println("LinkName: " + linkName);
          return "bpws:getLinkStatus('" + linkName + "')";
         }
      /**
       * 
       * MethodName		: getAbbreviationFor
       * Purpose    	: 
       * Pre		  	: 
       * Post		  	: abbrevation for s is returned XOR s if there is no Abbrevation.
       * 
       * @param s
       * @return
       */
      public static String getAbbreviationForActivity(String s)
         {
          if (BPEL.isStructuredActivity(s))
             return BPEL.structuredActivitiesAbbr[StringTools.indexOfStringInArray(s,BPEL.structuredActivities)];
          else if (BPEL.isBasicActivity(s))
             return BPEL.basicActivitiesAbbrevation[StringTools.indexOfStringInArray(s,BPEL.basicActivities)];
          else if (BPEL.isDataActivity(s))
                return BPEL.dataAttributesAbbrevation[StringTools.indexOfStringInArray(s,BPEL.dataAttributes)];
          else if (BPEL.isEvent(s))
             	return BPEL.eventsAbbr[StringTools.indexOfStringInArray(s,BPEL.events)];
                   
          return s;
         }
      /**
       * 
       * MethodName		: getListofRules
       * Purpose    	: 	This method is an experiment and is used to substitute a text-document
       * 				 	reporting about the different assumptions and rules about the BPEL language
       * 					and its translation into PNML/YAWL. 
       * 					A future usage idea is that this could define the basis for a specification
       * 					driven translation.
       * Pre		  	:
       * Post		  	: 
       * 
       * @return
       */
      public static List getListofRules()
         {
         	List rules=new ArrayList();
         	
         	String ruleHeader = "List of General Rules and Assumptions.\n" + 
         						"======================================";         	
         	rules.add(ruleHeader);
         	rules.add("assign => (assign.copy,assign.copy.from,assign.copy.to)");
         		
         	return rules;         	
         }
   }
