/**
 * MathTools.java
 * Purpose:		Collection of methods helpful for mathematical operations.
 * 				The method names and behaviours are motivated through my
 * 				Matlab experience. 
 * @author Stephan Breutel
 * @version 1.0
 * @created 7/04/2005
 * 
 * *  Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

package tools;

public class MathTools
   {

    
     
    /**
     * 
     * MethodName	: max
     * Purpose    	: compute the maximum of an int-Array 
     * Pre		  	:
     * Post		  	: 
     * 
     * @param list
     * @return
     */
  	public static int max(int[] list) 
   	   {                       
   	   	  if ( list.length==0 )
   	   	     return 0;
          int max = list[0];  
          for (int i = 1; i < list.length; i++)
             {                
              if (list[i] > max)
                 max = list[i];
             }                      
          return max;         
       }
  	
  	/**
  	 * 
  	 * MethodName	: appendCol
  	 * Purpose    	: append a column to a matrix.
  	 * Pre		  	: m is a [k,l] matrix AND v is [k] vector.
  	 * Post		  	: a [k,l+1] matrix m2 is returned with (Matlab: [m,v])
  	 * 
  	 * @param m
  	 * @param v
  	 */
  	public static boolean[][] appendCol(boolean [][] m, boolean[] v)
  	   {
  	    int k = m.length;
  	   	int l = m[0].length;
  	   	//System.out.println("k=" +k + ",l=" +l);
	    boolean [][] m2 = new boolean[k][l+1];
	    
	    for (int i=0;i<k;i++)
	       {
	        for(int j=0;j<l;j++)
	           m2[i][j] = m[i][j];
	        m2[i][l] = v[i];
	       }	 
	     return m2;
  	   }
  	
  	/**
  	 * 
  	 * MethodName	: assignValuesToArray
  	 * Purpose    	: 
  	 * Pre		  	: v is an Array with v.length >= indexVector.length
  	 * 				  and r is an Array with r.length=indexVector.length
  	 * 
  	 * Post		  	:  v[indexVector] = r (following Matlab notation). 
  	 * 
  	 * 
  	 * @param v
  	 * @param entryVector
  	 * @param r
  	 */
  	public static void assignValuesToArray(Object [] v, int [] indexVector, Object []r)
  		{
  	      for (int i=0;i<indexVector.length;i++)
  	         v[indexVector[i]] = r[i];  	      
  		}
  	/**
  	 * 
  	 * MethodName	: getIndexVector
  	 * Purpose    	:  
  	 * Pre		  	: from < to
  	 * Post		  	: indexVector = from:to (matlab notation)
  	 * 
  	 * @param from
  	 * @param to
  	 * @return
  	 */
  	public static int[] getIndexVector(int from,int to)
  	   {
  	   	int [] indexVector = new int[to-from + 1];
  	   	for (int i=0;i<to-from+1;i++)
  	   	   indexVector[i]=from + i;
  	   	return indexVector;
  	   	
  	   }
  	
  	/**
  	 * 
  	 * MethodName	: isInVector
  	 * Purpose    	: check if d is contained in v 
  	 * Pre		  	: true	
  	 * Post		  	: index of d in v xor -1 if d is not in v.
  	 * 
  	 * @param d
  	 * @param v
  	 * @return
  	 */
  	public static int isInVector(double d,double [] v)
  	   {
  	    for(int i=0;i<v.length;i++)
  	       if (v[i]==d)
  	          return i;  	      
  	    return -1; 
  	   }
  	/**
  	 * 
  	 * MethodName	: isInVector
  	 * Purpose    	: check if d is contained in v 
  	 * Pre		  	: true	
  	 * Post		  	: index of d in v xor -1 if d is not in v.
  	 * 
  	 * @param d
  	 * @param v
  	 * @return
  	 */
  	public static int isInVector(int d,int [] v)
  	   {
  	    for(int i=0;i<v.length;i++)
  	       if (v[i]==d)
  	          return i;  	      
  	    return -1; 
  	   }
  	
  	
  	public static int intPower(int a,int b)
  	   {  	    
  	    return new Double(Math.pow(a,b)).intValue();
  	   }
  	
  	public static void setCol(boolean [][] bM,int col,int [] interval,boolean b)
  	   {  	      	   
  	    for (int i=interval[0];i<=interval[1];i++)
  	       bM[i][col]=b;
  	   }
  	

   }
