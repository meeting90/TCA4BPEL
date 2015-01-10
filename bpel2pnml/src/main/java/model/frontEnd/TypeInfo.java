/**
 * TypeInfo.java
 * Purpose: This class encapsulates the information about DataTypeStructures.
 * 			It uses a two dimensional ListArray to store information about the
 * 			Name of an attribute and a "parallel" List containing the type of 
 * 			the corresponding attribute.  
 * @author Stephan Breutel
 * @version 1.0
 * @created 1/03/2005
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

import java.util.List;
import java.util.ArrayList;

public class TypeInfo
   {
   	List[] info;

      /**
       * MethodName   : TypeInfo
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a TypeInfo object has been created.
       * 
       */
      public TypeInfo()
         {
            super();
            info 	= new ArrayList[2];
            info[0]	= new ArrayList();
            info[1] = new ArrayList();            
         }
      
      /**
       * MethodName   : TypeInfo
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a TypeInfo object has been created.
       * @param attrList	List, containing the names of the Attributes
       * @param typeList	List, containing the names of the corresponding Types
       */
      public TypeInfo(List attrList,List typeList)
         {
         	super();
         	info 	= new ArrayList[2];
         	info[0]	= attrList;
         	info[1]	= typeList;
         }
      /**
       * MethodName		: toXMLString
       * Purpose    	: convert the TypeInfo into an XMLString
       * Pre		  	: info[0].size()==info[1].size() AND info[0] contains the attribute names
       * 				  AND info[1] contains the types. 
       * Post		  	: (a XML-String is returned containing the information of this TypeNode
       * 				  the XML-Structure follows the http://www.w3.org/2001/XMLSchema NameSpace)	XOR
       * 				  an empty String "" is returned indicating an empty TypeNode.
       * 
       * @return	XML-String following the http://www.w3.org/2001/XMLSchema NameSpace XOR "" 
       */
      public String toXMLString()
         {
         	String xmlString="";
         	if (!info[0].isEmpty())         	          	            
         	   {
         	   	// info[0].isEmpty() should usually not be the case. 
         	   	// TODO Design : Define a preCondition?  
         	   	// for now we just consider names and types
         	   	// TODO consider further attributes e.g. maxOccurs         	   
         	   	for (int i=0;i<info[0].size();i++)
         	      xmlString = xmlString + "<element name=\"" + (String) info[0].get(i) + "type=\"" 
         	      			 + (String) info[1].get(i) + "\"/>\n";         	               	     
         	   }         	
         	return xmlString;
         }     
      
   }
