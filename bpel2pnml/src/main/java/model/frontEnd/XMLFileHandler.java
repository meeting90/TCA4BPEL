 /**
 * XMLFileHandler.java
 * Purpose	 : 	Class that is used to read and write XML Files
 * ClassProperty :	General			
 * 
 * @author  		Stephan Breutel
 * @version 1.0  	26 Dec 2004
 * @created    		26 Dec 2004
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
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


//import java.io.IOException;


public class XMLFileHandler
  {
   protected String fileName; 		/* the current active fileName 		*/
   protected String fileContent; 	/* the content of the active file 	*/

   /**
   * MethodName: 	Constructor
   * Purporse  : 	construct an XMLFileHandler object and initialise the specFile and specContent 	
   * Pre       :	fileName is absolut path + XML Filename and is an exisiting and valid file
   * Post      :	an XMLFileHandler Object is constructed.
   */
	   
   public XMLFileHandler(String fileName)
      {
      	this.fileName = fileName;
        this.fileContent = getFileContent(fileName);
      }
	
   /**
    * MethodName	: getFileContent
    * Purpose    	: returns the content of the XML file
    * Pre		  	: a file Content exists
    * Post		  	: return of the file Content
    * 
    * @return		String, file Content of the XML file.
    */
   public String getFileContent()
      {
       return fileContent;
      }
   
   /**
   * MethodName : 	getFileContent
   * Purpose	:  	get the content of the workflow specification file via a String
   * Pre	:       valid specFile is specified via input parameter specFile
   * Post	:    	the content of the specFile is returned via a String
   *
   * @param specFile 	String, the name of the specification (absolut path + XML Filename)      
   *
   * @ return			String, the String of the specFile
   */
   public String getFileContent(String specFile)
      {
		String spec="";
	    String line;
	
		try {
	    	/* BufferedReader makes it more efficient */
            BufferedReader bR = new BufferedReader(new FileReader(specFile));
            while ((line=bR.readLine()) != null) 
                {
                spec=spec+line;
                }
            bR.close();           
	        } catch (IOException iOE) {
	            System.out.println("File error: " + iOE);
	            return spec;
	        }     	
	       return spec;
	      }
   
   /**
    * MethodName	: getDataFromXMLString
    * Purpose    	: 
    * Pre		  	:
    * Post		  	: 
    * 
    * @param xmlString
    * @param tagNameStart
    * @param tagNameEnd
    * @param offset
    * @return
    */
   public int[] getDataFromXMLString(String xmlString, String tagNameStart, String tagNameEnd, int offset)
      {	
		int [] interval=new int[2];
		interval[0] = xmlString.indexOf(tagNameStart,offset); 
		interval[1] = xmlString.indexOf(tagNameEnd,offset);
	
	    if (interval[0]!=-1)
		  interval[0]=interval[0] + tagNameStart.length();                 	    
		return interval;
      }   
    }
   
