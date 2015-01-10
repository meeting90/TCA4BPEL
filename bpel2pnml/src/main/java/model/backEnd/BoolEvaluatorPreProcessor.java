/**
 * BoolEvaluatorPreProcessor.java
 * Purpose:	This class is used to transform boolean expression into equivalent
 * 			boolean expressions following the assumptions of the BoolEvalutor
 * 			class. 
 * @author Stephan Breutel
 * @version 1.0
 * @created 26/04/2005
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

import java.util.HashMap;
import java.util.regex.*;

public class BoolEvaluatorPreProcessor
   {   	
   	HashMap hMVar;				/* HashMap containing the new varName as key and the original (substituted) expr 
   							   	   as value. */
   	HashMap hMLink;				/* mapping: name of a variable, true linkName */
   	String regExBPELLinkExpr;	/* complete regular expression defining the syntax structure of a BPEL boolExpr*/      
   	String notOperator;	 
   	String andOperator;
   	String orOperator;
   	String varName;
   	String linkName;
   	String openBrackets;
   	String closingBrackets;
   	String varNameReplace;
   	

      /**
       * MethodName   : BoolEvaluatorPreProcessor
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a BoolEvaluatorPreProcessor object has been created.
       * 
       */
      public BoolEvaluatorPreProcessor(String varNameReplace)
         {
            super();
           
            // build the regular expression for a BPEL link expression.
            this.regExBPELLinkExpr = regExBPEL();       
            this.notOperator="\\p{Space}*not\\p{Space}*";
            this.andOperator="\\p{Space}*and\\p{Space}*";
            this.orOperator ="\\p{Space}*or\\p{Space}*";
            // [^'] ... any character except '.
            // anyway does not work for: assess-to-setMessage
            this.varName	="\\p{Space}*bpws:\\w*\\((\\'|\")[^']*(\\'|\")\\)\\p{Space}*";
            this.linkName	="\\(\\'[^']*\\'\\)";
            this.varNameReplace = varNameReplace;
         }
      
      private String regExBPEL()
         {
          String res="";
          String varName 		= this.varName; // old "\\p{Space}*bpws:\\w*\\(\\'\\w*\\'\\)\\p{Space}*";
          String operator		= "\\p{Space}*(and|or)\\p{Space}*";
          String notOp			= "\\p{Space}*not\\p{Space}*";
          String basicBoolExpr 	= "(" + varName + "|" + varName + operator + varName + "|" + notOp + varName + ")";
          String boolExpr		= "(\\(" + basicBoolExpr + "\\)" + "|" + basicBoolExpr +")";
          boolExpr				= boolExpr + "(" + operator + boolExpr + ")*";                    				
          res			    	= boolExpr;
                                                          
          return res;
         }
    
      /**
       * 
       * MethodName		: bpelExpr2BoolEvalExpr
       * Purpose    	: Converts a String representing the BPEL link condition into 
       * 				  an equivalent logical expression following the BoolEvalutor 
       * 				  syntax.
       * 				  
       * Pre		  	: bpelExpr is a correct BPEL link condition following XPath 1.0 syntax.
       * 				  BPEL link conditions are simpliefied and are composed only of:
       * 				  (-> Marlon Email: 24.04.2005) 
       * 				  - calls to the bpws:joinStatus(L) function (and the parameter
       *				    must be one of the links pointing to the activity in question) 
       *			      - Boolean operators: and, or, and the not(...).
       *			      - And of course the brackets ( and )
       *
       * Post		  	: this.boolExpr_BE contains the logical for bpelExpr following
       * 				  the BoolEvaluator syntax. 
       * 				  this.hMVar contains the mapping between: varName and bpelExpression
       * 					e.g.: L1 | bpws:getLinkStatus('buyToSettle')
       * 
       * @param bpelExpr
       */
      public String bpelExpr2BoolEvalExpr(String bpelExpr)
         {
          String res = "";         
          /* potential different solution ...
          Pattern p = Pattern.compile(regExBPELLinkExpr);
          Matcher m = p.matcher(bpelExpr);
          boolean b = m.matches();
          
          System.out.println("Follows regExp: " + b);
          String [] tokens = bpelExpr.split(regExBPELLinkExpr);
          for (int i=0;i<tokens.length;i++)
             System.out.println(i + "-th Token is: " + tokens[i]);
             */
          res = this.replaceOperators(bpelExpr);
          //System.out.println(" Result Op replace: " + res );
          res = this.replaceVarNames(res);
          //System.out.println(" Result varName replace:" + res);
          return res;
         }    
      
      private void buildHashMapLink(String bpelExpr)
         {
         
          
          
         }
      
      private String replaceOperators(String bpelExpr)
         {          
          Pattern pNot 	= Pattern.compile(this.notOperator);
          Pattern pAnd	= Pattern.compile(andOperator);
          Pattern pOr	= Pattern.compile(this.orOperator);
          Matcher mNot 	= pNot.matcher(bpelExpr);
          bpelExpr = mNot.replaceAll(" ! ");
          Matcher mAnd	= pAnd.matcher(bpelExpr);
          bpelExpr=mAnd.replaceAll(" && ");
          Matcher mOr	= pOr.matcher(bpelExpr);
          bpelExpr=mOr.replaceAll(" || ");
          
          return bpelExpr;
         }	
    
      private String replaceVarNames(String bpelExpr)
         {
          this.hMVar = new HashMap();
          Pattern pVar = Pattern.compile(this.varName);
          Matcher mVar = pVar.matcher(bpelExpr);
          this.hMLink = new HashMap();          
          Pattern pLink	= Pattern.compile(this.linkName);
          Matcher mLink	= pLink.matcher(bpelExpr);
          int i=0;
          mVar.reset();
          while (mVar.find())
             {              
              hMVar.put(this.varNameReplace + i,mVar.group());
              // build the HashMap for links.
              mLink.find();              
              System.out.println("Group: " + mLink.group());
              this.hMLink.put(this.varNameReplace + i,mLink.group().replaceAll("\\(\\'","").replaceAll("\\'\\)",""));
              bpelExpr 	= mVar.replaceFirst(" " + this.varNameReplace + i + " ");
              mVar 		= pVar.matcher(bpelExpr);
              i++;
             }
          return bpelExpr;
         }
     
      public HashMap getHashMapVar()
         {
         	return this.hMVar;
         }
      
      public HashMap getHashMapLink()
         {
         	return this.hMLink;
         }
   }
