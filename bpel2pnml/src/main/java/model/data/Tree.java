/*
 * Created on 27/02/2005
 * Purpose: 
 * 
 */
package model.data;

import model.frontEnd.Node;


/**
 * Tree.java
 * Purpose: Interface defining the functionality of Tree's within the 
 * 			BABEL project.
 * @author Stephan Breutel
 * @version 1.0
 * @created 27/02/2005
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
public interface Tree
   {
   	public void insert(Node n);
   	
   	public void delete(Node n);
   		
   	public Object root();
   	
   	public Tree[] getSubTrees();
   	
   	public void deleteTree(Node n); 
   		
   	public Node visit();
   	
   	public void traverse();
   	
   	public String toString();
   	
   	
   		
   }
