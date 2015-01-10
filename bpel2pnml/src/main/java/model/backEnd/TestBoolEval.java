/*
 * Created on 23/04/2005
 * Purpose: 
 * 
 */
package model.backEnd;
import java.util.*;

/**
 * TestBoolEval.java
 * Purpose: 
 * @author Stephan Breutel
 * @version 1.0
 * @created 23/04/2005
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
public class TestBoolEval
   {

      /**
       * MethodName : TestBoolEval
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a TestBoolEval object has been created.
       * 
       */
      public TestBoolEval()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
      public static void printTT(boolean [][] m)
      	{ 
         int k = m.length;
   	   	 int l = m[0].length;
   	   	 
   	   	 System.out.println(" Truth Table ");
         for (int i=0;i<k;i++)
            {
             for(int j=0;j<l;j++)
                System.out.print(" " + m[i][j]);
             System.out.print("\n");
            }         
         System.out.println("=======================");
      	}
      
      public static void printHM(HashMap hM)
         {
         
         }

      public static void test(String input)
      	{
         String res;
         BoolEvaluator bE = new BoolEvaluator(); 	
      	 BoolEvaluatorPreProcessor bEPP = new BoolEvaluatorPreProcessor("l");      	
      	 System.out.println("----------------------");
      	 System.out.println("Preprocessing.");      	 
      	 System.out.println("Input: " + input);
      	 res = bEPP.bpelExpr2BoolEvalExpr(input);
      	 System.out.println("Output: " + res);
      	 TestBoolEval.printTT(bE.computeTruthTable(res));
      	 System.out.println("----------------------");  
      	 System.out.println(bEPP.getHashMapLink().toString());
      	 System.out.println(bEPP.getHashMapVar().toString());
      	}
      
      public static void main(String[] args)
         {
         
         	TestBoolEval.test("bpws:getLinkStatus('buyToSettle')");
         	
         	TestBoolEval.test( "not  bpws:getLinkStatus(buyToSettle)");
         	TestBoolEval.test("bpws:getLinkStatus('buyToSettle') and bpws:getLinkStatus('123')");
         	TestBoolEval.test("bpws:getLinkStatus('buyToSettle') or       bpws:getLinkStatus('sellToSettle')");
         	TestBoolEval.test("(bpws:getLinkStatus('buyToSettle') or   bpws:getLinkStatus('sellToSettle'))");
         	TestBoolEval.test("bpws:getLinkStatus('buyToSettle') and " +
         								"(bpws:getLinkStatus('buyToSettle') or   bpws:getLinkStatus('sellToSettle'))");
         	
         	
         	//bEPP.bpelExpr2BoolEvalExpr( "not bpws:getLinkStatus(buyToSettle) and bpws:getLinkStatus(buyToSettle)");
         	
         	/*TestBoolEval.printTT(bE.computeTruthTable("a"));
         	TestBoolEval.printTT(bE.computeTruthTable("a || b"));
         	TestBoolEval.printTT(bE.computeTruthTable("a && b"));
         	TestBoolEval.printTT(bE.computeTruthTable("! a"));
         	
         	TestBoolEval.printTT(bE.computeTruthTable("! a && ( b && c )"));
         	TestBoolEval.printTT(bE.computeTruthTable("a && ( b || c ) && ( d || e ) && f"));
         	*/
         	
         	
         }
   }
