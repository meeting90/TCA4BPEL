/**
 * SymbolTable.java
 * Purpose: A (key,value) pair table. For the application in the BABEL Project
 * 			two SymbolTables are used. 	
 * 			(1) (nameofVariable,nameofType)
 * 			(2) (nameofType,(nameofAttribute,[nameofType|String])*)
 * 			nameofType is used for struct-like types, 
 * 			String is used to indicate a baseType (e.g. "xsd:string","xsd:integer").
 * 			This implementation of the SymbolTable uses the java-HashMap
 * 		
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

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Iterator;
import controller.Visitable;
import controller.Visitor;
import controller.PrintVisitor;


public class SymbolTable implements Visitable
   {
   	HashMap hM;		/* Underlying data-structure used to store the (k,v) pairs. */ 
   	String name; 	/* Name of the SymbolTable: [Variable|Type|Task]			*/
   		
      /**
       * MethodName   : SymbolTable
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a SymbolTable object is created.
       * 
       */
      public SymbolTable()
         {
            super();
            hM = new HashMap();	          
         }
      
      /**
       * MethodName		: insert 
       * Purpose    	: insert an entry into the symbolTable
       * Pre		  	: info Object provides a toString() method
       * Post		  	: Key-Value pair (key,type) has been inserted into the SymbolTable.
       * 
       * @param key
       * @param info
       */
      public void insert(String key, Object info)	
         {
         	hM.put(key,info);
         }
           
      /**
       * MethodName		: getInfo
       * Purpose    	: returns the info object 
       * Pre		  	: TRUE
       * Post		  	: info Object is returned if key exists XOR 
       * 				  null if key does not exist
       * @param key
       * @return
       */
      public Object getInfo(String key)
         {
         	return hM.get(key);
         }
      
      public void setName(String s)
         {
          name=s;
         }
      
      public String getName()
         {
         	return name;
         }
      public boolean isEmpty()
         {
         	return hM.isEmpty();
         }
      
      public void updateValue(String key,Object value)
         {
          Object vOLD = hM.get(key);
          hM.put(key,value);
         }
      
      public void updateAllValues(SymbolTable typeTable)
         {
          // TODO remove this HACK 
          Set 	  s		=	hM.keySet();
          Iterator iter	=	s.iterator();
      	  while (iter.hasNext())
      	    {         	   	
      	   		String key 		= (String) iter.next();         	   
      	   		String value 	= (String) hM.get(key);
      	   		// lookup value in the typeTable and  replace if primitive Type.
      	   		String sHack = value.substring(4);        	
      	   		List [] typeInfo = (List []) typeTable.getInfo(sHack);
      	   		if (typeInfo[0].size()==1)
      	   		   // replace
      	   		   {
      	   		    System.out.println("Replace: " + sHack);
      	   		    String pType = (String) typeInfo[1].get(0);
      	   		    //pType = pType.substring(4);
      	   		    System.out.println(pType);
      	   		    updateValue(key,pType);
      	   		   }
      	   		   
      	    }         	
         }
      /**
       * MethodName		: getInfoXMLString
       * Purpose    	: get the information as XML-String.
       * Pre		  	: TRUE
       * Post		  	: information of the corresponding key is returned via an XML-String XOR
       * 				  null is returned to indicate that there is no such key in the SymbolTable 
       * 
       * @param key
       * @return
       */
      public String getInfoXMLString(String key)
         {
         	Object info = this.getInfo(key);
         	String xmlString;
         		
         	if (info.getClass().getName().compareTo("TypeInfo")==0)
         	   {
         	   // complex Type.
         	   xmlString = "<schema xmlns=\"http://www.w3.org/2001/XMLSchema\">\n";  // NameSpace
         	   xmlString = xmlString + "<complexType name=\"" + key + "\"/>\n";
         	   xmlString = xmlString + "<sequence>\n";
         	   xmlString = xmlString + ((TypeInfo) info).toXMLString();
         	   xmlString = xmlString + "</sequence>\n";
         	   xmlString = xmlString + "</complexType>";         	   
         	   }
         	else
         	   {
         	   // base Type: just String 
         	   xmlString = "<name>" + key + "</name>\n";
               xmlString = xmlString + "<type>" + (String)info + "</type>\n";
               xmlString = xmlString + "<namespace>http://www.w3.org/2001/XMLSchema</namespace>";
         	   }         	   
         	return xmlString;
         }
      
      /* (non-Javadoc)
       * @see java.lang.Object#toString()
       * Convert the content of the SymbolTable to a String.       
       */
      public String toString()      
         {
         	//TODO use PrintVisitor to walk over this dataStructure?
         	PrintVisitor pv = new PrintVisitor();
         	pv.visit(this);         	
         	return pv.getOutputString();         	
         }
            
      /* (non-Javadoc)
       * @see controller.Visitable#accept(controller.Visitor)
       * Benefit : the navigation is now generalised. The corresponding actions are defined by the visit 
       * method.
       */
      public void accept(Visitor v)
         {
         	Set 	  s		=	hM.keySet();
         	Iterator iter	=	s.iterator();
         	while (iter.hasNext())
         	   {         	   	
         	   	String key 		= (String) iter.next();         	   
         	   	Object value 	= hM.get(key);
         	   	v.visit(key,value);         	   	         	          	             	             	    
         	   }         	
         }
   }
