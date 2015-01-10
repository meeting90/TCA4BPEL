/**
 * TreeBuilderVisitor.java
 * Purpose: Build the (intermediate?) YAWL Tree while traversing the XML-Tree.
 * @author Stephan Breutel
 * @version 1.0
 * @created 7/03/2005
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
import java.util.List;
import java.util.ArrayList;

import org.jdom.Element;
import org.jdom.Namespace;

import controller.Visitor;

import model.data.XMLTree;
import model.data.YAWLTree;



public class TreeBuilderVisitor implements Visitor
   {
   	Element 	complexType;		/* will  be filled by applying the visitor method to the SymbolTable 	*/
   	List[] 	netIOVariables;
   	Namespace n0;
   	SymbolTable typeTable;			/* to resolve the type information of a variable 						*/
   	
   	YAWLTree yawlTree;
      /**
       * MethodName 	: TreeBuilderVisitor
       * Purpose	  	: Constructor
       * Pre		  	: TRUE
       * Post	      	: a TreeBuilderVisitor object has been created.
       * 
       */
      public TreeBuilderVisitor()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
      public void setComplexType(Element e)
         {
          complexType = e;
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
         	// TODO build the Result Tree (either YAWLTree for 2YAWL or <language>Tree for YAWL2). 
         	// for now: just YAWLTree.
         	
         }
      
      public void visit(String s)
         {
          //TODO remove Hack.
          // assumption this is called during the processing of the idTable.
          if (typeTable==null)             
             {
             	
             }
          else
             {
             	// Build data definition
             	String sHack = s.substring(4);
             	System.out.println("sHack: " + sHack);
             	List [] typeInfo = (List []) typeTable.getInfo(sHack);
             	if (typeInfo != null && typeInfo[0].size()>1)
             	   {
             	   	// time to construct a complex Type.
             	    // assumption: just one.
             	    System.out.println("Build complexType."); 
             	    complexType = new Element("complexType");
             		Namespace n0	= Namespace.getNamespace("http://www.w3.org/2001/XMLSchema");             		
             	    complexType.setAttribute("name",sHack);             	   
             	    complexType.setNamespace(n0);
             	    Element e2 = new Element("sequence");
             	    e2.setNamespace(n0);
             	    for (int i=0;i<typeInfo[0].size();i++)
             	       {
             	       	Element e3 = new Element("element");             	       	
             	       	e3.setAttribute("name",(String) typeInfo[0].get(i));
             	       	String type = (String) typeInfo[1].get(i);
             	       	// remove leading ":" stuff.
             	       	type=type.substring(type.indexOf(":")+1);             	       	
             	       	e3.setAttribute("type",type);
             	       	e3.setNamespace(n0);
             	       	e2.addContent(e3);
             	       }             	       
             	    complexType.addContent(e2);
             	   }
             }	
         }
      
      /**
       * MethodName		: buildDataTypes
       * Purpose    	: build DataTypeDef element by processing the idTable and typeTable
       * Pre		  	:
       * Post		  	: 
       * 
       * @param idTable
       * @param typeTable
       * @return
       */
      public Element buildDataTypes(SymbolTable idTable,SymbolTable typeTable)
         {
         	// TODO remove Hack
            complexType=null;
         	this.typeTable=typeTable;
         	visit(idTable);
         	return complexType;
         }
      
      public List[] buildNetIO(SymbolTable idTable,Namespace n0)
         {	        
  	   	  if (idTable.isEmpty())
  	   	     return null;
  	   	  else
  	   	     {
  	   	      this.n0			= n0;
  	   	      netIOVariables 	= new List[2];
  	   	      netIOVariables[0] = new ArrayList();
  	   	      netIOVariables[1] = new ArrayList();
  	   	      //netIOVariables[0] = new Element("inputParam");
  	   	      //netIOVariables[1] = new Element("outputParam");
  	   	      visit(idTable);
  	   	      return netIOVariables;  	   	      
  	   	     }
         }
      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.SymbolTable)
       */
      public void visit(SymbolTable s)
         {         	
         	s.accept(this);         	
         }

      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.util.Collection)
       */
      public void visit(Collection c)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.lang.String, java.lang.Object)
       */
      public void visit(String key, Object value)
         {                 
         	if (typeTable!=null)
         	   // build complexType
         	   dispatchVisit(value);
         	else if (netIOVariables!=null)
         	   // build Net IO Variables.
         	   {
         	   	for (int i=0;i<2;i++)
         	   	   {
         	   	   	String s="Param";
         	   	   	if (i==0)
         	   	   	   s = "input" + s;
         	   	   	else
         	   	   	   s = "output" + s;
         	   	   	Element e 			= new Element(s);
         	   	   	Element name 		= new Element("name");
         	   	   	Element type 		= new Element("type");
         	   	   	Element namespace 	= new Element("namespace");
         	   	    name.addContent("net"+key);
         	   	    name.setNamespace(n0);
         	   	    String s2 = value.toString();
         	   	    s2 = s2.substring(4);
         	   	    type.addContent(s2);
         	   	    type.setNamespace(n0);
         	   	    namespace.addContent("http://www.w3.org/2001/XMLSchema");
         	   	    namespace.setNamespace(n0);
         	   	    e.addContent(name);
         	   	    e.addContent(type);
         	   	    e.addContent(namespace);
         	   	    e.setNamespace(n0);
         	   	   	netIOVariables[i].add(e);
         	   	   }         	   	
         	   }         	   
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
         	   	visit(l[i]);         	   
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
         	  
         	   }
         	
         }
      
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(org.jdom.Element, int)
       */
      public void visit(Element e,int breadth, int depth)
         {
         
         }
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.lang.Object)
       */
      public void visit(Object o)
         {
            // TODO Auto-generated method stub
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
