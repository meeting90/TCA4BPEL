/**
 * StringTools.java
 * Purpose:		Collection of use helper-methods for String operations. 
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

import java.util.List;

public class StringTools
   {

    public static String escChar= "\\";
    
    		
    		
      /**
       * MethodName : StringTools
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a StringTools object has been created.
       * 
       */
      public StringTools()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
    public static boolean startsWithString(String nodeName, String[] nodeNames)
       {
   		for (int i=0;i<nodeNames.length;i++)
	      if (nodeName.startsWith(nodeNames[i]))
	         return true;
	    return false;  
       }
    
   
    
    /**
     * 
     * MethodName	: isInStringArray
     * Purpose    	: check if the String name is contained in the StringArray names
     * 				  for exact match.
     * Pre		  	:
     * Post		  	: 
     * 
     * @param nodeName
     * @param nodeNames
     * @return
     */  
  	public static boolean isInStringArray(String nodeName, String[] nodeNames)
   	   {
   	   	for (int i=0;i<nodeNames.length;i++)
   	      if (nodeName.compareTo(nodeNames[i])==0)
   	         return true;
   	   	return false;   
   	   }
  	
  	public static int indexOfStringInArray(String nodeName, String[] nodeNames)
   	   {
   	   	for (int i=0;i<nodeNames.length;i++)
   	      if (nodeName.compareTo(nodeNames[i])==0)
   	         return i;
   	   	return -1;     	   	   	   
   	   }
  	
  	public static int noOccurence(String name, String [] stringArray)
  	   {
  	    int no=0;
  	    for (int i=0;i<stringArray.length;i++)
  	     if (name.compareTo(stringArray[i])==0)
  	        no++;
  	    return no;
  	   }
  	/**
  	 * 
  	 * MethodName	: isInList
  	 * Purpose    	: 
  	 * Pre		  	: l is List of String objects
  	 * Post		  	: 
  	 * 
  	 * @param s
  	 * @param l
  	 * @return
  	 */
  	public static boolean isInList(String s, List l)
  	   {
  	    for(int i=0;i<l.size();i++)
  	       if (s.compareTo((String) l.get(i))==0)
  	          return true;
  	    return false;    
  	   }
  	/**
  	 * 
  	 * MethodName	: findInList
  	 * Purpose    	: 
  	 * Pre		  	: l is List of String objects
  	 * Post		  	: 
  	 * 
  	 * @param s
  	 * @param l
  	 * @return
  	 */
  	public static int findInList(String s,List l)
  	   {
 	    for(int i=0;i<l.size();i++)
 	       if (s.compareTo((String) l.get(i))==0)
 	          return i;
 	    return -1;    
 	   }
  	
  	/**
  	 * 
  	 * MethodName	: modifySpecialChar
  	 * Purpose    	: modifies the String in a way, such that special characters can
  	 * 				  be handeled by the MySQL-database, e.g. for directory names.
  	 * Pre		  	:
  	 * Post		  	: 
  	 * 
  	 * @param s
  	 * @return
  	 */
  	public static String modifySpecialChar(String s)
  	   {
  	    int fromIndex=0;
  	    int index;
  	    String res="";
  	    if (s==null)
  	       return null;
  	    while( (index=s.indexOf(StringTools.escChar,fromIndex))>=0)
  	       {
  	        System.out.println("Contains special characters.");
  	        // insert "\\" (i.e. + 2 *StringTools.escChar.
  	        res	=	res + s.substring(fromIndex,index) + StringTools.escChar+ StringTools.escChar;  	       
  	        fromIndex = index + 1;
  	       }
  	    res = res + s.substring(fromIndex,s.length());
  	    //System.out.println("res="+res);
  	    return res;
  	   }  	
   }
