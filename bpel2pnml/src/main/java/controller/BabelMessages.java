/**
 * BabelMessages.java
 * Purpose: This class contains a collection of possible BABEL messages.
 * 			Messages are of the following Type:
 * 				Error,Warning,Info.
 * @author Stephan Breutel
 * @version 1.0
 * @created 1/03/2005
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

package controller;

public class BabelMessages
   {
   	static final int Error		=	0;
   	static final int Warning	= 	1;
   	static final int Info		=	2;
   	
   	
      /**
       * MethodName : BabelMessages
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a BabelMessages object has been created.
       *
       */
      public BabelMessages()
         {
            super();            
         }
      	
      public static String error(int no,String str)
         {
          String errorMsg="Error_" + no + " : ";
         	switch (no)
         	{
         	case 0: return errorMsg + " Not enough parameters.\n";
         	case 1: return errorMsg + " Unsupported Language.\n";
         	case 2: return errorMsg + " Incorrect number of Parameters for BPEL 2 YAWL translation.";
         	default: return "Unknown error message. Complain to the Programmer.\n";
         	}	
         }
      
      public static String info(int no,String str)
         {
         String infoMsg="Information_" + no + " : ";
         switch(no)
         	{
         	case 0: return infoMsg + " Call Structure: Babel BPEL [toPNML|toYAWL] <WSDL-File> <BPEL-File>";         	
         	case 2: return infoMsg + " Call Structure for BPEL 2 YAWL translation: \n" +
         							 " Babel BPEL toYAWL <WSDL-File> <BPEL-File> <mapFile> \n ";
         	case 3 : return infoMsg + " The translation process from " + str + 
         							  " into YAWL started successfully.\n Good luck.\n";
         	case 1001 : return infoMsg + " The Option fromYAWL is not implemented yet.";
         	default: return "Unknown info message. Complain to the Programmer.\n";
         	}
         }

   }
