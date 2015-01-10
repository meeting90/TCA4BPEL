/**
 * Visitor.java
 * Purpose:	Interface specifying the methods for the Visitor Pattern. 
 * @author Stephan Breutel
 * @version 1.0
 * @created 4/03/2005
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
import model.frontEnd.*;
import java.util.Collection;
import org.jdom.Element;

//TODO interface-> abstract Class

public interface Visitor
   {
   	/**
   	 * MethodName	: dispatchVisit
   	 * Purpose    	: Dispatches to a type dependent visit method XOR 
   	 * 				  calls a default visit behaviour	
   	 * Pre		  	: o is a datastructure to be visited
   	 * Post		  	: traversal through the datastructure o and action 
   	 * 				  taken according to the concrete implementation of the
   	 * 				  visitor methods.
   	 * 
   	 * @param o
   	 */
   	public void dispatchVisit(Object o);
   
   	/**
   	 * MethodName	: visit
   	 * Purpose    	: visit method for the XML-Tree 
   	 * Pre		  	: x is a correct XML-Tree
   	 * Post		  	: all elements of x have been visited and actions
   	 * 				  according to the visit implementation have been
   	 * 				  performed. The traversal (navigation) through x 
   	 * 				  is (usually) specified by the implementation of
   	 * 				  the Visitable interface. 
   	 * 
   	 * @param x, XMLTree 
   	 */
   	public void visit(XMLTree x);
   	
	/**
   	 * MethodName	: visit
   	 * Purpose    	: visit method for the SymbolTable
   	 * Pre		  	: s is a correct SymbolTable
   	 * Post		  	: all elements of s have been visited and actions
   	 * 				  according to the visit implementation have been
   	 * 				  performed. The traversal (navigation) through s 
   	 * 				  is (usually) specified by the implementation of
   	 * 				  the Visitable interface. 
   	 * 
   	 * @param s, 	SymbolTable 
   	 */
   	public void visit(SymbolTable s);
   	
   	/**
   	 * MethodName	: visit
   	 * Purpose    	: visit method for a Collection of objects
   	 * Pre		  	: c is a correct Collection
   	 * Post		  	: all elements of c have been visited and actions
   	 * 				  according to the visit implementation have been
   	 * 				  performed. The traversal (navigation) through s 
   	 * 				  is (usually) specified by the implementation of
   	 * 				  the Visitable interface. 
   	 * 
   	 * @param c,	Collection 	
   	 */
   	public void visit(Collection c);   	 
   	
   	/**
   	 * MethodName	: visit
   	 * Purpose    	: Visit a key, item pair.
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @param key
   	 * @param o
   	 */
   	public void visit(String key,Object item);
   	
   	/**
   	 * MethodName	: visit
   	 * Purpose    	: 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @param e		, Element an XML-Element according to the JDOM Lib.
   	 * @param breadth
   	 * @param depth	, int	the depth in the Element Tree
   	 */
   	public void visit(Element e,int breadth, int depth);
   	/**
   	 * MethodName	: visit
   	 * Purpose    	: default visit method
   	 * Pre		  	: TRUE
   	 * Post		  	: all elements of o have been visited and actions
   	 * 				  according to the visit implementation have been
   	 * 				  performed. The traversal (navigation) through o
   	 * 				  is (usually) specified by the implementation of
   	 * 				  the Visitable interface.
   	 * 
   	 * @param o
   	 */
   	public void visit(Object o);
   }
