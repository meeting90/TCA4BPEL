/**
 * Tester.java
 * Purpose:		Testing class to test different parts of the Babel program.		 
 * @author 		Stephan Breutel
 * @version 	1.0
 * @created 	4/04/2005
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

public class Tester
   {

      /**
       * MethodName   : Tester
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a Tester object has been created.
       * 
       */
      public Tester()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
      public void testSymbolTable(SymbolTable sT)
         {
         	System.out.println(" Content of : " + sT.getName());
         	System.out.println(sT.toString());
         }
      
    

   }
