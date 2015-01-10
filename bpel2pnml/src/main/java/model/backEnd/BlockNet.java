/**
 * BlockNet.java
 * Purpose: This class is responsible to build a petri-net for a given boolean expression.
 *   	
 * @author Stephan Breutel
 * @version 1.0
 * @created 21/04/2005
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

package model.backEnd;

import java.util.*;

public class BlockNet
   {
   	String 		boolExpr;	/* boolean expression */
   	String[] 	varName;
   	HashMap		hMVar;
   	HashMap		hMLink;
   	boolean [][] truthTable;

      /**
       * MethodName   : BlockNet
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a BlockNet object has been created.
       * 
       */
      public BlockNet(String bpelBoolExpr)
         {
          BoolEvaluator bE = new BoolEvaluator(); 	
      	  BoolEvaluatorPreProcessor bEPP = new BoolEvaluatorPreProcessor("l"); // l is the name given for a variable.
      	  String res 	= bEPP.bpelExpr2BoolEvalExpr(bpelBoolExpr);
      	  hMVar 		= bEPP.getHashMapVar();
      	  hMLink		= bEPP.getHashMapLink();
      	  truthTable 	= bE.computeTruthTable(res);      	  
         }
      
      public boolean[][] getTruthTable()
         {
          return this.truthTable;
         }
      
      public HashMap getHashMapVar()
         {
          return hMVar;
         }
      
      public HashMap getHashMapLink()
         {
          return hMLink;
         }               	
   }
