/**
 * XMLFileHandlerYAWL.java
 * Purpose	 : 	Class that is used to handle with an XMLFile.
 *			It inherits all properties from XMLFileHandler and 
 *			extends it functionality with YAWL specific stuff.
 * ClassProperty :	Specific with respect to YAWL
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


import java.util.ArrayList;
import java.util.List;


public class XMLFileHandlerYAWL extends XMLFileHandler
   {    
   
   	   
   public XMLFileHandlerYAWL(String fileName)
      {
	super(fileName);
      }
	
    /**
    * MethodName : 	getAllTasks
    * Purpose	:  	get all task of a specification File
    * Pre	:       valid specFile is specified via input parameter specFile
    *			the string '<decomposition id="' is used to identify the start
    *                    of the taskName,
    * 			the string '" xsi:type="WebServiceGatewayFactsType">' is used to identify
    *			the end of the taskName
    * Post	:    	a list of tasks identified via name is returned
    *
    * @param specFile 		String, the name of the specification (absolut path + XML Filename)      
    *
    * @ return			List,  lsit of all tasks
    */
    public List getAllTasks(String specFile)
       {				
       		ArrayList l = new ArrayList();
            String specContent;
            String tagStart = "<decomposition id=\"";
            String tagEnd = "\" xsi:type=\"WebServiceGatewayFactsType\">";
            int offset = 0;
            int[] interval = new int[2];

            if (this.fileName.compareTo(specFile) != 0)
               {
                  specContent = getFileContent(specFile);
               } else
               specContent = fileContent;
            interval = getDataFromXMLString(specContent, tagStart, tagEnd,
                  offset);
            while (interval[0] != -1)
               {
                  System.out.println("Intervals Now");
                  System.out.println(interval[0]);
                  System.out.println(interval[1]);
                  l.add(specContent.substring(interval[0], interval[1]));
                  offset = interval[1] + tagEnd.length();
                  interval = getDataFromXMLString(specContent, tagStart,
                        tagEnd, offset);
               }
            return l;
         }
   }
