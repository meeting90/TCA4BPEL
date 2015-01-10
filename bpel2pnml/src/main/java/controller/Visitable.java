/**
 * Visitable.java
 * Purpose:	Interface specifying methods for Visitable. 
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

import controller.Visitor;

public interface Visitable
   {

   	/**
   	 * MethodName	: accept
   	 * Purpose    	: Implementations of the accept method should define the
   	 * 				  traversal through the implementing datastructure.
   	 * Pre		  	: TRUE
   	 * Post		  	: traversal through the datastructure occured.
   	 * 				  (not necessarily the comlete datastructure).
   	 * 
   	 * @param v
   	 */
   	public void accept(Visitor v);
   	
   }
