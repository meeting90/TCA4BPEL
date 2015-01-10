/**
 * XMLModifier.java
 * Purpose	: 	Class that is used to modify data obtained via XML.
 *  			
 * 
 * @author  		Stephan Breutel
 * @version 1.0  	12 Dec 2004
 * @created    		12 Dec 2004
 * ATTENTION : SOON BE OBSOLETE.
 * 
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
/* Java stuff */
import java.io.*;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;



// 2DO: generalise to abtract class and impl. yawl specific rules.

public class XMLModifier
   {
    
     private String xmlString;
	
	
     public void setXMLStringFromFile(String xmlFile)
       {
	String xmlString="";
        String line;

	try {
	    /* BufferedReader makes it more efficient */
            BufferedReader bR = new BufferedReader(new FileReader(xmlFile));
            while ((line=bR.readLine()) != null) 
                {
                xmlString=xmlString+line;
                }
            bR.close();           
        } catch (IOException iOE) {
            System.out.println("File error: " + iOE);
            xmlString="<Failure> File Not Found </Failure>";
        }     	        
       }


     public void setXMLString(String s)
	{
	xmlString = s;
	}

     public String getXMLString()	
	{
	 return xmlString;	
	}

     //2do : pass modifier class : for now simply modify all data randomly //	
     public String mkXMLOutput()
       {
	String result="";
	SAXBuilder builder = new SAXBuilder();
	List setBoolList = new ArrayList();
	 try {
           	Document doct = builder.build(new StringReader(xmlString));
        	Element  e = doct.getRootElement();
		System.out.println(e.getName());
		List     l = e.getChildren();
		for(int i=0;i<l.size();i++)
		  {
		   Element child  = (Element) l.get(i);	
		   String  str	  = child.getName();
		   if ( str.indexOf("want") >= 0 )
			setBoolList.add(new Integer(i));					   
		  }
		if ( setBoolList.size() > 0)
		  {		
		   //boolean [] boolArray=rs.createRandomBooleanArray(setBoolList.size(),1);
		   boolean [] boolArray = new boolean[setBoolList.size()];
		   for (int i=0;i<setBoolList.size();i++)
		      {
		       Element child  = (Element) l.get( ((Integer)setBoolList.get(i)).intValue() );
		       child.addContent( (new Boolean(boolArray[i])).toString() );
		      }
		  }
		result=new XMLOutputter().outputString(doct.getRootElement()).trim();		
	       } catch (JDOMException e) {
             	  e.printStackTrace();
                } catch (IOException e) {
            	  e.printStackTrace();
        	}	      	    
	return result;	
  	}

  }
