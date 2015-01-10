/*
 * Created on 16/05/2005
 * Purpose: 
 * 
 */
package model.backEnd;

import java.util.List;

import model.data.Block;

import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Attribute;
import java.util.ArrayList;

/**
 * EmitterYAWL.java
 * Purpose: 
 * @author Stephan Breutel
 * @version 1.0
 * @created 16/05/2005
 * 
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
public class EmitterYAWL implements Emitter
   {

      /**
       * MethodName : EmitterYAWL
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a EmitterYAWL object has been created.
       * 
       */
      public EmitterYAWL()
         {
            super();
            // TODO Auto-generated constructor stub
         }

      /**
       * 
       * MethodName	: setProperties
       * Purpose    	: sets general properties for the Emitter. 
       * Pre		  	: propertyList is a list of valid properties for a specific Emitter.
       * Post		  	: properties for the Emitter according to the propertyList have 
       * 				  been set.
       * 
       * @param o, property to set.
       * @param i, i-th entry of the property List.
       */
      public void setProperty(Object o,int i)
         {         
         }
      
      /**
       * 
       * MethodName	: getProperties
       * Purpose    	: returns the propertyList of an Emitter. 
       * Pre		  	: 
       * Post		  	: 
       * 
       * @return
       */
      public List getProperties()
         {
          return null;
         }
      /* (non-Javadoc)
       * @see model.backEnd.Emitter#header()
       */
      public String header()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#prologue(java.lang.String)
       */
      public String prologue(String processName)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#prologueElement(java.lang.String)
       */
      public Element prologueElement(String processName)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genTaskElement(java.util.List)
       */
      public Block genTaskElement(List info)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genStructureCode(java.lang.String, java.util.List, int)
       */
      public List[] genStructureCode(String operation, List eList, int treeLevel)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#setTreeHeight(int)
       */
      public void setTreeHeight(int h)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#incrNoPrevSA()
       */
      public void preProcessNode(List l)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genCaseName(org.jdom.Element, int, int)
       */
      public String genCaseName(Element e, int breadth, int treeDepth)
         {
            // TODO Auto-generated method stub
            return null;
         }
      
      public List genCatchList(Element e,int breadth,int treeDepth)
         {
          List catchList = new ArrayList();
          
          return catchList;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genSwitch(java.util.List[], java.util.List)
       */
      public Block genSwitch(List[] l, List info)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genFlow(java.util.List[], java.util.List)
       */
      public Block genFlow(List[] l, List info)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genWhile(java.util.List[], java.util.List)
       */
      public Block genWhile(List[] l, List info)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genSequence(java.util.List[], java.util.List)
       */
      public Block genSequence(List[] l, List info)
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#genScope(java.util.List[], java.util.List)
       */
      public Block genScope(List[] l, List info)
         {
            // TODO Auto-generated method stub
            return null;
         }

      public Block genHandler(List[] l,List info)
         {
          return null;
         }
      /* (non-Javadoc)
       * @see model.backEnd.Emitter#preProcessing(java.util.List)
       */
      public void preProcessing(List preList)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#postProcessing(java.util.List[])
       */
      public void postProcessing(List[] code)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#epilogue()
       */
      public String epilogue()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#taskControl()
       */
      public String taskControl()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#taskDecomposition()
       */
      public String taskDecomposition()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#netDecomposition(java.lang.String)
       */
      public String netDecomposition(String netName)
         {
            // TODO Auto-generated method stub
            return null;
         }
          
      public Element getProcessControllTemplate(List taskList,Namespace n)
         {
            Namespace n0=n;
         	Element e = new Element("processControlElements");
         	
         	e.setNamespace(n0);
         	Element eCurr = new Element("inputCondition");
         	eCurr.setNamespace(n0);
         	eCurr.setAttribute("id","0_InputCondition");
         	// assume taskList has at least one element.
         	Element task	=	(Element) taskList.get(0);
         	String  nE		= "2";
         	Attribute a = task.getAttribute("operation");
     	    if (a==null)
     	       nE = nE + "_" + task.getName();
     	    else
     	       nE = nE + "_" + task.getName() + a.getValue();
     	    Element eFlow = new Element("flowsInto");
     	    Element eNext = new Element("nextElementRef");
    	    eFlow.setNamespace(n0);
    	    eNext.setNamespace(n0);
    	    eNext.setAttribute("id",nE);
     	    eFlow.addContent(eNext); 
     	    eCurr.addContent(eFlow);
     	    
     	    e.addContent(eCurr);
     	    
         	int counter;
         	for (int i=1;i<taskList.size()+1;i++)
         	   {
         	    // previous
         	    String currentTask = nE;
         	    eCurr = new Element("task");         	    
         	    eCurr.setNamespace(n0);
         	    eCurr.setAttribute("id",nE);
         	    eFlow = new Element("flowsInto");
         	    eNext = new Element("nextElementRef");
         	    eFlow.setNamespace(n0);
         	    eNext.setNamespace(n0);
         	    
         	    // next
         	    counter = i + 2;
         	    nE = new Integer(counter).toString();
         	    if (i==taskList.size())
         	       nE="1_OutputCondition";
         	    else
         	       {
         	       	task = (Element) taskList.get(i);
         	       	a 	 = task.getAttribute("operation");
         	       	if (a==null)
         	       	   nE = nE + "_" + task.getName();
         	       	else
         	       	   nE = nE + "_" + task.getName() +  a.getValue();
         	       }
         	    eNext.setAttribute("id",nE);
        	    eFlow.addContent(eNext); 
         	    Element eJoin=new Element("join");
         	    eJoin.setNamespace(n0);
         	    eJoin.setAttribute("code","xor");
         	    Element eSplit = new Element("split");
         	    eSplit.setNamespace(n0);
         	    eSplit.setAttribute("code","and");
         	    Element eDecomposesTo = new Element("decomposesTo");
         	    eDecomposesTo.setNamespace(n0);
         	    eDecomposesTo.setAttribute("id",currentTask.substring(currentTask.indexOf("_")+1));         	    
         	    eCurr.addContent(eFlow);
         	    eCurr.addContent(eJoin);
         	    eCurr.addContent(eSplit);
        	    eCurr.addContent(eDecomposesTo);
        	    
        	    e.addContent(eCurr);
         	   }
         	// last one         	         	        
         	Element eOut = new Element("outputCondition");
         	eOut.setNamespace(n0);
         	eOut.setAttribute("id","1_OutputCondition");
         	e.addContent(eOut);
         	
         	
         	return e;
         }
      
      public List getTaskDecomposition(List taskList,Namespace n)
         {
          Namespace n0=n;
          List result = new ArrayList();
          String tName="";
          for (int i=0;i<taskList.size();i++)
             {
              Element e = new Element("decomposition");
              e.setNamespace(n0);
              Element task = (Element) taskList.get(i);
   	       	  Attribute a 	 = task.getAttribute("operation");
   	       	  if (a==null)
   	       	     tName = task.getName();
   	       	  else
   	       	     tName = task.getName() +  a.getValue();
              e.setAttribute("id",tName);
              Namespace ns = Namespace.getNamespace("xsi","http://www.w3.org/2001/XMLSchema-instance");
              e.setAttribute("type","WebServiceGatewayFactsType",ns);                            
              result.add(e);
             }
          return result;
         }

   }
