/**
 * ListTools.java
 * Purpose:		Tools for Lists.		 
 * @author Stephan Breutel
 * @version 1.0
 * @created 11/04/2005
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

import java.util.*;
import org.jdom.*;


public class ListTools
   {

      /**
       * MethodName : ListTools
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a ListTools object has been created.
       * 
       */
      public ListTools()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
   	   /**
   	    * MethodName	: concList
   	    * Purpose    	: Concatenate a List of objects.
   	    * Pre		  	: l is ListArray where non of the entries is null.
   	    * Post		  	: a single list is returned.
   	    * 
   	    * @param l	, ListArray
   	    * @return
   	    */
		public static List concList(List [] l)
		   {
		   	List result = new ArrayList();
		   	for (int i=0;l!=null && i<l.length;i++)
		   	   for (int j=0;j<l[i].size();j++)
		   	      result.add(l[i].get(j));
		   	return result;
		   }
		
		/**
		 * 
		 * MethodName	: concListElement
		 * Purpose    	: concatenate a list of elements
		 * Pre		  	:
		 * Post		  	: 
		 * 
		 * @param l
		 * @return
		 */
		public static List concListElement(List [] l)
		   {
			List result = new ArrayList();
		   	for (int i=0;i<l.length;i++)
		   	   for (int j=0;j<l[i].size();j++)
		   	      result.add((Element) l[i].get(j));
		   	return result;
		   }
		/**
		 * 
		 * MethodName	: removeNull
		 * Purpose    	: remove all elements of a list with the value null. 
		 * Pre		  	: TRUE
		 * Post		  	: l is a List without null elements.
		 * 
		 * @param l
		 * @return
		 */
		public static List removeNull(List l)
		   {
		   	ListIterator lIt = l.listIterator();
		   	List toRemoveList = new ArrayList();
		   	//int i=0;
		   	// collect all elements which have to be removed in the toRemoveList.
		   	// otherwise: java.util.ConcurrentModificationException
		   	while (lIt.hasNext())
		   	   {
		   	    Object el = lIt.next();
		   	    if ( el==null )
		   	       {
		   	        toRemoveList.add(el);
		   	      //  System.out.println("Element " + i + " added in toRemoveList. ");
		   	       }
		   	  //  i++;
		   	   }
		   	lIt = toRemoveList.listIterator();
		   	while (lIt.hasNext())
		   	   {
		   	    Object el = lIt.next();
		   	    l.remove(el);
		   	   // System.out.println(" Element removed.");
		   	   }
		   	
		   	return l;
		   }
		
		/**
		 * 
		 * MethodName	: deepCopy_String
		 * Purpose    	: deep copy of the content of a String List. 
		 * Pre		  	:
		 * Post		  	: 
		 * 
		 * @param l
		 * @return
		 */
		public static List deepCopy_String(List l)
		   {
		    List res = new ArrayList();
		    if (l==null)
		       return null;
		    for(int i=0;i<l.size();i++)
		       {
		        res.add(new String(l.get(i).toString()));
		       }
		    return res;
		   }
		
		public static List clone(List l)
		   {
		    List res = new ArrayList();
		     if (l==null)
		       return null;
		     for(int i=0;i<l.size();i++)
		       {
		        res.add(l.get(i));
		       }
		    return res;
		   }
		
		
		/**
		 * 
		 * MethodName	: toStringArray
		 * Purpose    	: return the i-th row of an listArray and store it in an StringArray,
		 * 				  APPLICATION in: 
		 * Pre		  	:
		 * Post		  	: 
		 * 
		 * @param l
		 * @param i
		 * @return
		 */
		public static String[] toStringArray(List[] l, int i)
		   {
		    String [] res = new String[l.length];
		    for(int j=0;j<l.length;j++)
		       if (l[j].get(i)!=null)
		          res[j] = l[j].get(i).toString();		       
		    return res;
		   }
		

   }
