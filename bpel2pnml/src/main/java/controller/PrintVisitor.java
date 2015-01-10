/**
 * PrintVisitor.java
 * Purpose: Visits all elements of data structure and prints the contents.
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
package controller;

import model.data.XMLTree;
import model.frontEnd.SymbolTable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Iterator;
import org.jdom.Element;

public class PrintVisitor implements Visitor
   {
   	String oString;
   	int depth;
      /**
       * MethodName 	: PrintVisitor
       * Purpose	  	: Constructor
       * Pre		  	: TRUE
       * Post	      	: a PrintVisitor object has been created.
       * 
       */
      public PrintVisitor()
         {
            super();
            init();
            // TODO Auto-generated constructor stub
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
            oString = " ---THE PRINTVISITOR ---\n";
            oString = oString + "Content of the XML-Tree\n";
            oString = oString + "===========================\n";
            x.accept(this);
            oString = oString + "============================\n";                     	
         }
      
      public void visit(Element e,int breadth,int treeDepth)
         {
         	String spaces ="";
         	
         	for (int i=0;i<treeDepth;i++)
         	   spaces = spaces + "   ";
         	oString = oString + spaces + e.getName() + "    [" + treeDepth + "," + breadth + "]" + "\n";         
         }
      

      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.SymbolTable)      
       */
      public void visit(SymbolTable s)
         {
          oString =  " ---THE PRINTVISITOR ---\n";
          oString = oString +  "Content of the SymbolTable	\n";
       	  oString = oString + "===========================	\n";
       	  s.accept(this);
       	  oString = oString + "===========================\n";
         }
      
      public void visit(String key, Object value)
         {
            if (value == null)               
                oString = oString + "(" + key + ", NULL)" ;
            else
               {
                String type	= value.toString();         	
                oString	= oString + "(" + key + ",";
                dispatchVisit(value);
                oString = oString + ")\n";
               }
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
