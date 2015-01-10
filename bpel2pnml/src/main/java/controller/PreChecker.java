/**
 * PreChecker.java
 * Purpose:  This class is used to check the source BEFORE the start of the translation process.
 * @author Stephan Breutel
 * @version 1.0
 * @created Jun 16, 2005
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

import model.frontEnd.*;
import model.data.*;
import java.util.*;


public class PreChecker
   {
      FindVisitor fV;
      XMLTree	  tree;
      /**
       * MethodName   : PreChecker
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a PreChecker object has been created.
       * 
       * @param		t, XMLTree the tree to be searched on.
       */
      public PreChecker(XMLTree t)
         {            
            fV 		= new FindVisitor();
            tree 	= t;
         }
      
      /**
       * 
       * MethodName		: hasProcessExit
       * Purpose    	: Checks if a source file contains an exit/terminate command.
       * Pre		  	: tree is a valid XML-Tree.
       * 					
       * 				  commandNames is a StringArray containing the keywords of a language.
       * 				  It is a StringArray because this allows to handle
       * 				  also different versions of a language, e.g. BPEL: [exit|terminate]
       * Post		  	: true is returned, iff an exit-command has been found. 
       * 				  false, otherwise.
       * 
       * @param exitNames
       * @return
       */
      public boolean hasCommand(String[] commandNames)
         {                              
          for (int i=0;i<commandNames.length;i++)
             {
              fV.init();               
              fV.setSearchString(commandNames[i]);
              List idList = new ArrayList();     
              idList.add("C");
              fV.setIdString(idList);
              fV.visit(tree);
              if ( fV.getNameList()!=null && fV.getNameList().size()>0 )
                 return true;
             }
          return false;
         }                        
   }
