
package model.backEnd;

/**
 * EmitterPNMLConstants.java
 * Purpose: Naming Conventions ("Constants") for places and transitions 
 * 		 	used in EmitterPNML. 
 * 			This is class needs to be extended to do this for all places
 * 			and transitions. Because of time restrictions I am doing it only
 * 			for the scope Places and Transitions.
 * @author Stephan Breutel
 * @version 1.0
 * @created Sep 3, 2006
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
public class EmitterPNMLConstants
   {
   
    /* Scope Places and Transitions */
    public final static String toContinueName 		= "to_Continue_";
    public final static String toStopName			= "to_Stop_";
    public final static String snapShotName			= "snapShot_";
    public final static String noSnapShotName		= "no_snapShot_";    
    public final static String transStartScopeName 	= "startScope_";
    public final static String transEndScopeName	= "endScope_" ;
    public final static String transStopCollector	= "stopCt_"	;
    public final static String transNoSnapCollector	= "noSnapCt_" ;
    public final static String transSnapCollector	= "snapCt_"	;
    public final static String collector			= "collected_";
    public final static String toExitPlaceName		= "toExit_"	;
    public final static String noExitPlaceName		= "noExit_"	;          
    public final static String toExitCollectName	= "toExitCt_";
    public final static String noExitCollectName	= "noExitCt_";
    public final static String exitCollectorName	= "exitCtP_";
    
    /* Termination Handler Places and Transitions */
    public final static String _TH_toInvoke			= "TH_toInvoke_";
    public final static String _TH_eTerminate		= "TH_eTerminate_";
    public final static String _TH_invoked			= "TH_invoked_";
    public final static String _TH_pre				= "TH_Pre_";
    public final static String _TH_ready			= "TH_Ready_";
    public final static String _TH_abstract			= "TH_Abstract_";
    public final static String _TH_finish			= "TH_Finish_";
    public final static String _TH_post				= "TH_Post_";

      /**
       * MethodName : EmitterPNMLConstants
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a EmitterPNMLConstants object has been created.
       * 
       */
      public EmitterPNMLConstants()
         {
            super();
            // TODO Auto-generated constructor stub
         }

   }
