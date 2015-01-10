/**
 * FindVisitor.java
 * Purpose:		To find all elements with a given Name in an XML-Tree.
 * 				Current implementation requires that max. one attribute/child is used if any.
 * 				i.e.: idString.size() <=1
 * 
 * Example of Usuage:
 * 
 *      FindVisitor findVisitor = new FindVisitor();
 *      findVisitor.setSearchString(task);
 *      List idList = new ArrayList();     
 *      idList.add("A"+taskId);
 *      findVisitor.setIdString(idList);
 *      findVisitor.visit(xmlTree); 
 *      List res = findVisitor.getNameList();
 * 
 * @author Stephan Breutel
 * @version 1.0
 * @created 17/04/2005
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
package model.frontEnd;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.jdom.Element;
import org.jdom.Attribute;

import controller.Visitor;
import controller.Visitable;
import model.data.XMLTree;


public class FindVisitor implements Visitor
   {         
   	String oString;
   	String searchString; 	/* Name of the Element */
   	List idString;		    /* List of Ids         */
   	List elementList;		/* List of Elements */
   	List nameList;			/* List of Strings  */
   	int level;				/* search until a given level. */
   	
      /**
       * MethodName 	: FindVisitor
       * Purpose	  	: Constructor
       * Pre		  	: TRUE
       * Post	      	: a PrintVisitor object has been created.
       * 
       */
      public FindVisitor()
         {
            super();
            init();          
            this.level = Integer.MAX_VALUE; // const. max an int can represent. in Java: (2^31)-1
         }
      
      public FindVisitor(int level)
         {
          this.level = level;
          init();
         }
      
      public void setSearchString(String s)
         {
          searchString = s;
         }
      
      public void setIdString(List s)
         {
          idString = s;
         }

      public List getElementList()
         {
          return elementList;
         }
      
      public List getNameList()
         {
          return nameList;
         }
      
      /**
       * MethodName	: getOutputString
       * Purpose    	: 
       * Pre		  	:
       * Post		  	: 
       * 
       * @return
       */
      public String getOutputString()
         {
         	return oString;
         }
      
      public void visit(SymbolTable sT)
         {
          ;
         }
      
      /**
       * MethodName		: init
       * Purpose    	:	 
       * Pre		  	:
       * Post		  	: 
       * 
       * 
       */
      public void init()
         {
         	oString = "";
         	this.elementList = null;
         	this.nameList    = null;
         	this.idString	 = null;
         }
      /* (non-Javadoc)
       * @see controller.Visitor#dispatchVisit(java.lang.Object)
       */
      public void dispatchVisit(Object o)
         {                  
            try {
              Method method = getMethod(o.getClass());
              method.invoke(this, new Object[] {o});
            } catch (Exception e) { }
         }

      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.XMLTree)
       */
      public void visit(XMLTree x)
         {
            oString = " ---THE  FINDVISITOR ---\n";
            elementList = new ArrayList();
            nameList 	= new ArrayList();
            x.accept(this);                                 
         }
      
 
      public void visit(Element e,int breadth,int treeDepth)
         {
         	if (e.getName().compareTo(searchString)==0 && treeDepth <= level)
         	   {
         	   	elementList.add(e);
         	   	for (int i=0;idString!=null && i<idString.size();i++)
         	   	   {
         	   	    // idString is either attribute or child. A:attrName C:childName
         	   	    String id = (String) idString.get(i);
         	   	    if (id.charAt(0)=='A')
         	   	       {
         	   	        Attribute a = e.getAttribute(id.substring(1));
         	   	        if (a!=null)
         	   	           nameList.add(a.getValue());
         	   	        else
         	   	           nameList.add("null");
         	   	        //System.out.println(id.substring(1));
         	   	       }
         	   	    else
         	   	       {         	   	        
         	   	        nameList.add(e.getText());
         	   	       }         	   	    
         	   	   }
         	   }         	
         }
     
      /**
       * 
       * MethodName	: postProcessResult
       * Purpose    	: post-process name and element- list
       * 				  hMAttrValues is a HashMap with (nameOfAttribute,Value)  
       * Pre		  	: A search has been performed using an attribute.
       * 				  idString!=null
       * Post		  	: 
       * 
       *
       */
      public void postProcessResult(HashMap hMAttrValues)
         {
          List elements = new ArrayList();
          List names	= new ArrayList();
          
          for(int i=0;i<this.idString.size();i++)
             {
              // add all the elements and names where the values agree.              
              String attrName 	= ((String) idString.get(i)).substring(1);
              String attrValue 	= (String)  hMAttrValues.get(attrName);
              for(int j=0;j<elementList.size();j++)
                 {
                  String attrValue_ = (String) this.nameList.get(j);
                  if (attrValue.compareTo(attrValue_)==0)
                     {
                      elements.add((Element) elementList.get(j));
                      names.add((String) attrValue_);
                     }                  
                 }
             }
          
          this.elementList 	= elements;
          this.nameList		= names;          
         }
      
      /**
       * 
       * MethodName	: postProcessResult
       * Purpose    	: reduce the results to those which agree with the attrValue 
       * Pre		  	: only applicable for exactly one attribute in the moment.
       * Post		  	: 
       * 
       * @param attrValue
       */
      public void postProcessResult(String attrValue)
         {
          List elements = new ArrayList();
          List names	= new ArrayList();
         
          for(int i=0;i<this.idString.size();i++)
            {            
             for(int j=0;j<elementList.size();j++)
                {
                 String attrValue_ = (String) this.nameList.get(j);
                 if (attrValue.compareTo(attrValue_)==0)
                    {
                     elements.add((Element) elementList.get(j));
                     names.add((String) attrValue_);
                    }                  
                }
            }
         
          this.elementList 	= elements;
          this.nameList		= names;        
         }
      
      public void visit(String key, Object value)
         {
         	String type	= value.toString();
         	
         	oString	= oString + "(" + key + ",";
         	dispatchVisit(value);
         	oString = oString + ")\n";   
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.util.Collection)
       */
      public void visit(Collection c)
         {
         	Iterator iterator = c.iterator();
         	while (iterator.hasNext()) 
         	   {
         	   	Object o = iterator.next();
         	   	if (o instanceof Visitable)
         	   	   ((Visitable)o).accept(this);
         	   }
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.lang.Object)
       */
      public void visit(Object o)
         {
          System.out.println("Call of the Default visit.");
          oString = oString + o.toString();
         }
      
      /**
       * MethodName		: visit
       * Purpose    	: visit a ListArray. 
       * Pre		  	: TRUE
       * Post		  	: 
       * 
       * @param l
       */
      public void visit(List [] l)
         {
         	for(int i=0;i<l.length;i++)
         	   {
         	   	oString = oString + "{";
         	   	visit(l[i]);
         	   	oString = oString + "}";
         	   }
         }
      
      /**
       * MethodName		: visit
       * Purpose    	: visit a list 
       * Pre		  	: TRUE
       * Post		  	: the list is visited. 
       * 
       * @param l
       */
      public void visit(List l)
         {
         	//TODO visitable interface for traversal ?         	
         	for (int i=0;i<l.size()-1;i++)
         	   {
         	   System.out.println(l.get(i));
         	   	oString = oString + l.get(i).toString() + ",";
         	   }
         	oString = oString + l.get(l.size()-1).toString();
         }
      
      protected Method getMethod(Class c) {
         Class newc = c;
         Method m = null;
         // Try the superclasses
         while (m == null && newc != Object.class) 
            {
            String method = newc.getName();
            method = "visit"; // + method.substring(method.lastIndexOf('.') + 1);
            try {
               	m = getClass().getMethod(method, new Class[] {newc});
            	} catch (NoSuchMethodException e) 
            	{
            	newc = newc.getSuperclass();
            	}
            }
         // Try the interfaces.  If necessary, you
         // can sort them first to define 'visitable' interface wins
         // in case an object implements more than one.
         if (newc == Object.class) {
            Class[] interfaces = c.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
               String method = interfaces[i].getName();
               method = "visit";// + method.substring(method.lastIndexOf('.') + 1);
               try {
                  m = getClass().getMethod(method, new Class[] {interfaces[i]});
               } catch (NoSuchMethodException e) {}
            }
         }
         if (m == null) {
            try {
                 m = getClass().getMethod("visit", new Class[] {Object.class});
            } catch (Exception e) {                
            }
         }
         return m;
      }

   }
