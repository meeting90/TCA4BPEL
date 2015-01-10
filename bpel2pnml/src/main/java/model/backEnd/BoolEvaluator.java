/**
 * BoolEvaluator.java
 * Purpose: This class provides a set of methods useful for the evaluation of boolean expressions.
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


import tools.MathTools;
import tools.StringTools;

public class BoolEvaluator
   {
    
    public final static String [] operators = {"!","&&","||"}; // in precedence order.
    private EvalTree evalTree;
    private int nV;
    private boolean [][] cM;
    private HashMap hM;
    
    
    public BoolEvaluator()
       {
        super();
       }
      /**
       * MethodName   : BoolEvaluator
       * Purpose	  : Constructor
       * Pre		  : bExpr is a boolean expression in infix notation 
       * 					using "(,)" brackets
       * 					operators: {&,|,!}. 
       * 					Space between varName and operator.
       * 					for example correct expr are: 
       * 						a1 & (a2 | a3)
       * 						 (a1 & a2) | a3
       * Post	      : a BoolEvaluator object has been created.
       *    
       * 
       */
      public BoolEvaluator(String bExpr)
         {
           super();   
           nV = this.calcNoVar(bExpr);
         }
      
           
      /* infix -> postfix and build the evaluationTree of a given postfix notation */
      
      /**
       * 
       * MethodName		: infix2Postfix
       * Purpose    	: This method takes a boolean expression in infix notation as input
       * 				  and produces its representation as postfix notation. 
       * 
       * Pre		  	: bExpr is a correct boolean expression in infix notation
       * 				  with "space" between each token. e.g. a & ( b | c ) 
       * Post		  	: postfix notation of bExpr. 
       * 
       * @param bExpr ,	String containing the boolean expression in infix notation.
       * @return		String containing the boolean expression in postfix notation.	
       */
      public String infix2Postfix(String bExpr)
         {
          String oString ="";
          String delim = " ";
          StringTokenizer sT = new StringTokenizer(bExpr);
          Stack stack = new Stack();
          
          while (sT.hasMoreTokens()) 
             {
              String tok = sT.nextToken();
              //System.out.println(tok);
              if (StringTools.isInStringArray(tok,BoolEvaluator.operators))
                 {
                   while ( !stack.isEmpty() && higherPrecedence((String) stack.peek(), tok))                      
                       oString = oString + (String) stack.pop() + delim;
                   stack.push(tok);                                             
                 }
              else if (tok.compareTo("(")==0)
                 stack.push(tok);
              else if (tok.compareTo(")")==0)
                 while ( !stack.isEmpty() && StringTools.isInStringArray((String) stack.peek(),BoolEvaluator.operators))
                    oString = oString + (String) stack.pop() + delim;
              else // here comes a boolean variable.
                 oString = oString + tok + delim;                 
             }
          // now it remains to empty the stack.
          while (!stack.isEmpty())
             {
             	String el = (String) stack.pop();
             if ( StringTools.isInStringArray(el,BoolEvaluator.operators) )
                oString = oString + el + delim;
             }
          
          // return oString with leading and trailing whitespace omitted.
          return oString.trim();
         }
      
      /**
       * 
       * MethodName		: buildEvalTree
       * Purpose    	: 
       * Pre		  	: postFixExpr is a correct boolean expression in postfix notation.
       * Post		  	: 
       * 
       * @param postFixExpr
       */
      public void buildEvalTree(String postFixExpr)
         {
          StringTokenizer sT = new StringTokenizer(postFixExpr);
          Stack stack = new Stack();
          evalTree = new EvalTree();
          while (sT.hasMoreTokens()) 
            {
             String tok = sT.nextToken();
            // System.out.println(tok);
             if (StringTools.isInStringArray(tok,BoolEvaluator.operators))
                {
                 EvalTree evOp = new EvalTree(tok);
                 // unary operator
                 if (tok.compareTo("!")==0)
                    evOp.insertLeftTree( ((EvalTree) stack.pop()).getRoot());
                 // binary operator
                 else 
                    {
                     evOp.insertRightTree( ((EvalTree) stack.pop()).getRoot() );
                     evOp.insertLeftTree(((EvalTree) stack.pop()).getRoot());
                    }                   
                 stack.push(evOp);
                }
             else // variable                
                 stack.push(new EvalTree(tok));                
            }
          evalTree = (EvalTree) stack.pop();
          if (!stack.isEmpty())
             System.out.println("stack is not empty. Error???");
         }
      
      /**
       * 
       * MethodName		: evaluate
       * Purpose    	: Evaluates the boolean Expression associated with this object. 
       * Pre		  	: 
       * Post		  	: 
       * 
       * @param v
       * @return
       */
      boolean evaluate(boolean [] v)
         {          
          
          evalTree.initHashMap(hM);          
          return evalTree.evaluate(evalTree.getRoot(),v);
          
         }
      
      public boolean [][] computeTruthTable(String bExpr)
         {
          int n=calcNoVar(bExpr);
          boolean []tt = new boolean[MathTools.intPower(2,n)];
          
          // 1. build the evalTree (infix2Postfix,buildEvalTree).
          String e=infix2Postfix(bExpr);
          // System.out.println("PostFix Exp= " + e);
          buildEvalTree(e);
          // 2. compute the combination matrix.
          initCombMatrix(n);
          int [] row = new int[2];
          row[0]=0;
          row[1]=MathTools.intPower(2,nV) -1;
          buildCombMatrix(nV,row);
          for (int i=0;i<MathTools.intPower(2,nV);i++)
             tt[i] = evaluate(cM[i]);                                         
          return MathTools.appendCol(cM,tt);
         }
      
      private void initCombMatrix(int n)
         {
          cM = new boolean[MathTools.intPower(2,n)][n];                  
          //System.out.println("CM=[" + cM.length + "," + cM[0].length + "]");
         }
      
      private void buildCombMatrix(int i,int [] row)
         {
           if (i==1)
              {              
               cM[row[0]][nV-1]=true;
               cM[row[1]][nV-1]=false;
              }
           else
              {
               int [] interval = new int[2];
               interval[0] = row[0];
               interval[1] = row[0] + MathTools.intPower(2,(i-1)) - 1;
               MathTools.setCol(cM,nV-i,interval,true);              
               buildCombMatrix(i-1,interval);
               interval[0] = row[0] + MathTools.intPower(2,(i-1));
               interval[1] = row[1];
               MathTools.setCol(cM,nV-i,interval,false);
               buildCombMatrix(i-1,interval);
              }                      
         }
      
      private int calcNoVar(String bExpr)
         {
          StringTokenizer sT = new StringTokenizer(bExpr);
          int no=0;
          hM=new HashMap();
          while (sT.hasMoreTokens()) 
             {
              String tok = sT.nextToken();              
              if ( isVar(tok) && ! hM.containsKey(tok))
                 {                                  
                  hM.put(tok,new Integer(no));
                  no ++;
                 }
             }
          nV=no;    
          //System.out.println("N=" + nV);
          return no;
         }
          
       private boolean isVar(String s)
          {
           return  	! StringTools.isInStringArray(s,BoolEvaluator.operators) &&
          			! (s.compareTo(")")==0) &&
          			! (s.compareTo("(")==0);
          }
      
      /**
       * 
       * MethodName		: higherPrecedence
       * Purpose    	: 
       * Pre		  	: operator2 is IN: this.operators
       * 			      AND
       * 				  this.operators contains the operators in precedence order.
       * 				  AND
       *   				  equal(operator1,operator2) <=> operator1==operator2
       * Post		  	: TRUE , if operator1 has higher or equal precedence then operator2. 
       * 				  FALSE, otherwise, i.e. operator1 is not in this.operators
       * 
       * @param operator1
       * @param operator2
       * @return
       */
      private boolean higherPrecedence(String operator1, String operator2)
         {
          if (!StringTools.isInStringArray(operator1,BoolEvaluator.operators))
             return false;
          if (StringTools.indexOfStringInArray(operator1,BoolEvaluator.operators) <= 
              StringTools.indexOfStringInArray(operator2,BoolEvaluator.operators))
             return true;
          return false;
         }
      
      /* Here comes all the evaluation stuff */
      
      /**
       * 
       * EvalTree Class. Tree implemetation used within this class as follows:
       * 		buildEvalTree constructs the evaluation Tree of a boolean expression given in postFix notation.
       * 		evaluate uses an EvalTree to compute the output for a given vector of bool.Values
       * @created 22/04/2005
       */
      private class EvalTree
      	{
         Node root;
         Stack resStack;
         int varNo; // this is used as "external" counter within the recursive evaluation method.
         HashMap hM;
         
         
         EvalTree()
         	{
             root = null;
             resStack = new Stack();
         	}
         EvalTree(String s)
         	{
             Node n = new Node(s);
             resStack = new Stack();
             root = n;
         	}
         
         public Node getRoot()
            {
             return root;
            }
         
         public void insertLeftTree(Node left)
         	{
             root.left = left;
         	}
         
         public void insertRightTree(Node right)
            {
             root.right = right;                         
            }  
         public void initVarCount()
            {
             this.varNo = -1; // -1 because we increment before the return.
            }
         
         public void initHashMap(HashMap hM)
            {
             this.hM=hM;
            }
         /**
          * 
          * MethodName	: evaluate
          * Purpose    	: 
          * Pre		  	: This method relies on the property that a translation into postfix is 
          * 			  "order"-invariant with respect to the variables. Therefore the leftmost-leaf
          * 			  contains the first variable and the tree preserved the ordering of the 
          * 			  variables. This allows an easy value setting using the vector v.
          * 			  BEFORE the call of this method the Caller must initialise varNo by calling
          * 			  the initHashMap() method. (to map varname to index)
          * Post		: 
          * 
          * @param n
          * @param v          
          * @return
          */
          public boolean evaluate(Node n,boolean [] v)
             {             
              // traverse the tree              
              if (n==null)
                 return false;             
              if (StringTools.isInStringArray(n.item,BoolEvaluator.operators))
                 {
                  //System.out.println("Operator: " + n.item);
                  if (n.item.compareTo("!")==0)
                     return !evaluate(n.left,v);
                  else if (n.item.compareTo("&&")==0)
                     return evaluate(n.left,v) && evaluate(n.right,v);
                  else 
                     return evaluate(n.left,v) || evaluate(n.right,v);
                 }
              else // variable
                 {
                  int index= ((Integer) hM.get(n.item)).intValue(); 
                  //System.out.println(" var no. " + index + "=" + v[index]);
                  return v[index];
                 }
             }
         } 
      
      private class Node
   		{                
         String item;
         Node left;
         Node right;
         
         Node(String s)
          {
           this.item = s;
           this.left = null;
           this.right= null;
          }                  
   		}
      
      /* primitive set and get methods */
            
   }
