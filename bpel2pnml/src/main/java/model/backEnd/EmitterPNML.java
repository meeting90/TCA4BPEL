/**
 * EmitterPNML.java
 * Purpose: Implements the Emitter interface to produce XML-code for (PIPE) PNML.
 * 			TODO: 	introduce parameter for the constructor to specify if EPNML or PIPE PNML
 * 					should be used.
 * 					
 * 					main problems occured always in conjection with handlers. This has several
 * 					reasons:
 * 					i) initially they have not been implemented
 * 					ii) the first implements relied on "abstractions" compared to the paper and
 * 						on fast bug-fixes, i.e. they are not documented (or not well documented). 
 * 					Hence to improve the output of BPEL2PNML a proper complete implementation of
 * 					the Handler stuff is needed. I am not involved in this project anymore and 
 * 					have another two major software projects to write and maintain.
 * 					Additionally: the initial version of BPEL2PNML was implemented under tight time
 * 					constraints. This lead to a not fully documented and proper designed software.
 * 					I would like to redesign some parts, but I unfortunately do not have the time.
 * 
 * 
 * @author Stephan Breutel
 * @version 1.0
 * @created 4/04/2005
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

package model.backEnd;

import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.Comment;
import org.jdom.Namespace;

import babeltools.*;


import java.util.List;
import java.util.ArrayList;

import model.data.Block;
import tools.MathTools;
import tools.StringTools;

import java.util.*;
import controller.Babel;

// import BPEL specific stuff 
import model.languageDefinition.*;



public class EmitterPNML implements Emitter
   {
   	  Namespace n0;
   	  /* class attributes required for graphical settings. */
   	  int yOffset;	/* "hack?" : this attribute is used to add an additional offset i.e. necessary after
   	  				an switch or pick */
   	  int xStart;  		// initial start position.   	  
   	  int noPrevBA;		// number of previous basic activities.
   	  int noPrevSA;		// number of previous structured activities.
   	  final int xposMax;	
   	  int inputTreeHeight;
   	  int xMiddle;   	  
   	  int [] gYIncr;
   	  int xHandlerOffset; /* {Event,Fault,Compensation} Handler get shifted to right. additionally:
   	  						  xHandlerOffset can later be used as flag (xHandlerOffset == 0) */
   	  /* Helper-DataStructures required to keep information during the translation process. */
   	  HashMap hMLinkInfo;  /* HashMap for Linkinformation */
   	  Stack sjfStack;      // suppressJoinFailure flag. pop() at callTime and push at CodeGenerationTime.  
   	  					   // Strategie : see also Babel-Algorithm paper, page 3.
   	  HashMap hMSources;   // postprocessing: used to connect transitions of source Links to 
   	  HashMap hMTargets;   // postprocessing: the places of the target links.
   	  HashMap hMSjf;	   // postprocessing: used to connect the sjf Transition to the places 
   	  					   // of the targetLinks.
   	  HashMap hMSkip;	   // postprocessing: add the link between the final skipping transition and
   	  					   // the lsf-places.
   	  HashMap hMFaultHandler; // used to resolve the connection between a faultHandler and the transition
   	  						  // refering to this faultHandler. (faultName,transName)
   	  HashMap hMCompHandler;  // used to connect a compensationHandler of a scope with the correct 
   	  						  // compensate activity. (hMCompHandler set: in genTaskElement (BA)
   	  						  // used in genHandler.
   	  HashMap hMEventHandler; // (faultName of throw Activity, toInvokeName of EventHandler)
   	  
   	  HashMap hMScopeTList;	// scope and the corresponding terminationList (bugFix05)
   	 
   	  List doWhileList;		// list of pre_While Transition Elements within a scope.
   	  							// insert into list: genWhile, building arc between  toContinue and pre_while
   	    						// within: genScope. this is also the place where the list gets emptied.
   	  
   	  List collectorList;		// bugFix03.9: list of collector places. collectorplace arc to post-of process.
   	  
   	  List collectorTransList;	// bugFix03.10: list of snapCt,noSnapCt transitions. neeeded to build
   	    						// double arc to the complete Place of the process.
   	  	  
  	  List terminationList[]; 	// an List array containing termination activities.
  	  							// 0 ... BA 1 ... BA and sjf 2 ... SA  3 ... pick 4 ... while 
  	  							// Strategie: at each call to a scope the termination list gets emptied.
  	  							// bugFix05: quick & dirty: associate the scope with it.
   	  List propertyList;   // List of properties for EmitterPNML. 
   	  					   // interpretation: propertyList.size()==1 AND: 
  	  					   //    propertyList.get(0) is a boolean indicating if the SourceFile has an exitCommand.
   	  String exitActivity; // this string contains either "" OR the name (including the id) of the exitCommand.
   	  
   	  List eHList_Scope;	// list of eventhandlers for a scope
   	  						// added to construct the blue arc of Fig.17 between all eventhandlers
   	  						// and all faulthandlers of the scope.
   	  List fHList_Scope;	// fault handler
   	  
   	  List _FH_ToInvoke2StopCt_Scope; // bugFix09: arc between toInvoked of FH_i to stopCt of FH_j.
   	  
   	     	
      /**
       * MethodName 	: EmitterPNML
       * Purpose	  	: Constructor
       * Pre		  	: TRUE
       * Post	      	: an EmitterPNML object has been created.
       * 
       */
      public EmitterPNML()
         {
            super();
            gYIncr 							= new int[2];
            gYIncr[BabelConst.basic]		= 150;
            gYIncr[BabelConst.structured]	= 50;
            xStart = 60;
            noPrevBA = 0;
            noPrevSA = -1; // -1 because of topLevel is always SA.
            xposMax = 440+xStart;
            xMiddle	= xposMax/2;
            xHandlerOffset  = 0; //TODO: should be dependent on out-most skip Path.
            inputTreeHeight = -1; // unknown       
            sjfStack		= new Stack();
            hMLinkInfo		= new HashMap();
            hMSources 		= new HashMap();
            hMTargets		= new HashMap();
            hMSjf			= new HashMap();
            hMSkip			= new HashMap();
            hMFaultHandler	= new HashMap();
            hMCompHandler	= new HashMap();            
            // bugfFix03
            hMEventHandler  = new HashMap();
            // bugFix03.8
            doWhileList	= new ArrayList();
            //bugFix03.9
            collectorList	= new ArrayList();
            //buFix03.10
            collectorTransList = new ArrayList();
            //bugFix05:
            hMScopeTList	= new HashMap();
            // 16.06.05
            propertyList 	= new ArrayList();
            propertyList.add(new Boolean("false")); // hasExit command   
            propertyList.add(new Boolean("false")); // hasCompensate command for a given scope.
            exitActivity	= "";
            // 20.06.05
            terminationList = new List[5];
            for (int i=0;i<5;i++)
              terminationList[i] = new ArrayList();  
            //>> bugFix08:
            eHList_Scope	= new ArrayList();
            fHList_Scope	= new ArrayList();
            //<<
            // >> bugFix09
            this._FH_ToInvoke2StopCt_Scope = new ArrayList();
            //<<.
            
         }
      
      /**
       * 
       * MethodName		: setProperties
       * Purpose    	: sets general properties for the Emitter. 
       * Pre		  	: propertyList is a list of valid properties for a specific Emitter.
       * Post		  	: properties for the Emitter according to the propertyList have 
       * 				  been set XOR an error message is reported to stdout.
       * 
       * @param o, property to set.
       * @param i, i-th property.
       */
      public void setProperty(Object o,int i)
         {
          // TODO: check how to define c-like macros in java, e.g. #define 
          //        found thus far: JAPPO.
          // TODO : check if the actual classes between o and propertyList.get(i) match.
         if ( propertyList.size()>=i )        
              propertyList.set(i,o);             
         else
             System.out.println("ERROR. PropertyList was not set because it does not match with"
                                + "the size of the PropertyList used here.");                       
         }
      /**
       * 
       * MethodName	: getProperties
       * Purpose    	: returns the propertyList of an Emitter. 
       * Pre		  	: 
       * Post		  	: 
       * 
       * @return
       */
      public List getProperties()
         {
          return propertyList;
         }
      
      public void setTreeHeight(int h)
         {
          this.inputTreeHeight = h;
         }
      /* (non-Javadoc)
       * @see model.backEnd.Emitter#header()
       */
      public String header()
         {            
            return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>";
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#prologue(java.lang.String)
       */
      public String prologue(String processName)
         {            
            return "<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
                   "<pnml>\n" +
                   "<net id=\"Net-One\" type=\"P/T net\">\n";
         }
      
      /**
       * 
       * MethodName		: preProcessing
       * Purpose    	: Here : we set the flag for sjf. (suppressjoinFailure)
       * Pre		  	: l is List with: 
       * 					l.get(0)=element  AND element can be any activity incl. <process> 
       *
       * 					
       * Post		  	: 
       * 
       * @param l
       */
      public void preProcessing(List l)
         {         
          Element 	e = (Element) l.get(0);
          if (StringTools.isInStringArray(e.getName(),BPEL.dataItem))
             return; // do nothing, e.g. for "links"
          Attribute a = e.getAttribute("suppressJoinFailure"); 
          System.out.println(e.getName());
          if ( a==null )
             {             
              if ( sjfStack.isEmpty() )
                 {
                  System.out.println("Stack is Empty And No sjf Attribute. => FALSE.");
                  sjfStack.push(new Boolean("false"));
                 }
              // apply Inheritance
              else
                 {
                  System.out.println("Apply Inheritance: Push:" + sjfStack.peek());
                  sjfStack.push(sjfStack.peek());
                 }
             }
          else
             {
              sjfStack.push(new Boolean(a.getValue().compareTo("yes")==0));
              System.out.println("PUSH: " + (a.getValue().compareTo("yes")==0));
             }
         }
      
      
      public Element prologueElement(String processName)
         {         
            // new added 11.10.2005: Version Tag.
            Comment eVersion = new Comment(Babel.BabelVersion);
      		Element e 	= new Element("pnml");
      		Element	e2	= new Element("net");
      		e2.setAttribute("id",processName); 
      		e2.setAttribute("type","P/T net");
      		e.addContent(eVersion);
      		e.addContent(e2);
      		
      		return e;
         }  

      
      public List[] genStructureCode(String operation,List blockList,int treeLevel)
         {
          /* 2do: Code CleanUp and Generalisation.           
     		if (operation.compareTo("sequence")==0)
     		   return null; //this.genSequence(blockList,treeLevel);
     		else
     		   {
     		   	System.out.println("operation " + operation + " not supported yet.");
     		   	return null;
     		   }
     	  */
          return null;
         }
     
      /* OBSOLETE SOON? public List[] genSwitch(List[] block,int treeLevel)
         {
          List [] res=null;
          return res;
         }*/
      
      /**
       * MethodName	:	preProcessNode
       * 	
       * Purpose	: operations which need to be performed BEFORE the generation of the
       * 			  Code for the next Node.
       *        			
       * PRE		: l is null XOR 
       * 			  l is a List with
       * 			  	l.get(0) = Integer with : 0 ... no Handler 	
       * 										  1 ... begin Handler {event,faul,compensation} 
       * 										  2 ... end Handler	
       * 	
       * POST		: 
       * 
       */
      public void preProcessNode(List l)
         {
            /* increment the number of previous structured activities 
        	 			in order to set the correct position for the basic activities 
        				at creation time. This is necessary due to the recursive code-generation.
            */
         if ( l==null ) /* l==null iff non-handler structured activity. */ 
         	noPrevSA = noPrevSA + 1;         
         else if (l!=null)
            {
         	   if ( ((Integer) l.get(0)).intValue()==1 )
         	      {
         	       xHandlerOffset = xMiddle + 25;         	            	    
         	      }
         	   else if ( ((Integer) l.get(0)).intValue()==2 )
         	      {
         	       xHandlerOffset = 0;         	      
         	      }
            }                    	               	    
         }
                  
      /**
       * 
       * MethodName		: genScope
       * Purpose    	: Generate code for a scope activity. The topLevel activity of a BPEL process 
       * 				  is always a Scope. Process itself is scope. 
       * Pre		  	: A Scope has exactly one primary activity.
       * 					=> the first element of the blockList will be used.
       * 				  	l[0]	... blockList		("normal" activities)       
       * 					l[1]	... controlLinks	
       * 					l[2]	... handlerList		({event,fault,compensation} handler activities)
       * 					l[3]	... eventList	    (Names for onMsg,onAlarm)          												
       * 					l[4]	... catchList
       * 					from : BabelEngine.genCodeScope(Element e,int breadth, int treeDepth)
       *                    List info = new ArrayList();  
       * 					info.add(new Integer(breadth)); 0	      
	   *   					info.add(new Integer(treeDepth));	1  
	   *   					info.add(new Integer(conNo)); 2  	      
	   *   					info.add(e.getName());  3
	   * 					info.add(scopeStack); 4
       * 
       * Post		  	: Code for a scope has been generated.
       * 
       * @param l
       * @param info
       * @return
       */
      public Block genScope(List [] l, List info)
         {          
          Info i_			= new Info(l,info);  
          boolean sjfFlag 	= ((Boolean)this.sjfStack.pop()).booleanValue();
          List[] res 		= new List[7];
          List catchList	= l[4];
          // produce the standard 
          List [] wrapEl 	= genBlockWrappingElements(i_.blockName,i_.yposStart,i_.yposEnd);          
          int xpos			= xMiddle,ypos;          
          ypos 				= i_.yposStart - gYIncr[BabelConst.structured];
          Block resBlock	= null;
          // Names for Places, Transitions of the Scope activity.          
          String toContinueName 		= EmitterPNMLConstants.toContinueName 	+ i_.id;
          String toStopName				= EmitterPNMLConstants.toStopName 		+ i_.id;
          String snapShotName			= EmitterPNMLConstants.snapShotName 		+ i_.id;
          String noSnapShotName			= EmitterPNMLConstants.noSnapShotName	+ i_.id;    
          String transStartScopeName 	= EmitterPNMLConstants.transStartScopeName		+ i_.id;
          String transEndScopeName	 	= EmitterPNMLConstants.transEndScopeName   	+ i_.id;
          String transStopCollector		= EmitterPNMLConstants.transStopCollector		+ i_.id;
          String transNoSnapCollector	= EmitterPNMLConstants.transNoSnapCollector  		+ i_.id;
          String transSnapCollector		= EmitterPNMLConstants.transSnapCollector		+ i_.id;
          String collector				= EmitterPNMLConstants.collector		+ i_.id;
          String toExitPlaceName		= EmitterPNMLConstants.toExitPlaceName			+ i_.id;
          String noExitPlaceName		= EmitterPNMLConstants.noExitPlaceName			+ i_.id;          
          String toExitCollectName		= EmitterPNMLConstants.toExitCollectName		+ i_.id;
          String noExitCollectName		= EmitterPNMLConstants.noExitCollectName		+ i_.id;
          String exitCollectorName		= EmitterPNMLConstants.exitCollectorName		+ i_.id;          
          // the scope Activity itself.
          Element readyPlace		= (Element) wrapEl[1].get(0);
          Element startPlace 		= (Element) wrapEl[1].get(1);
          Element completePlace		= (Element) wrapEl[1].get(2);
          Element finishPlace 		= (Element) wrapEl[1].get(3);
          Element transStartScope	= this.genTransitionElement(transStartScopeName,transStartScopeName);
          Element transEndScope		= this.genTransitionElement(transEndScopeName,transEndScopeName);
          // 4 Places wrapping the scope.               
          Element toContinue 		= this.genPlaceElement(toContinueName,toContinueName);
          Element toStop			= this.genPlaceElement(toStopName,toStopName);
          Element snapShot			= this.genPlaceElement(snapShotName,snapShotName);
          Element noSnapShot		= this.genPlaceElement(noSnapShotName,noSnapShotName);                            
          // Graphical Information.
          int xposSkipPath    = this.xposMax + 20 + 100 * (this.inputTreeHeight -i_.treeDepth);          
          int yposMiddleScope = i_.yposStart-4*this.gYIncr[BabelConst.structured] + 
                               (i_.yposEnd+4*this.gYIncr[BabelConst.structured] -
                               (i_.yposStart-4*this.gYIncr[BabelConst.structured]))/2;	
          EmitterPNMLGraphics.addRotationElement(transStartScope,90);
          EmitterPNMLGraphics.addRotationElement(transEndScope,90);
          EmitterPNMLGraphics.addGraphicsElement(transStartScope,xpos,ypos);          
          EmitterPNMLGraphics.addGraphicsElement(transEndScope,xpos,i_.yposEnd + gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addGraphicsElement(toContinue,xposSkipPath+100,ypos);
          EmitterPNMLGraphics.addGraphicsElement(snapShot,xposSkipPath+100,i_.yposEnd + gYIncr[BabelConst.structured]);       		
          EmitterPNMLGraphics.addGraphicsElement(noSnapShot,xposSkipPath + 100,i_.yposEnd);
          EmitterPNMLGraphics.addGraphicsElement(toStop,xposSkipPath + 100,yposMiddleScope + 50);          
          // add the wrapping places,arcs and transitions to the result.
          for(int i=0;i<wrapEl.length;i++)
             res[i]=wrapEl[i];
          for(int i=wrapEl.length;i<7;i++)
             res[i]=new ArrayList();    
          Element postTrans	= (Element) wrapEl[2].get(1);
          res[1].add(toContinue);
          res[1].add(toStop);
          res[1].add(snapShot);
          res[1].add(noSnapShot);
          // scope : always has exactly one block.         
          // get the finish and ready places of the inner block.
          // Fault,Event and Compensation Handlers are treated in a special way.
          Block b 			= (Block) i_.blockList.get(0);
          List ioBlock[] 	= b.getIOBlock();
          Element bReady 	= (Element) ioBlock[0].get(0);
          Element bToSkip	= (Element) ioBlock[0].get(1);
          Element bFinish 	= (Element) ioBlock[1].get(0);
          Element bSkipped	= (Element) ioBlock[1].get(1);
          // connect via arcs.
          Element arcPStart2TStart			= this.genArcElement(startPlace.getAttributeValue("id"),transStartScopeName);
          Element arcTStartScope2bReady 	= this.genArcElement(transStartScopeName,bReady.getAttributeValue("id"));
          Element arcTStartScope2ToContinue	= this.genArcElement(transStartScopeName,toContinueName);
          Element arcToContinue2TEndScope	= this.genArcElement(toContinueName,transEndScopeName);
          Element arcTEndScope2SnapShot		= this.genArcElement(transEndScopeName,snapShotName);
          Element arcbFinish2TEndScope		= this.genArcElement(bFinish.getAttributeValue("id"),transEndScopeName);     
          Element arcTEndScope2completePlace= this.genArcElement(transEndScopeName,completePlace.getAttributeValue("id"));
          // add the transitions and arcs (necessary places have been already added via wrapEl).
          res[2].add(transStartScope);
          res[2].add(transEndScope);          
          res[3].add(arcPStart2TStart);
          res[3].add(arcTStartScope2bReady);
          res[3].add(arcTStartScope2ToContinue);
          res[3].add(arcToContinue2TEndScope);
          res[3].add(arcTEndScope2SnapShot);
          res[3].add(arcbFinish2TEndScope);
          res[3].add(arcTEndScope2completePlace);
          //  TokenCollector. 15.06.2005
          Element collectorPlace = this.genPlaceElement(collector,collector);
          EmitterPNMLGraphics.addGraphicsElement(collectorPlace,xpos + 90,
				 								i_.yposEnd+2*gYIncr[BabelConst.structured]+10);          
          res[1].add(collectorPlace);          
        
          Element tNoStopCollector = this.genTransitionElement(transNoSnapCollector,transNoSnapCollector);
          EmitterPNMLGraphics.addGraphicsElement(tNoStopCollector,xpos + 160,
                								 i_.yposEnd+1.5*gYIncr[BabelConst.structured]);
          Element tSnapCollector	= this.genTransitionElement(transSnapCollector,transSnapCollector); 
          EmitterPNMLGraphics.addGraphicsElement(tSnapCollector,xpos + 160,
				 								 i_.yposEnd+2.5*gYIncr[BabelConst.structured]);
              
	      res[2].add(tNoStopCollector);
	      res[2].add(tSnapCollector);	                
          res[3].add(genArcElement(noSnapShotName,transNoSnapCollector));
          res[3].add(genArcElement(snapShotName,transSnapCollector));
          res[3].add(genArcElement(transSnapCollector,collector));
          res[3].add(genArcElement(transNoSnapCollector,collector));
          //>> bugFix03.9,bugFix03.10: arc from collector always to end of process.
          // res[3].add(genArcElement(collector,postTrans.getAttributeValue("id")));
          collectorList.add(collector);
          collectorTransList.add(transSnapCollector);
          collectorTransList.add(transNoSnapCollector);
          if ( info.get(3).toString().compareTo(BPEL.topActivity[0])==0 ) //TopLevel.
             {
              for (int i=0;i<collectorList.size();i++)
                 res[3].add(genArcElement((String) collectorList.get(i),postTrans.getAttributeValue("id")));
              for(int i=0;i<collectorTransList.size();i++)
                 {
                  res[3].add(genArcElement((String) collectorTransList.get(i),completePlace.getAttributeValue("id")));
                  res[3].add(genArcElement(completePlace.getAttributeValue("id"),(String) collectorTransList.get(i)));
                 }
             }
          // << bugFix03.9, bugFix03.10
          // 16.06.05: Adding  the toExit and noExit places and arcs if topLevel scope AND
          //           if exit-command is in the source file.
          if ( info.get(3).toString().compareTo(BPEL.topActivity[0])==0 && 
               ((Boolean) this.propertyList.get(0)).booleanValue() )
             {             
              Element toExitPlace = genPlaceElement(toExitPlaceName,toExitPlaceName);
              Element noExitPlace = genPlaceElement(noExitPlaceName,noExitPlaceName);
              Element toExitCollect = genTransitionElement(toExitCollectName,toExitCollectName);
              Element noExitCollect = genTransitionElement(noExitCollectName,noExitCollectName);
              Element exitCollector = genPlaceElement(exitCollectorName,exitCollectorName);
              EmitterPNMLGraphics.addGraphicsElement(toExitPlace,xposSkipPath + 100,yposMiddleScope - 50);
              EmitterPNMLGraphics.addGraphicsElement(noExitPlace,xposSkipPath + 100,yposMiddleScope - 100);
              EmitterPNMLGraphics.addGraphicsElement(toExitCollect,xposSkipPath + 100,
                                                     i_.yposEnd - 4*gYIncr[BabelConst.structured]);
              EmitterPNMLGraphics.addGraphicsElement(noExitCollect,xposSkipPath + 100,
                    								 i_.yposEnd - 3*gYIncr[BabelConst.structured]);
              EmitterPNMLGraphics.addGraphicsElement(exitCollector,xposSkipPath + 100,
                                                     i_.yposEnd + 3*gYIncr[BabelConst.structured]);              
              res[1].add(toExitPlace);
              res[1].add(noExitPlace);
              res[1].add(exitCollector);
              res[2].add(toExitCollect);
              res[2].add(noExitCollect);
              res[3].add(genArcElement(transStartScopeName,noExitPlaceName));
              res[3].add(genArcElement(noExitPlaceName,exitActivity));
              res[3].add(genArcElement(exitActivity,toExitPlaceName));
              res[3].add(genArcElement(toContinueName,exitActivity));
              res[3].add(genArcElement(exitActivity,toStopName));
              res[3].add(genArcElement(toExitPlaceName,toExitCollectName));
              res[3].add(genArcElement(noExitPlaceName,noExitCollectName));
              res[3].add(genArcElement(toExitCollectName,exitCollectorName));
              res[3].add(genArcElement(noExitCollectName,exitCollectorName));
              res[3].add(genArcElement(exitCollectorName,postTrans.getAttributeValue("id")));
             }                               
          /* 20.06.05 addTermination */
          /* bugfix06_ii: 04.10.05: extension of termination. */
          String t_completePlace = completePlace.getAttributeValue("id");
          String t_finishPlace   = bFinish.getAttributeValue("id");
          addTermination(res,toStopName,toContinueName,i_.id,transStopCollector,
                         t_completePlace,noSnapShotName,t_finishPlace,xpos,i_.yposEnd,
                         info.get(3).toString().compareTo(BPEL.topActivity[0])==0);
          /* IO interface */
          res[4].clear();
          res[5].clear();
          res[4].add(readyPlace);
          res[5].add(finishPlace);
          res[6].add(i_.blockName);                    
          List [] code = b.getCode();
          // add the inner blocks. here: should be exactly one block.
          for(int k=0;k<b.getCodeLength();k++)                 
             res[k].addAll(code[k]);          
          List[] skipCode=this.genSkipPathSA(i_.blockList,i_.blockName,
				   							 i_.yposStart-4*this.gYIncr[BabelConst.structured],
				   							 i_.yposEnd+4*this.gYIncr[BabelConst.structured],
				   							 i_.treeDepth,i_.conNo);                 
          if (skipCode!=null)
             {              
	          // toSkipPlace and skipTrans of the Skip-Path of the structured activity itself
	          String toSkipPlace		= ((Element) skipCode[1].get(0)).getAttributeValue("id");
	          String skipStartTrans		= ((Element) skipCode[2].get(0)).getAttributeValue("id");
	          String skipEndTrans		= ((Element) skipCode[2].get(1)).getAttributeValue("id");
	          List [] skipPlaces 		= getSkipPlaces(i_.blockList);	          
	          // add the arc between the endSkipping transition and no_SnapShot.
	          res[3].add(this.genArcElement(skipEndTrans,noSnapShotName));	          
	          //>>> 06.06.05: Adding arc to toStop (avoid single node).
	          // 17.06.05: removed it again. Otherwise Errors in conjuntion with FaultHandler.
	          //           Anyway toStop is now connected always due to the introduction of 
	          // 		   a defaut FaultHandler.
	          //res[3].add(genArcElement(skipStartTrans,toStopName));
	          //res[3].add(genArcElement(toStopName,skipEndTrans));
	          // <<<
	          
	          // add controlLinks to the structured activity switch
	          if (!l[1].isEmpty())
	             {	             
	              addControlLinks(res,l[1],sjfFlag,i_.treeDepth,i_.conNo,
	                   			 "T_pre"+i_.blockName,"T_post"+i_.blockName,readyPlace.getAttributeValue("id"),
	                   			 finishPlace.getAttributeValue("id"),
	                   			 toSkipPlace,skipStartTrans,skipEndTrans,skipPlaces[0],skipPlaces[1]);  	        
	              // add the arc between the sjfEnd transition and noSnapShot.
	              // attention! we assume to follow here the naming convention!!!
	              if (sjfFlag)
	                 res[3].add(this.genArcElement("sjf2_" + i_.id,noSnapShotName));
	             }	         
             }
          // bugFix03.8: connection between toContinue and doWhile
          for(int i=0;i<doWhileList.size();i++)
             {
              res[3].add(genArcElement(toContinueName,(String) doWhileList.get(i)));
              res[3].add(genArcElement((String) doWhileList.get(i),toContinueName));
             }
          doWhileList.clear();
          // Integrate Code of a Handler          
          List bInfo = b.getInfo();       
          if (bInfo==null)
             {
              System.out.println("binfo is null.");
              System.out.println("readyElement: " + bReady.getAttributeValue("id"));
             }
          // needed for eventHandler.
          String transPostName    = ((Element) bInfo.get(0)).getAttributeValue("id"); /* name of the post-Transition of the sub-activity of the scope. */
          int counterFaultHandler = 0; // needed to pass the correct entry of the catchList to addFaultHandler
          int counterCompHandler  = 0;
          if ( !l[2].isEmpty() )         
            {
             for (int i=0;i<l[2].size();i++)
                {
                 Block handlerBlock = (Block) l[2].get(i);
                 if (handlerBlock.getBlockType()==BabelConst.eventHandler)
                    addEventHandlerCode(res,transStartScopeName,transEndScopeName,transPostName,handlerBlock,
                          				toContinueName,transStopCollector,
                          				info.get(3).toString().compareTo(BPEL.topActivity[0])==0,
                          				(Stack) info.get(4));
                 else if (handlerBlock.getBlockType()==BabelConst.faultHandler)
                    {         
                     System.out.println("CatchList Size: " + catchList.size());
                     addFaultHandlerCode(res,transStartScopeName,transEndScopeName,
                          				toContinueName,toStopName,noSnapShotName,snapShotName,
                          			    bFinish.getAttributeValue("id"), 	
                          				completePlace.getAttributeValue("id"),
                          				handlerBlock,(List) catchList.get(counterFaultHandler),
                          				i_.id,
                          				transStopCollector,
                          				info.get(3).toString().compareTo(BPEL.topActivity[0])==0);
                     counterFaultHandler ++;                     
                    }
                 else
                    addCompensationHandler(res,noSnapShotName,snapShotName,handlerBlock);
                }
            }
          if ( counterFaultHandler == 0 ) // i.e.: no Fault Handler for the scope has been defined             
             addDefaultFaultHandler(res,transStartScopeName,transEndScopeName,
                                    toContinueName,toStopName,i_.id,xpos,ypos,noSnapShotName,
                                    completePlace.getAttributeValue("id"),
                                    bFinish.getAttributeValue("id"), 
                                    transStopCollector,	
                                    info.get(3).toString().compareTo(BPEL.topActivity[0])==0);
          // if no compHandler has been defined for a non-topLevel scope then add the default
          // compensationHandler.
          if ( counterCompHandler == 0 &&  info.get(3).toString().compareTo(BPEL.topActivity[0])!=0 )
             addDefaultCompHandler();
          //>> bugFix08: all FaultHandler have connection to all Eventhandler of the scope.
          // 1. connect all toInvoke Places from the Eventhandlers to the 
          //    transInvoked Transitions of the FaultHandlers.
          for(int i=0;i<this.eHList_Scope.size();i++)
             for(int j=0;j<this.fHList_Scope.size();j++)
                res[3].add(genArcElement((String) eHList_Scope.get(i), 
                      					 (String) fHList_Scope.get(j)));
          
          // 2. empty the lists.
          eHList_Scope.clear();
          fHList_Scope.clear();          
          // <<< bugFix 08.
          // bugFix09: arc from toInvoke_FH_i -> stopCt_FH_j, i!=j
          for (int i=0;i<this._FH_ToInvoke2StopCt_Scope.size();i++)
             {
              String[] _s = (String []) this._FH_ToInvoke2StopCt_Scope.get(i); 
              String toInvokeFH = _s[0] ;
              // all j<i
              for (int j=0;j<i;j++)
                 {
                  String [] _sj = (String []) this._FH_ToInvoke2StopCt_Scope.get(j);
                  String transStopCt = _sj[1];
                  res[3].add(genArcElement(toInvokeFH,transStopCt));
                 }
              // all j>i
              for(int j=i+1;j<this._FH_ToInvoke2StopCt_Scope.size();j++)
                 {
                  String [] _sj = (String []) this._FH_ToInvoke2StopCt_Scope.get(j);
                  String transStopCt = _sj[1];
                  res[3].add(genArcElement(toInvokeFH,transStopCt));
                 }
             }
          this._FH_ToInvoke2StopCt_Scope.clear();
          // << bugFix09.
          resBlock = new Block(res,4,BabelConst.structured);                       
          resBlock.addListArray(skipCode);          
          addInfo(postTrans,resBlock);
                    
          return resBlock;                                             
         }
      
     
      private void addDefaultCompHandler()
         {
          // TODO
         
         }
      private void addCompensationHandler(List[] res,String noSnapShot, String snapShot,Block compHandler)
         {
          /* 11.10.05: not supported in the moment */
          /*
          List iList = compHandler.getInput();	         
          // get the no-op transition and the invoke transition.
          Element noOpTrans  	= (Element) iList.get(0);         
          Element invokeTrans 	= (Element) iList.get(1);
          // add the compensation Code itself.
          List [] code = compHandler.getCode();
          for(int k=0;k<compHandler.getCodeLength();k++)	                           	           
             res[k].addAll(code[k]);	 
          // generate arcs between these transitions and the noSnapShot and snapShot place of the scope.        
          res[3].add(genArcElement(noOpTrans.getAttributeValue("id"),noSnapShot));
          res[3].add(genArcElement(noSnapShot,noOpTrans.getAttributeValue("id")));
          res[3].add(genArcElement(invokeTrans.getAttributeValue("id"),noSnapShot));
          res[3].add(genArcElement(snapShot,invokeTrans.getAttributeValue("id")));
          */                   
         }
      
      /**
       * 
       * MethodName		: addEventHandlerCode
       * Purpose    	: To add the source code for a {event,fault,compnesation} handler
       * Pre		  	: res is List Array with the first 4 entries as follows:
       * 					res[0] ... labels
       * 					res[1] ... places
       * 					res[2] ... transitions	
       * 					res[3] ... arcs
       * 					        				
       *  
       * Post		  	: res is modified. 
       * 
       * @param res
       * @param transStartScopeName
       * @param transEndScopeName
       * @param transPostName
       * @param eventHandler
       */
      private void addEventHandlerCode(List[] res, String transStartScopeName, String transEndScopeName,
                                  String transPostName,Block eventHandler,String toContinueName,
                                  String transStopCollector,boolean isTopLevelScope,
                                  Stack scopeStack)
         {                                        
          /* an eventHandler has just two input places enabled and toInvoke */
          List iList = eventHandler.getInput();
          Element enablePlace 	= (Element) iList.get(0);
          //bugFix03.7 : transition eNormal.
          Element transE		= (Element) iList.get(1);
          Element toInvokePlace = (Element) iList.get(2);          
          /* the 4-Arcs to connect an eventHandler */
          Element arcStartScope2Toinvoke = this.genArcElement(transStartScopeName,toInvokePlace.getAttributeValue("id"));
          Element arcStartScope2Enable	 = this.genArcElement(transStartScopeName,enablePlace.getAttributeValue("id"));
          Element arcEnable2Tpost		 = this.genArcElement(enablePlace.getAttributeValue("id"),transPostName);
          Element arcToInvoke2EndScope	 = this.genArcElement(toInvokePlace.getAttributeValue("id"),transEndScopeName);
          // add the eventHandler itself.
          List [] code = eventHandler.getCode();
          for(int k=0;k<eventHandler.getCodeLength();k++)             	              	            
              res[k].addAll(code[k]);             
          // add the arcs to res.
          res[3].add(arcStartScope2Toinvoke);
          res[3].add(arcStartScope2Enable);
          res[3].add(arcEnable2Tpost);
          res[3].add(arcToInvoke2EndScope);	      
          // bugFix03.7: add the double arc. to the toContinue of the enclosing scope.
          res[3].add(genArcElement(transE.getAttributeValue("id"),toContinueName));
          res[3].add(genArcElement(toContinueName,transE.getAttributeValue("id")));
          // bugFix_Anna_1: add the double arc toContinue <-> eNormal to all of its parent scopes.         
          String toContinueBaseName = "to_Continue_";
          for (int i=0;i<scopeStack.size();i++)
             {
             res[3].add(genArcElement(transE.getAttributeValue("id"),toContinueBaseName + scopeStack.get(i)));
             res[3].add(genArcElement(toContinueBaseName + scopeStack.get(i),transE.getAttributeValue("id")));
             }          
          //>>> 05.10.2005: Chuns Proposal. toInvoke to stopCt.
          if (!isTopLevelScope)
           ;//  res[3].add(genArcElement(toInvokePlace.getAttributeValue("id"),transStopCollector));
          //<<<
         }
      
      /**
       * 
       * MethodName		: addDefaultFaultHandler
       * Purpose    	: Default Fault Handler in case no fault Handler was defined for the scope. 
       * Pre		  	: No Fault Handler was defined for the scope.
       * Post		  	: 
       * 
       * @param res
       * @param transStartScopeName
       * @param transEndScopeName
       * @param toContinuePlace
       * @param toStopPlace
       * @param id					-- the unique id of the current scope.
       * @param xpos				-- x position of the startScope transition.
       * @param ypos				-- y position of the startScope transition.
       */
      private void addDefaultFaultHandler(List[] res, String transStartScopeName, String transEndScopeName,
            							  String toContinuePlace,String toStopPlace,String id,int xpos,int ypos,
            							  String noSnapShotPlace,String completePlace,String finishPlace,
            							  String transStopCollector,	
            							  boolean isTopLevelScope)
         {
           String toInvokeName = "toInvoke_" + id;
           String eFaultName   = "eFault_" + id;
           Element toInvoke    = this.genPlaceElement(toInvokeName,toInvokeName);
           Element eFault	   = this.genTransitionElement(eFaultName,eFaultName);
           EmitterPNMLGraphics.addGraphicsElement(toInvoke,xpos + 100, ypos + 50);
           EmitterPNMLGraphics.addGraphicsElement(eFault,xpos + 200, ypos + 50);
           res[1].add(toInvoke);
           res[2].add(eFault);
           res[3].add(genArcElement(transStartScopeName,toInvokeName));
           res[3].add(genArcElement(toInvokeName,eFaultName));
           res[3].add(genArcElement(toInvokeName,transEndScopeName));
           res[3].add(genArcElement(toContinuePlace,eFaultName));
           res[3].add(genArcElement(eFaultName,toStopPlace));
           //>>> 06.10.2005: Chuns Proposal toInvoke to stopCt
           if(!isTopLevelScope)
              ;//res[3].add(genArcElement(toInvokeName,transStopCollector));
           //<<<
           //>>> bugFix09: - commented 06.10.2005 and added to the _FH
           if (!isTopLevelScope)
              {
               String [] _s = new String[2];
               _s[0] = toInvokeName;
               _s[1] = transStopCollector;
               this._FH_ToInvoke2StopCt_Scope.add(_s);              
              }
           //<<<
           //>> bugFix04.3 Extension of default FaultHandler.
           String tfName = "TF_FinishedDFH_" + id;
           Element tf = this.genTransitionElement(tfName,tfName);
           EmitterPNMLGraphics.addGraphicsElement(tf,xpos + 250, ypos + 50);
           res[2].add(tf);          
           res[3].add(genArcElement(toStopPlace,tfName));
           res[3].add(genArcElement(tfName,noSnapShotPlace));
           res[3].add(genArcElement(tfName,completePlace));          
           //<<< bugFix04.3 
           //>>> bugFix06.iii extension of DefaultFaultHandler.
           if (!isTopLevelScope)
              res[3].add(genArcElement(finishPlace,tfName));
           else
              {           
	           String invoked 		= "invoked_" + id;
	           String beforeExitDFH	= "beforeExitDFH_" + id;
	           res[1].add(this.genPlaceElement(invoked,invoked));
	           res[2].add(this.genTransitionElement(beforeExitDFH,beforeExitDFH));
	           // Exit Activity Frame
	           String rDFH 			= "rDFH_"+id;               
	           String sDFH 			= "sDFH_" + id;
	           String cDFH			= "cDFH_" + id;
	           String fDFH 		 	= "fDFH_" +id;     
	           String preDFH		= "preDFH_" + id;           
	           String exitDFH		= "exitDFH_" + id + "{name=exit}" ; 
	           //18.10.05: eric Email label the transitions that signal abnormal
	           //termination of the top scope
	           String postDFH		= "postDFH_" + id;
	           res[1].add(this.genPlaceElement(rDFH,rDFH));           
	           res[1].add(this.genPlaceElement(sDFH,sDFH));
	           res[1].add(this.genPlaceElement(cDFH,cDFH));
	           res[1].add(this.genPlaceElement(fDFH,fDFH));
	           res[2].add(this.genTransitionElement(preDFH,preDFH));
	           res[2].add(this.genTransitionElement(postDFH,postDFH));
	           res[2].add(this.genTransitionElement(exitDFH,exitDFH));
	           // arcs.
	           res[3].add(genArcElement(eFaultName,invoked));
	           res[3].add(genArcElement(invoked,beforeExitDFH));
	           res[3].add(genArcElement(finishPlace,beforeExitDFH));
	           res[3].add(genArcElement(beforeExitDFH,rDFH));
	           res[3].add(genArcElement(rDFH,preDFH));
	           res[3].add(genArcElement(preDFH,sDFH));
	           res[3].add(genArcElement(sDFH,exitDFH));
	           res[3].add(genArcElement(exitDFH,cDFH));
	           res[3].add(genArcElement(cDFH,postDFH));
	           res[3].add(genArcElement(postDFH,fDFH));
	           res[3].add(genArcElement(fDFH,tfName));                          
              }
           //<<<bugFix06.iii    
         }
      
      /**
       * 
       * MethodName		: addFaultHandlerCode
       * Purpose    	: 
       * Pre		  	:
       * Post		  	: 
	   * 					res[0] ... labels
       * 					res[1] ... places
       * 					res[2] ... transitions	
       * 					res[3] ... arcs
       * 
       * @param res
       * @param transStartScopeName
       * @param transEndScopeName
       * @param toContinuePlace
       * @param toStopPlace
       * @param noSnapShotPlace
       * @param snapShotPlace
       * @param faultHandler
       */
      private void addFaultHandlerCode(List[] res, String transStartScopeName, String transEndScopeName,
                                       String toContinuePlace,String toStopPlace, String noSnapShotPlace, 
                                       String snapShotPlace, String finishActivityPlace,
                                       String completeScopePlace, Block faultHandler,
                                       List catchElement,String id,
                                       String transStopCollector,
                                       boolean isTopLevelScope)
         {                           	      
          // catchElement is a List with two Strings: id-name of catch and the Attributes. (see genCatchName method).
          String catchAttr = (String) catchElement.get(1);
          /* a faultHandler has only one toInvoke place. */              
          List iList = faultHandler.getInput();	         
          // get the previously created places and transitions. Probably it would have been a better
          // solution to create them at this point??? don't have time to check this.
          Element invokedPlaceElement  = (Element) iList.get(0);         
          Element transFHFinishElement = (Element) iList.get(1);
          Element transInvoked2ReadyElement = (Element) iList.get(2); // T_invoked
          Element toInvokePlaceElement = (Element) iList.get(3);
          String throwTransName="";	          
          String toInvokePlace = (String) toInvokePlaceElement.getAttributeValue("id");
          String invokedPlace  = (String) invokedPlaceElement.getAttributeValue("id");
          String transFHFinish = (String) transFHFinishElement.getAttributeValue("id");
          String transInvoked2Ready = (String) transInvoked2ReadyElement.getAttributeValue("id");
          // bugFix03: find a match in
          String eventHandlerToInvoke;
          /* OLD: OBSOLETE SOON.....
          if ( (eventHandlerToInvoke = (String) this.hMEventHandler.get(catchAttr))!=null )
             {
              System.out.println("Generate blue arc of Fig. 17 between Event and FaultHandler("
                                 + eventHandlerToInvoke + "," + transInvoked2Ready +")" );             
              res[3].add(genArcElement(eventHandlerToInvoke,transInvoked2Ready));              
             }
             */
          //>>> bugFix08: all FaultHandler have connection to all Eventhandler of the scope.
          /*
          Iterator it = eHSet.iterator();
          while(it.hasNext())
             {
              eventHandlerToInvoke = (String) it.next();
              System.out.println("Generate blue arc of Fig. 17 between Event and FaultHandler("
                   + eventHandlerToInvoke + "," + transInvoked2Ready +")" );             
              res[3].add(genArcElement(eventHandlerToInvoke,transInvoked2Ready)); 
             }*/
          //<<< bugFix08.
          /* find a match for the given faultHandler */
          if ( (throwTransName = (String) this.hMFaultHandler.get(catchAttr))!=null)	             
              System.out.println("Found matching throw for the faultHandler.");
          else
             {
              System.out.println("Generate e_Default.");              
              throwTransName 	= "e_Default_" + id;
              Element eDefault 	= genTransitionElement(throwTransName,throwTransName);
              res[2].add(eDefault);
             }
          /* the arcs to connect an faultHandler */	         
          Element arcStartScope2ToInvoke 			= genArcElement(transStartScopeName,toInvokePlace);	                											
          //bugFix02 Element arcStartScope2ToContinue			= genArcElement(transStartScopeName,toContinuePlace);
          Element arcToContinue2ThrowTrans			= genArcElement(toContinuePlace,throwTransName);
          //bugFix02 Element arcToContinue2TransEndScope		= genArcElement(toContinuePlace,transEndScopeName);
          Element arcToInvoke2ThrowTrans			= genArcElement(toInvokePlace,throwTransName);
          Element arcToInvoke2EndScope	 			= genArcElement(toInvokePlace,transEndScopeName);
          Element arcThrowTrans2ToStop				= genArcElement(throwTransName,toStopPlace);
          Element arcThrowTrans2Invoked				= genArcElement(throwTransName,invokedPlace);
          Element arcToStop2TransFHFinish   		= genArcElement(toStopPlace,transFHFinish);
          Element arcFinishAct2TransInvoked2Ready 	= genArcElement(finishActivityPlace,transInvoked2Ready);
          Element arcTransFHFinish2completeScope	= genArcElement(transFHFinish,completeScopePlace);
          Element arcTransFHFinish2NoSnapShot		= genArcElement(transFHFinish,noSnapShotPlace);
          //bugFix02 Element arcTransEndScope2SnapShot			= genArcElement(transEndScopeName,snapShotPlace);
          // add Graphical information to the Places and Transitions of the FaultBlock.          
          //Element 
          // add the faultHandler itself.
          List [] code = faultHandler.getCode();
          for(int k=0;k<faultHandler.getCodeLength();k++)	                           	           
              res[k].addAll(code[k]);	             
          // add the arcs to res.
          res[3].add(arcStartScope2ToInvoke);
          //bugfix02: res[3].add(arcStartScope2ToContinue);	 
          res[3].add(arcToContinue2ThrowTrans);
          //bugFix02: res[3].add(arcToContinue2TransEndScope);
          res[3].add(arcToInvoke2ThrowTrans);  
          res[3].add(arcToInvoke2EndScope);    	    
          //>>> 06.10.2005: Chuns Proposal: toInvoke -> stopCt
          //if (!isTopLevelScope)
          //  ; // res[3].add(genArcElement(toInvokePlace,transStopCollector));
          //<<<
          //>>> bugFix09: - commented 06.10.2005 and added to the _FH
          if (!isTopLevelScope)
             {
              String [] _s = new String[2];
              _s[0] = toInvokePlace;
              _s[1] = transStopCollector;
              this._FH_ToInvoke2StopCt_Scope.add(_s);              
             }
          //<<<
          
          res[3].add(arcThrowTrans2ToStop);
          res[3].add(arcThrowTrans2Invoked);
          res[3].add(arcToStop2TransFHFinish); 
          res[3].add(arcFinishAct2TransInvoked2Ready);  
          res[3].add(arcTransFHFinish2completeScope);
          res[3].add(arcTransFHFinish2NoSnapShot);                            
         }
      
      
      
      
     /**
      * 
      * MethodName	: addTermination
      * Purpose    	: 
      * Pre		  	:
      * Post		  	: 
      * 
      * @param res
      * @param toStopName
      * @param toContinueName
      * @param scopeId
      * @param transStopCollector
      * @param completePlace
      * @param noSnapShotName
      * @param finishPlace
      * @param xpos
      * @param ypos
      * @param isTopLevelScope
      */
      private void addTermination(List[] res,String toStopName,String toContinueName,
                                  String scopeId,String transStopCollector,
                                  String completePlace,String noSnapShotName,
                                  String finishPlace,
                                  int xpos,int ypos,
                                  boolean isTopLevelScope)
         {
          //bugFix06_i:
          String toContinue="to_Continue_";
          //bugFix06_ii:
          String toStop		= "to_Stop_";
          //modified for bugFix05:
          //under the same scopeId several basic-activities are possible.
          //but all of them have exactly the same scopeStack status. 
          //Hence Stack can be declared outside the for-Loop.
          List terminationList = (List) this.hMScopeTList.get(scopeId);
          Stack scopeStack = null;
          if (terminationList!=null)
             {
             for (int j=0;j<terminationList.size();j++)
                {
                 // Convention. terminationList[i] contains a list object, where the first entry
                 // is the core Activity, the second the bypass transition 
                 // + bugFix06_i: third entry is the scopeStack. This is needed because 
                 // any coreActivity has a bi-directional arc to toContinuePlace of all upper scopes.
                 // 4.entry is possible  a place (sjf) Path 
                 List l 					= (List) terminationList.get(j);
                 String transActivity		= ((Element) l.get(0)).getAttributeValue("id");
                 String byPassTransName		= ((Element) l.get(1)).getAttributeValue("id");
                 /*
                 System.out.println("TransActivity: " + transActivity);
                 System.out.println("byPassTransName: " + byPassTransName);
                 System.out.println(l.get(2));
                 System.out.println(l.get(2).getClass());
                 System.out.println("j" + j);
                 System.out.println("l.size()="+ l.size());
                 //System.out.println(l.get(3).getClass());                  
                 */
                 // bugFix07: scopeStack is always the last entry in the list.
                 scopeStack = (Stack) l.get(l.size()-1);
                //old: scopeStack					= (Stack)   l.get(2); 
                 // bidirectional: toStop <---> byPass
                 res[3].add(genArcElement(toStopName,byPassTransName));
                 res[3].add(genArcElement(byPassTransName,toStopName));
                 // Bidirectional: toContinue <-> coreActivity, if coreActivity is not a throw activity
                 if (!StringTools.startsWithString(transActivity.substring(0,3),BPEL.throwActivityAbr))
                    {
	                   res[3].add(genArcElement(toContinueName,transActivity)); 
	                   System.out.println("Activity: " + transActivity);
	                   // uni - directional if exit-activity.
	                   if ( !StringTools.startsWithString(transActivity.substring(0,3),BPEL.exitCommandsAbr) )
	                      res[3].add(genArcElement(transActivity,toContinueName));    
	                   else                      
	                      System.out.println("One-directional for activity " + transActivity);
	                   //bugFix06: add for all upperScopes.
	                   for(int i=0;i<scopeStack.size();i++)
	                      {
	                       String upperScopeId = (String) scopeStack.get(i);
	                       String toContinueNameUpper =toContinue + upperScopeId;
 	                       res[3].add(genArcElement(toContinueNameUpper,transActivity));
	                       if ( !StringTools.startsWithString(transActivity.substring(0,3),BPEL.exitCommandsAbr) )
		                      res[3].add(genArcElement(transActivity,toContinueNameUpper));   
	                      }	                   
                    }
                 // check for BA with controlLinks.
                 if ( l.size()>=4 )
                    {
                     String ttName = ((Element) l.get(2)).getAttributeValue("id");
                     System.out.println("BA with SourceLinks. Add Arc to tt-transition: " + ttName);
                     res[3].add(genArcElement(toContinueName,ttName));
                     res[3].add(genArcElement(ttName,toContinueName));
                    }                      
                }
             }
          // bugFix03	: toStopCt_... disabled in the moment. (0==1)
          // bugFix06_ii: moved from genScope to addTerminationa and activated it.
          if ( !isTopLevelScope ) //very old: only for the process scope: info.get(3).toString().compareTo(BPEL.topActivity[0])==0 )
             {
              Element tStopCollector = this.genTransitionElement(transStopCollector,transStopCollector);
	          EmitterPNMLGraphics.addGraphicsElement(tStopCollector,xpos + 80,ypos + .5 *gYIncr[BabelConst.structured]);
	          EmitterPNMLGraphics.addRotationElement(tStopCollector,90);          
	          res[2].add(tStopCollector);
	          res[3].add(genArcElement(toStopName,transStopCollector));
	          res[3].add(genArcElement(transStopCollector,noSnapShotName));
	          res[3].add(genArcElement(transStopCollector,completePlace));
	          res[3].add(genArcElement(finishPlace,transStopCollector));
	          // extension: all upper scopes
	          if (scopeStack!=null)
	             {
	              // tricky: Stack is implemented as a vector and vector.get(0) is the bottom of the stack,
	              // i.e. the most outside (upper) scope. 
	              //String innerScopeId	 = scopeId;
	              
	             /* required Scope places and transitions 
                  * INIT PHASE 
                  * - Places: 	c_Scope, f_Scope_Activity, no_SnapShot, to_Continue, to_Stop 
                  	 - Transitions:	endScope,startScope */
	             
	              /* Transitions */
	              String startScope = EmitterPNMLConstants.transStartScopeName 	+ scopeId;
                  String endScope	= EmitterPNMLConstants.transEndScopeName	+ scopeId;
                  /*Places*/
                  String cScope				=completePlace;
                  String finishScopeActivity=finishPlace;
                  String noSnapShot 	= EmitterPNMLConstants.noSnapShotName + scopeId;                  
                  String toContinueInner = toContinueName;
	              String toStopInner	 = toStopName;
	              /*>>>bugFix09_2
	              // anyway this should not be a for-Loop because seeing the behaviour of the translation
	              // Engine then this should be exactly for one scope.	              
	              //for(int i=scopeStack.size()-1;i>=1;i--) //bugFix09_2 >=1 to avoid betweenToStop to the most outside scope.
	                 									  // see Fig.23.
	              //  {                     
                    String outerScopeId 		= (String) scopeStack.get(i);
	              */
	               //  {                     
                   String outerScopeId 		= (String) scopeStack.get(scopeStack.size()-1);                            
                   String toStopOuter 		= toStop  + outerScopeId;
                   List [] _abstractTH 		= mkAbstractTerminationHandler(scopeId,outerScopeId);                      
                   String eTerminate		= EmitterPNMLConstants._TH_eTerminate+scopeId+"_TO_"+outerScopeId;                      
                      /* OBSOLETE 
                      String betweenToStop		= "betweenToStop_" + (i+1) + "TO" + i + "_" + innerScopeId + "_" +
                                                   outerScopeId;                                            
                      Element betweenToStopTrans= this.genTransitionElement(betweenToStop,betweenToStop);
                      res[2].add(betweenToStopTrans);
                      */                    
                   // 1. add the termination Handler itself.
                   res[1].addAll(_abstractTH[0]);
                   res[2].addAll(_abstractTH[1]);
                   res[3].addAll(_abstractTH[2]);
                   // 2. add the "external" 
                   // 2_1 eTerminate Arcs
                   res[3].add(genArcElement(eTerminate,toStopInner));
                   res[3].add(genArcElement(toContinueInner,eTerminate));
                   res[3].add(genArcElement(eTerminate,toStopOuter));
                   res[3].add(genArcElement(toStopOuter,eTerminate));
                   // 2_2 arcs from inside the TerminationHandler to the Scope                  
                   res[3].add(genArcElement(startScope,
                         	EmitterPNMLConstants._TH_toInvoke+scopeId));
                   res[3].add(genArcElement(EmitterPNMLConstants._TH_toInvoke+scopeId,
                         	endScope));
                   res[3].add(genArcElement(finishScopeActivity,EmitterPNMLConstants._TH_pre+scopeId));
                  
                   res[3].add(genArcElement(toStopInner,EmitterPNMLConstants._TH_post+scopeId));
                   res[3].add(genArcElement(EmitterPNMLConstants._TH_post+scopeId,noSnapShot));
                   res[3].add(genArcElement(EmitterPNMLConstants._TH_post+scopeId,cScope));
                   
                   /*>>> bugFix09_2: removed this part which was within the loop.                      
                      toContinueInner = toContinue + outerScopeId;
                      toStopInner	  = toStopOuter;
                   <<<*/    
	                 
	             }
             }	          	                          
        }
      
      
      /**
       * 
       * MethodName		: mkAbstractTerminationHandler
       * Purpose    	: Abstract Termination Handler according to Fig.23.
       * 				  "Abstract" because the actual Termination Handling Code
       * 				  is abstracted into a single transtion. 
       * Pre		  	:
       * Post		  	: List[3] array is returned with
       * 					res[0] 	... places
       * 					res[1] 	... transitions	
       * 					res[2]	... arcs
       * @param scopeId
       * @return
       */
      private List [] mkAbstractTerminationHandler(String scopeId,String outerScopeId)
         {
          List [] res = new List[3];
          for(int i=0;i<3;i++)
             res[i] = new ArrayList();
          /* Places */
          res[0].add(genPlaceElement(EmitterPNMLConstants._TH_toInvoke+scopeId,
                					EmitterPNMLConstants._TH_toInvoke+scopeId));
          res[0].add(genPlaceElement(EmitterPNMLConstants._TH_invoked+scopeId,
                					EmitterPNMLConstants._TH_invoked+scopeId));
          res[0].add(genPlaceElement(EmitterPNMLConstants._TH_ready+scopeId,
                					EmitterPNMLConstants._TH_ready+scopeId));
          res[0].add(genPlaceElement(EmitterPNMLConstants._TH_finish+scopeId,
                					EmitterPNMLConstants._TH_finish+scopeId));
          /* Transitions */
          res[1].add(genTransitionElement(EmitterPNMLConstants._TH_eTerminate+scopeId+"_TO_"+outerScopeId,
                						EmitterPNMLConstants._TH_eTerminate+scopeId+"_TO_"+outerScopeId));
          res[1].add(genTransitionElement(EmitterPNMLConstants._TH_pre+scopeId,
                						EmitterPNMLConstants._TH_pre+scopeId));
          res[1].add(genTransitionElement(EmitterPNMLConstants._TH_abstract+scopeId,
                						EmitterPNMLConstants._TH_abstract+scopeId));
          res[1].add(genTransitionElement(EmitterPNMLConstants._TH_post+scopeId,
                						EmitterPNMLConstants._TH_post+scopeId));
          /* Arcs */
          res[2].add(genArcElement(EmitterPNMLConstants._TH_toInvoke+scopeId,
                					EmitterPNMLConstants._TH_eTerminate+scopeId+"_TO_"+outerScopeId));
          res[2].add(genArcElement(EmitterPNMLConstants._TH_eTerminate+scopeId+"_TO_"+outerScopeId,
                EmitterPNMLConstants._TH_invoked+scopeId));
          res[2].add(genArcElement(EmitterPNMLConstants._TH_invoked+scopeId,
                EmitterPNMLConstants._TH_pre+scopeId));
          res[2].add(genArcElement(EmitterPNMLConstants._TH_pre+scopeId,
                EmitterPNMLConstants._TH_ready+scopeId));
          res[2].add(genArcElement(EmitterPNMLConstants._TH_ready+scopeId,
                EmitterPNMLConstants._TH_abstract+scopeId));
          res[2].add(genArcElement(EmitterPNMLConstants._TH_abstract+scopeId,
                EmitterPNMLConstants._TH_finish+scopeId));
          res[2].add(genArcElement(EmitterPNMLConstants._TH_finish+scopeId,
                EmitterPNMLConstants._TH_post+scopeId));
          return res;
         }
      
      /**       
       * MethodName		: genHandler
       * Purpose    	: Generate code for an Event XOR Fault XOR Compensation - Handler.
       * 				  This method is called directly from the BabelEngine.
       * Pre		  	: eventHandlers are direct childs of a scope.
       * 				  just one event message is active at a time. (i.e. no simultaneously active instances). 
       *       			 l[0]!=null AND l[1]!=null        
       * 			 		l[0]	... list of "Activity" Blocks.
       * 					l[1]	... actionList (
       * 									l[1].size()=1 AND l[1] is a String containing
       * 										the name for e_normal for eventHandler
       * 									l[1].size()=1 AND
       *  									l[1].get(0) is a List build according to genCatchList        									        					
       * 									)       
       * 
       * 					info[0]	... Integer, breadth
       * 					info[1]	... Integer, treeDepth
       * 					info[2]	... Integer, conNo
       * 					info[3]	...	String,	 name of the Element (use: for switch/pick)
       * Post		  	:
       * 					Code for a handler has been generated.         
       * @param l
       * @param info
       * @return Block of generated Code.
       */
      public Block genHandler(List[] l,List info)
         {
          Info i_			= new Info(l,info);  
          boolean sjfFlag 	= ((Boolean)this.sjfStack.pop()).booleanValue();
          boolean isEventHandler = (l[1].size()==1 && 
                					l[1].get(0).getClass().getName().compareTo("java.lang.String")==0);
          boolean isFaultHandler = (l[1].size()==1 && 
                                    l[1].get(0).getClass().getName().compareTo("java.util.ArrayList")==0);          
          List[] res 		= new List[7];          
          for (int i=0;i<7;i++)
             res[i] = new ArrayList();  
          // add all the "inner" Blocks.     	       	  
     	  for(int i=0;i<i_.blockList.size();i++)
     	     {     	      
     	      List [] code = ((Block) i_.blockList.get(i)).getCode();
     	      for (int j=0;j<code.length;j++)
     	         res[j].addAll(code[j]);
     	     }
     	  // clear to define new IO-Interface.
     	  res[4].clear();
     	  res[5].clear();     	       	     	 
    	  res[6].clear();
          // TODO Graphical Layout of Handler.                 
          int xpos						= xMiddle + xHandlerOffset;          
          int ypos 						= i_.yposStart - 4*gYIncr[BabelConst.structured];         
          /* get the ready and finish Places of the first and last subActivity  within the Handler.*/
          Block bFirst					= (Block) i_.blockList.get(0);       
          Block bLast					= (Block) i_.blockList.get(i_.blockList.size()-1);
          String readyNameSubActivity 	= bFirst.getReadyPlace().getAttributeValue("id");
          String finishNameSubActivity	= bLast.getFinishPlace().getAttributeValue("id");          
          /* Naming for Handler */
          String toInvokeName			= "toInvoke_"	+ i_.id;                                           
          if (isEventHandler)
             {
              /* EventHandler specific Place */  
              System.out.println(" GENERATE EVENT HANDLER ...");  
              System.out.println(l[1].getClass().getName());
              String transELoopName			= "T_eventLoop"	+ i_.id;  
              String enableName				= "enable_" 	+ i_.id;
              String transEName				= (String) l[1].get(0) + i_.id;   
              Element enablePlace			= this.genPlaceElement(enableName,enableName);
              Element arcEnormal2enable		= this.genArcElement(transEName,enableName);
              Element arcEnable2Enormal 	= this.genArcElement(enableName,transEName);
              Element transELoop			= this.genTransitionElement(transELoopName,transELoopName);
              Element arcFinishSub2ELoop	= this.genArcElement(finishNameSubActivity,transELoopName);
              Element arcELoop2ToInvoke		= this.genArcElement(transELoopName,toInvokeName); 
              Element arcEnormal2readySub	= this.genArcElement(transEName,readyNameSubActivity);              
              Element transE				= this.genTransitionElement(transEName,transEName);                      
              Element arcToInvoke2Enormal	= this.genArcElement(toInvokeName,transEName);
              res[1].add(enablePlace);
              res[2].add(transELoop);
              res[2].add(transE);     
         	  res[3].add(arcEnormal2enable);
         	  res[3].add(arcEnable2Enormal);
         	  res[3].add(arcFinishSub2ELoop);
        	  res[3].add(arcELoop2ToInvoke);
        	  res[3].add(arcEnormal2readySub);        	  	
         	  res[3].add(arcToInvoke2Enormal);
        	  res[4].add(enablePlace);
        	  //bugFix03: Fig.17: arc toContinue to eNormal
        	  res[4].add(transE);
        	  //bugFix 08: Fig 17 blue arc toInvoke Place EH to transInvoked FH
        	  this.eHList_Scope.add(toInvokeName);        	  
             }                 
          else if (isFaultHandler)
             {
              System.out.println(" GENERATE FAULTHANDLER ...");                
              String transInvokedName			= "T_invoked_"	+ i_.id;
              String invokedName 				= "invoked_" 	+ i_.id;
              String t_FHfinishName				= "T_FHFinish_" + i_.id;               
              Element invokedPlace 				= genPlaceElement(invokedName,invokedName);
              Element transInvoked2Ready		= genTransitionElement(transInvokedName,transInvokedName);
              Element transFHFinish				= genTransitionElement(t_FHfinishName,t_FHfinishName);              
              Element arcInvoked2transInvoked	= genArcElement(invokedName,transInvokedName);
              Element arcTransInvoked2Ready		= genArcElement(transInvokedName,readyNameSubActivity);
              Element arcFinish2transFHFinish	= genArcElement(finishNameSubActivity,t_FHfinishName);
              // graphical information
              EmitterPNMLGraphics.addGraphicsElement(invokedPlace,xpos,ypos);
              res[1].add(invokedPlace);
              res[2].add(transInvoked2Ready); 
              res[2].add(transFHFinish);                           
              res[3].add(arcInvoked2transInvoked);
              res[3].add(arcTransInvoked2Ready);
              res[3].add(arcFinish2transFHFinish);
              res[4].add(invokedPlace);
              res[4].add(transFHFinish);
              res[4].add(transInvoked2Ready);
              // bugFix08: 
              this.fHList_Scope.add(transInvokedName);
             }
          else
             {
              // CompensationHandler
              System.out.println("GENERATE COMPENSATION-HANDLER...");  
              System.out.println(readyNameSubActivity);
              System.out.println(finishNameSubActivity);              
              if ( hMCompHandler.get((String) l[1].get(0))==null )
                    System.out.println("ERROR. No corresponding Compensation Handler found.");
              else
                 {
                  // List of elements of the compensate basic activity                 
                  List cList 			 = (List) hMCompHandler.get((String) l[1].get(0));
                  String compName 		 = ((Element) cList.get(0)).getAttributeValue("id");
                  String compPostName	 = ((Element) cList.get(1)).getAttributeValue("id");
                  // Comp-specific transitions
                  String transInvokeName = "invoke_" 	+ i_.id;
                  String transNoOp		 = "nop_"		+ i_.id;
                  String transFinish	 = "CHFinish_"	+ i_.id;
                  // comp-specific place.
                  String posFinish		 = "CHF_"		+ i_.id;
                 
                  // Generate Elements. Later: add graphical information
                  Element tInvoke 		 = genTransitionElement(transInvokeName,transInvokeName);
                  Element tNoOp	 		 = genTransitionElement(transNoOp,transNoOp);
                  Element tFinish		 = genTransitionElement(transFinish,transFinish);
                  Element pCF	 		 = genPlaceElement(posFinish,posFinish);
                  // add place and transitions into res - Array.
                  res[1].add(pCF);
                  res[2].add(tInvoke);
                  res[2].add(tNoOp);
                  res[2].add(tFinish);
                  // add the arcs 
                  res[3].add(genArcElement(compName,toInvokeName));
                  res[3].add(genArcElement(posFinish,compPostName));
                  res[3].add(genArcElement(toInvokeName,transNoOp));
                  res[3].add(genArcElement(toInvokeName,transInvokeName));
                  res[3].add(genArcElement(transInvokeName,readyNameSubActivity));
                  res[3].add(genArcElement(finishNameSubActivity,transFinish));
                  res[3].add(genArcElement(transFinish,posFinish));
                  res[3].add(genArcElement(transNoOp,posFinish));
                  res[4].add(tNoOp);
                  res[4].add(tInvoke);
                 }                                 
             }
          /* general code for event,fault Handler */
          Element toInvokePlace			= this.genPlaceElement(toInvokeName,toInvokeName);                                 
     	  //add all Places,Transitions,Arcs
     	  //res[0].add(this.genAnnotationBlock(blockName,xStart,new Double(yposStart).intValue(),100,18));     	
     	  res[1].add(toInvokePlace);     	       	       	      	      	 
     	  res[4].add(toInvokePlace);     	       	 
     	  res[6].add(i_.blockName);
     	  
     	  Block resBlock;
     	  if (isEventHandler)     	    
     	     resBlock   = new Block(res,4,BabelConst.eventHandler);     	     
     	  else if (isFaultHandler) 
     	     resBlock = new Block(res,4,BabelConst.faultHandler);
     	  else
     	     resBlock = new Block(res,4,BabelConst.compHandler);
    	  return resBlock;
         }
      
      /**
       * MethodName :	genSequence
       * Purpose	:	generate PNML-Code for a sequence of "Block" Tasks.
       * 				
       * 				
       * PRE		:	
       * 				l[0]	... blockList
       * 				l[1]	... controlLinks
       * 				((Block) blockList.get(i)).getInput().get(0) ... is the ready Block Place 
       * 				((Block) blockList.get(i)).getInput().get(1) ... is the skip Block Place
       * 				List [] eList is a list of elements 
       * 				AND all (sub)activities are BasicActivities
       *
       * POST		:	List[]	of Elements 
       * 				AND the resulting List Arrray res contains the content of eList and the Sequence Code
       * 			where List[] is :
       * 					List[0]		...	list of labels (annotations)
       *  				 	List[1] 	... list of places Elements
       * 					List[2]		...	list of transition Elements
       * 					List[3]		...	list of arc Elements
       *					List[4]		... ioBlock input
       * 					List[5]		... ioBlock output
       * 					List[6]		... BlockName
       * 			
       * 
       * @param		List[],	Array of Lists of Elements
       * 
       * 
       * @return	Block
       */
      public Block genSequence(List[] l,List info)
         {          
          int breadth,treeDepth,conNo;
          boolean sjfFlag = ((Boolean)this.sjfStack.pop()).booleanValue();
          String structureName;
          List blockList = l[0];
          breadth 	= ((Integer) info.get(0)).intValue();
          treeDepth	= ((Integer) info.get(1)).intValue();
          conNo		= ((Integer) info.get(2)).intValue();
          structureName = (String) info.get(3);
          
          List[] res = new List[7];
          int noBlock		= blockList.size();
          int pIndex=1,tIndex = 2,aIndex=3; /* places,transition,arc Index */
          int blockNameIndex=6, blockIOIndex=4; 
          //TODO get correct xpos and ypos
          // start position of first Block
          Block bFinish = (Block) blockList.get(noBlock-1);
          double [] blockReadyPos		= ((Block) blockList.get(0)).getPosReadyPlace();          
          // finish position of last Block
          double [] blockFinishPos		= bFinish.getPosFinishPlace();
          int 	yposStartBlock = new Double(blockReadyPos[1]).intValue();
          double xpos=xMiddle,ypos;
          
          if ( ((Block)blockList.get(0)).getBlockType()==BabelConst.structured)
          	ypos  = yposStartBlock - 150;
          else
             ypos = yposStartBlock - 290; // more space to basic activity because of annotation box                    	             	             
          int yposStart = new Double(ypos).intValue();
          for (int i=0;i<7;i++)
             res[i] = new ArrayList();                                    
          // generate Code for the SEQ.
          structureName			= BPEL.getAbbreviationForActivity(structureName);
          String blockName 		= structureName + treeDepth + conNo;          
          // places 
          Element readyPlace   	= genPlaceElement("r_" + blockName, "Pr_" + blockName);
          Element seqStartPlace	= genPlaceElement("s_" + blockName, "Ps_" + blockName );
          Element seqEndPlace	= genPlaceElement("c_" + blockName, "Pc_" + blockName );
          Element finishPlace 	= genPlaceElement("f_" + blockName, "Pf_"+ blockName);          
          // transitions
          Element preTrans		= genTransitionElement("T_pre" + blockName,"pre_" + blockName);
          Element postTrans		= genTransitionElement("T_post" + blockName,"post_" + blockName);          
          //arcs          
          Element ready2Pre 	= genArcElement("Pr_" + blockName,"T_pre" + blockName);
          Element pre2s			= genArcElement("T_pre" + blockName,"Ps_" + blockName);
          Element s2sFlow		= genArcElement("Ps_" + blockName,"T_sFlow" + blockName);       
          Element c2post		= genArcElement("Pc_"+ blockName,"T_post" + blockName);
          Element post2f		= genArcElement("T_post" + blockName,"Pf_"+ blockName);
          
          //graphics          
          EmitterPNMLGraphics.addGraphicsElement(readyPlace,xpos,ypos);
          ypos+=gYIncr[BabelConst.structured];
          EmitterPNMLGraphics.addGraphicsElement(preTrans,xpos,ypos);
          EmitterPNMLGraphics.addRotationElement(preTrans,90);
          ypos+=gYIncr[BabelConst.structured];
          EmitterPNMLGraphics.addGraphicsElement(seqStartPlace,xpos,ypos);
          
          res[pIndex].add(readyPlace);
          res[pIndex].add(seqStartPlace);
          
          res[tIndex].add(preTrans);
          res[aIndex].add(ready2Pre);
          res[aIndex].add(pre2s);
          
          Element seqT,seqP2T,seqT2P;
          Block bprev=null;
          
          Element prevEndPlace 	= seqStartPlace;
          String prevEndPlaceId	= prevEndPlace.getAttributeValue("id");
          String endPlaceSeq 	= seqEndPlace.getAttributeValue("id"); 
          // blockWise is generalisation of this.
          for (int i=0;i<blockList.size();i++)
             {                        
             	Block b = (Block) blockList.get(i);
             	Element currStartPlace	= (Element) b.getInput().get(0);	           
             	Element currEndPlace   	= (Element) b.getOutput().get(0);	
             	Attribute startPId 		= currStartPlace.getAttribute("id");
             	Attribute endPId		= currEndPlace.getAttribute("id");				
             	System.out.println(" Activity:" 	+ (String) ((Block) blockList.get(i)).getName());
             	System.out.println(" StartPlaceId:" + startPId.getValue());
             	System.out.println(" EndPlaceId:" 	+ endPId.getValue());             	
             	String currStartPlaceId = startPId.getValue();             	
             	// build Transition for Sequence.
             	String tName = "T" + blockName + "_" + i;
             	seqT =genTransitionElement(tName,tName);
             	if (i==0)
             	   ypos+=gYIncr[BabelConst.structured];
             	else
             	   {
             	    double [] prevFinish = bprev.getPosFinishPlace();
             	    ypos = prevFinish[1];
             	    if (bprev.getBlockType()==BabelConst.structured)
             	       ypos+=gYIncr[BabelConst.structured];
             	    else
             	       ypos+=150;             	    
             	   }
             	EmitterPNMLGraphics.addGraphicsElement(seqT,xpos,ypos);
             	EmitterPNMLGraphics.addRotationElement(seqT,90);
             	res[tIndex].add(seqT);
             	// connect prevEndPlace to Transition
             	seqP2T = genArcElement(prevEndPlaceId,tName);
             	if (i>0 && bprev.getBlockType()==BabelConst.basic)
             	   addSeqArcGraphics(seqP2T,xpos,ypos,0);
             	res[aIndex].add(seqP2T);
             	// connect Transition to currStartPlace 
             	seqT2P = genArcElement(tName,currStartPlaceId);
             	if (b.getBlockType()==BabelConst.basic)
             	   addSeqArcGraphics(seqT2P,xpos,ypos,1);
             	res[aIndex].add(seqT2P);            
             	System.out.println("TName:" + tName);
             	System.out.println("PrevPlace" + prevEndPlaceId);
             	System.out.println("EndPlace" + currStartPlaceId);
             	// update for next Sequence Block.
             	prevEndPlace = currEndPlace;         
             	prevEndPlaceId=prevEndPlace.getAttributeValue("id");
             	bprev = b;
             }
          // the last transition to complete.
          String tName = "T"+blockName+"_"+ blockList.size();
          seqT 	 = genTransitionElement(tName,tName);
          ypos   = blockFinishPos[1] + gYIncr[bFinish.getBlockType()];
       	  EmitterPNMLGraphics.addGraphicsElement(seqT,xpos,ypos);
       	  EmitterPNMLGraphics.addRotationElement(seqT,90);
          seqP2T = genArcElement(prevEndPlaceId,tName);
          addSeqArcGraphics(seqP2T,xpos,ypos,0);
          seqT2P = genArcElement(tName,endPlaceSeq);          
          res[tIndex].add(seqT);
          res[aIndex].add(seqP2T);
          res[aIndex].add(seqT2P);                
          ypos+=gYIncr[BabelConst.structured];
          EmitterPNMLGraphics.addGraphicsElement(seqEndPlace,xpos,ypos);
          res[pIndex].add(seqEndPlace);
          ypos+=gYIncr[BabelConst.structured];
          EmitterPNMLGraphics.addGraphicsElement(postTrans,xpos,ypos);
          EmitterPNMLGraphics.addRotationElement(postTrans,90);
          ypos+=gYIncr[BabelConst.structured];
          EmitterPNMLGraphics.addGraphicsElement(finishPlace,xpos,ypos);          
          res[pIndex].add(finishPlace);
          res[tIndex].add(postTrans);
          res[aIndex].add(c2post);
          res[aIndex].add(post2f);
                	  
     	  // add all the "inner" Blocks.     	       	  
     	  for(int i=0;i<blockList.size();i++)
     	     {
     	      List [] code = ((Block) blockList.get(i)).getCode();
     	      for (int j=0;j<code.length;j++)
     	         res[j].addAll(code[j]);
     	     }     	        	 
     	  //update the block information.
     	  res[4].clear();
     	  res[4].add(readyPlace);
     	  //res[4].add(seqStartPlace);
     	  res[5].clear();
     	  res[5].add(finishPlace);
     	  //res[5].add(seqEndPlace);
     	  
     	  res[6].clear();
     	  res[6].add(blockName);
     	       	
     	  //Skip Path
          List[] skipCode=this.genSkipPathSA(blockList,blockName,yposStart,new Double(ypos).intValue(),
                							 treeDepth,conNo);
          // toSkipPlace and skipTrans of the Skip-Path of the structured activity itself.
          String toSkipPlace		= ((Element) skipCode[1].get(0)).getAttributeValue("id");
          String skipStartTrans		= ((Element) skipCode[2].get(0)).getAttributeValue("id");
          String skipEndTrans		= ((Element) skipCode[2].get(1)).getAttributeValue("id");
          List [] skipPlaces 		= getSkipPlaces(blockList);
          //List toSkipPlacesList	= new ArrayList(); 	// List of toSkip places of subactivities of this structured activity.
          //List skippedPlacesList	= new ArrayList();	// List of skipped places of subactivities of this structured activity.
          // add controlLinks to the structured activity switch
          if (!l[1].isEmpty())
             addControlLinks(res,l[1],sjfFlag,treeDepth,conNo,
                   			 "T_pre"+blockName,"T_post"+blockName,readyPlace.getAttributeValue("id"),
                   			 finishPlace.getAttributeValue("id"),
                   			 toSkipPlace,skipStartTrans,skipEndTrans,skipPlaces[0],skipPlaces[1]);          
          Block resBlock = new Block(res,4,BabelConst.structured);          
          // add the skip path
          resBlock.addListArray(skipCode);         
          addInfo(postTrans,resBlock);
     	  return resBlock;
         }
      /*
       *  (non-Javadoc)
       * @see model.backEnd.Emitter#genCaseName(org.jdom.Element, int, int)
       */      
      public String genCaseName(Element e,int breadth,int treeDepth)
         {          
          if (StringTools.isInStringArray(e.getName(),BPEL.events))
             {
              String s = BPEL.getAbbreviationForActivity(e.getName())+treeDepth+breadth;
              s = s + getActivityInfo(e);
              return s;
             }
          else            
             return BPEL.getAbbreviationForActivity(e.getName())+treeDepth+breadth;
         }
                  
      /*
       * PRE: e is a <catch> Element
       *  (non-Javadoc)
       * @see model.backEnd.Emitter#genCatchList(org.jdom.Element, int, int)
       */
      public List genCatchList(Element e,int breadth,int treeDepth)
         {
          List catchList = new ArrayList();                    
          String s = BPEL.getAbbreviationForActivity(e.getName())+treeDepth+breadth;
          String catchAttr = "{" +  this.getAttributeInfo(e,BPEL.faultAttr,",",true) + "}";                                         
          catchList.add(s);           
          // needed to select the correct faultHandler (see BPEL-spec).
          catchList.add(replaceVarWithType(catchAttr,e));
          
          System.out.println("CATCH : " + catchList.get(1)); 
          System.out.println("catchList_size: " + catchList.size());
          
          return catchList;
         }
      /**
       * 
       * MethodName		: replaceVarWithType
       * Purpose    	: replace the entry for <faultVariable> with its type.
       * 					2 do:
       * 					- generalise
       * 					- move to other class  
       * Pre		  	:
       * Post		  	: 
       * 
       * @param attrList
       * @param e
       * @return
       */
      private String replaceVarWithType(String attrList, Element e)
         {
          String varType=null; // = "noTypeFound";
          String varName;
          // lookup the type of the variable in the Babel.idTable.
          if ( (varName=e.getAttributeValue(BPEL.faultVariable))!=null )             
             varType=(String) Babel.idTable.getInfo(varName);
          System.out.println("VARTYPE:" + varType);  
          
          return  attrList.replaceAll("<" + BPEL.faultVariable + ">[^,}]*",
               "<" + BPEL.faultType + ">" + varType);          
         }
            
      /**
       * MethodName	:	genSwitch
       * Purpose	:	
       * 	
       * PRE		: 	l[0]	... list of Strings naming the condition // onEvent,onMessage,...
       * 			 	l[1]	... list of "Activity" Blocks.
       * 				l[2]	... list of  Sources or Targets Element.
       * 				info[0]	... Integer, breadth
       * 				info[1]	... Integer, treeDepth
       * 				info[2]	... Integer, conNo
       * 				info[3]	...	String,	 name of the Element (use: for switch/pick)
       * POST		:
       * 
       *       
       */
      public Block genSwitch(List [] l,List info)
         {                       
          // get Info from info List.
          int breadth,treeDepth,conNo;
          boolean sjfFlag = ((Boolean)this.sjfStack.pop()).booleanValue();
          String structureName;
          breadth 	= ((Integer) info.get(0)).intValue();
          treeDepth	= ((Integer) info.get(1)).intValue();
          conNo		= ((Integer) info.get(2)).intValue();
          structureName = BPEL.getAbbreviationForActivity((String) info.get(3));
             
          Block resBlock	= null;               
          List condList 	= l[0];
          List blockList 	= l[1];
          int noBlock		= condList.size();
          Element[] condT	= new Element[noBlock];
          Element[] condTf	= new Element[noBlock];
          Element[] s2Cond	= new Element[noBlock];
          Element[] cond2B	= new Element[noBlock];
          Element[] b2Condf	= new Element[noBlock];          
          String blockName 	= structureName + treeDepth + breadth;
          int codeLength	= ((Block) blockList.get(0)).getCodeLength();
          List[] res		= new List[codeLength+3];
          String [] blockReadyId 	= new String[noBlock];
          String [] blockToSkipId 	= new String[noBlock];
          String [] blockEndId		= new String[noBlock];
          String [] blockSkippedId	= new String[noBlock];
          // positioning stuff
          // 18.04 changed the initial "local" xStart from 60 to xStart + 30
          
          int xStart=this.xStart+30,xIncr;
          
          if (noBlock<=1) 
             xIncr=480;
          else
             xIncr=480/(noBlock-1);
          int [] yIncr = new int[2];
          yIncr[BabelConst.basic]=150;
          yIncr[BabelConst.structured]=50;          
          double xposConst=xMiddle,xpos,ypos;
          // start position of first Block
          Block bStart 	= (Block) blockList.get(0);
          Block bEnd	= (Block) blockList.get(noBlock-1);
          double [] blockReadyPos		= bStart.getPosReadyPlace();
          // finish position of last Block
          double [] blockFinishPos		= bEnd.getPosFinishPlace();
          int yposStart		= new Double(blockReadyPos[1] 	- 6*this.gYIncr[BabelConst.structured]).intValue();
          int yposEndBlock	= new Double(blockFinishPos[1] + yIncr[bEnd.getBlockType()]).intValue();                                            
          for(int i=0;i<codeLength+3;i++)
             res[i] = new ArrayList();                      
          for(int i=0;i<noBlock;i++)
             {
              Element blockReady	= (Element) ((Block) blockList.get(i)).getInput().get(0);
	          Element blockToSkip	= (Element) ((Block) blockList.get(i)).getInput().get(1);
	      	  Element blockEnd   	= (Element) ((Block) blockList.get(i)).getOutput().get(0);
	      	  Element blockSkipped	= (Element) ((Block) blockList.get(i)).getOutput().get(1);
	      	  blockReadyId[i]		= (blockReady.getAttribute("id")).getValue();
	      	  blockToSkipId[i]		= (blockToSkip.getAttribute("id")).getValue();
	      	  blockEndId[i]			= (blockEnd.getAttribute("id")).getValue();
	      	  blockSkippedId[i] 	= (blockSkipped.getAttribute("id")).getValue();	      	 
             }          
          // build the switch "ingredients".
          Element swhReady 	= this.genPlaceElement("r_"+blockName,"Pr_"+blockName);
          EmitterPNMLGraphics.addGraphicsElement(swhReady,xposConst,yposStart);
          Element swhTpre	= this.genTransitionElement("T_pre"+blockName,"pre_"+blockName);
          ypos				= yposStart + yIncr[1];
          EmitterPNMLGraphics.addGraphicsElement(swhTpre,xposConst,ypos);
          EmitterPNMLGraphics.addRotationElement(swhTpre,90);
          Element swhFinish	= this.genPlaceElement("f_"+blockName,"Pf_"+blockName);
          Element swhStart  = this.genPlaceElement("s_"+blockName,"Ps_"+blockName);
          ypos		 		= ypos + yIncr[1];
          EmitterPNMLGraphics.addGraphicsElement(swhStart,xposConst,ypos);
          Element swhEnd	= this.genPlaceElement("c_"+blockName,"Pc_"+blockName);                    
          Element swhTpost	= this.genTransitionElement("T_post"+blockName,"post_"+blockName);          
          // build arcs
          Element r2Pre		= this.genArcElement("Pr_"+blockName,"T_pre"+blockName);
          Element pre2s		= this.genArcElement("T_pre"+blockName,"Ps_"+blockName);
          Element c2Post 	= this.genArcElement("Pc_"+blockName,"T_post"+blockName);
          Element post2f	= this.genArcElement("T_post"+blockName,"Pf_"+blockName);                
          // build the code 
          res[0].add(this.genAnnotationBlock(blockName,xStart,new Double(yposStart).intValue(),100,18));                
          res[1].add(swhReady);
          res[1].add(swhStart);
          res[2].add(swhTpre);
          res[3].add(r2Pre);
          res[3].add(pre2s);
          xpos = xStart;
          ypos = ypos + yIncr[1];
          for(int i=0;i<noBlock;i++)
             {
              String condName = (String) condList.get(i);	      	 
              String tName 	= "s_" + condName + "_" + treeDepth + breadth;  
              String tNamef = "f_" + condName + "_" + treeDepth + breadth;
              // condition Transition
              condT[i] = this.genTransitionElement(tName,tName);
              EmitterPNMLGraphics.addGraphicsElement(condT[i],xpos,ypos);
              EmitterPNMLGraphics.addRotationElement(condT[i],90);             
              res[2].add(condT[i]);
              //arcs into the condition Transition
              s2Cond[i] = this.genArcElement("Ps_"+blockName,tName);
              res[3].add(s2Cond[i]);
              // arcs from the condition Transition into the block 
              // into ready
              cond2B[i]	= this.genArcElement(tName,blockReadyId[i]);
              res[3].add(cond2B[i]);
              // into skip                                          
              for (int j=0;j<i;j++)
                 {
                  cond2B[j] = this.genArcElement(tName,blockToSkipId[j]);
                  res[3].add(cond2B[j]);
                 }
              System.out.println("BlockList.size");
              for (int j=i+1;j<blockToSkipId.length;j++) // before: blockList.size();j++)
                 {                  
                  System.out.println("BlockToSkipId: " + blockToSkipId.length);
                  cond2B[j] = this.genArcElement(tName,blockToSkipId[j]);
                  res[3].add(cond2B[j]);
                 }
              // add all elements of the Block
              List [] code = ((Block) blockList.get(i)).getCode();
              for(int k=0;k<codeLength;k++)                 
                 res[k].addAll(code[k]);
               // now transition after end of Block              
              condTf[i] = this.genTransitionElement(tNamef,tNamef);
              EmitterPNMLGraphics.addGraphicsElement(condTf[i],xpos,yposEndBlock);
              EmitterPNMLGraphics.addRotationElement(condTf[i],90);
              res[2].add(condTf[i]);
              // arcs block i to condTf[i]
              b2Condf[i] = this.genArcElement(blockEndId[i],tNamef);
              res[3].add(b2Condf[i]);
              // arcs for  blockSkippedId[[0,i-1],[i+1,noBlock]] into condTf[i]
              for(int j=0;j<i;j++)
                  {
                   b2Condf[j] = this.genArcElement(blockSkippedId[j],tNamef);
                   res[3].add(b2Condf[j]);
                  }
              for(int j=i+1;j<noBlock;j++)
                  {
                   b2Condf[j] = this.genArcElement(blockSkippedId[j],tNamef);
                   res[3].add(b2Condf[j]);
                  }                           
              // finally all cond transitions into the complete Place of the switch
              res[3].add(this.genArcElement(tNamef,"Pc_"+blockName));
              xpos = xpos + xIncr;
             }
          ypos = yposEndBlock + yIncr[1]; // last is always basic activity.
          EmitterPNMLGraphics.addGraphicsElement(swhEnd,xposConst,ypos);
          res[1].add(swhEnd);          
          res[3].add(c2Post);
          ypos = ypos + yIncr[1];
          EmitterPNMLGraphics.addGraphicsElement(swhTpost,xposConst,ypos);
          EmitterPNMLGraphics.addRotationElement(swhTpost,90);
          res[2].add(swhTpost);
          res[3].add(post2f);
          ypos = ypos + yIncr[1];
          EmitterPNMLGraphics.addGraphicsElement(swhFinish,xposConst,ypos);
          res[1].add(swhFinish);
          // ioBlock
          res[4].add(swhReady);
          res[5].add(swhFinish);
          
          res[6].add(blockName);
           
          //Skip Path
          List[] skipCode=this.genSkipPathSA(blockList,blockName,yposStart,
                                             new Double(ypos).intValue(),treeDepth,conNo);
          // toSkipPlace and skipTrans of the Skip-Path of the structured activity itself.
          String toSkipPlace		= ((Element) skipCode[1].get(0)).getAttributeValue("id");
          String skipStartTrans		= ((Element) skipCode[2].get(0)).getAttributeValue("id");
          String skipEndTrans		= ((Element) skipCode[2].get(1)).getAttributeValue("id");
          List [] skipPlaces 		= getSkipPlaces(blockList);
          //List toSkipPlacesList		= new ArrayList(); 	// List of toSkip places of subactivities of this structured activity.
          //List skippedPlacesList	= new ArrayList();	// List of skipped places of subactivities of this structured activity.
          // add controlLinks to the structured activity switch
          if (!l[2].isEmpty())
             addControlLinks(res,l[2],sjfFlag,treeDepth,conNo,
                   			 "T_pre"+blockName,"T_post"+blockName,swhReady.getAttributeValue("id"),
                   			 swhFinish.getAttributeValue("id"),
                   			 toSkipPlace,skipStartTrans,skipEndTrans,skipPlaces[0],skipPlaces[1]);          
          resBlock = new Block(res,4,BabelConst.structured);          
          // add the skip path
          resBlock.addListArray(skipCode);
         
          // Graphical extra: increase yOffset to provide enough space for the following blocks.
          // This is due to the 2 final places and transitions of a switch Block.                                      
          yOffset+=200;          
          this.addInfo(swhTpost,resBlock);
                         
          return resBlock;
         }
      
      /**
       * 
       * MethodName		: addControlLinks
       * Purpose    	: addControlLinks to a non-sequence structured Activity. 
       * Pre		  	:
       * Post		  	: 
       * 
       * @param res
       * @param controlLinks
       * @param sjfFlag
       * @param treeDepth
       * @param conNo
       * @param postTrans
       * @param readyPlace
       * @param finishPlace
       * @param toSkipPlace
       * @param toSkipPlaces
       * @param skippedPlaces
       */
      private void addControlLinks(List[] res,List controlLinks,boolean sjfFlag, int treeDepth,int conNo,
                                   String preTrans,String postTrans,String readyPlace,String finishPlace,
                                   String toSkipPlace,String skipStartTrans, String skipEndTrans,
                                   List toSkipPlacesList,List skippedPlacesList)
         {
          List[] sourceLinks	= null;
          List[] targetLinks	= null; 
          List[] sjfPath    	= null;
          String taskId 		= "" + treeDepth  + conNo;
          for (int i=0;i<controlLinks.size();i++)
             {
              Element e = (Element) controlLinks.get(i);             
              if ( e.getName().compareTo(BPEL.controlLinks[0])==0 ) // sources
                 sourceLinks = genSourceLinks(e,treeDepth,conNo,postTrans,skipEndTrans);
              else if (BPEL.hasJoinCondition(e)) // old: eChild.getName().compareTo(BPEL.controlLinks[1])==0) //targets
                 {
              	  targetLinks = genTargetLinks(e,treeDepth,conNo,preTrans,
              	        					   readyPlace,finishPlace,toSkipPlace,skipStartTrans);  
              	  if ( sjfFlag )
              	     {              	      
              	      String jctPlace 	= (String) targetLinks[3].get(0);
              	      String jcfPlace 	= (String) targetLinks[3].get(1);
              	      sjfPath 			= genSjfPath(jctPlace,jcfPlace,readyPlace,finishPlace,
              	            			  			 toSkipPlacesList,skippedPlacesList,treeDepth,conNo);              	                  	    
              	     }
                 }
             }
                             
         // attach the sourceLinks
         if (sourceLinks!=null)
            {
             addGraphicsSourceLinks(sourceLinks);
             for (int i=0;i<3;i++)
                res[i].addAll(sourceLinks[i]);
             if ( sjfPath!=null ) // i.e.: sources and targets
                {
                 // add linkNames to the HashMap for sjf - transition. 
                 // the name of the sjf-transition is returned via sjfPath[3] 
                 for (int i=0;i<sourceLinks[3].size();i++)
                    this.hMSjf.put((String) sourceLinks[3].get(i),sjfPath[3].get(0));
                }
            }
         // attach the targetLinks
         if (targetLinks!=null)
            {
             addGraphicsTargetLinks(targetLinks);
             for (int i=0;i<3;i++)
                res[i].addAll(targetLinks[i]);
             // add the sjf-Path
             if ( sjfPath!=null )
                for(int i=0;i<3;i++)
                   res[i].addAll(sjfPath[i]);
            }            
         }
      
      /**
       * MethodName : genFlow
       * 
       * Purpose	: generate code for Flow constructs.
       * 
       * PRE		: 	
       * 			 	l[0]	... list of Blocks.
       * 				l[1]	... list of  Sources or Targets Element.
       * 
       * 				info[0]	... Integer, breadth
       * 				info[1]	... Integer, treeDepth
       * 				info[2]	... Integer, conNo
       * 				info[3]	...	String,	 name of the Element (use: for switch/pick)
       * POST		:
       */
      public Block genFlow(List[] l, List info)
         {
          // get Info from info List.
          // most of the following is similar in: SEQ,SWITCH => 
          // TODO Generalise 
          int breadth,treeDepth,conNo;
          boolean sjfFlag = ((Boolean)this.sjfStack.pop()).booleanValue();
          String structureName;
          breadth 	= ((Integer) info.get(0)).intValue();
          treeDepth	= ((Integer) info.get(1)).intValue();
          conNo		= ((Integer) info.get(2)).intValue();
          structureName = (String) info.get(3);
                     
          List blockList = l[0];          
          Block resBlock	= null;
          int noBlock		= blockList.size();
          int pIndex=1,tIndex = 2,aIndex=3; /* laces,transition,arc Index */
          int blockNameIndex=6, blockIOIndex=4; 
          //TODO get correct xpos and ypos
          // start position of first Block
          Block bFinish = (Block) blockList.get(noBlock-1);
          double [] blockReadyPos		= ((Block) blockList.get(0)).getPosReadyPlace();          
          // finish position of last Block
          double [] blockFinishPos		= bFinish.getPosFinishPlace();
          double yposStartBlock = blockReadyPos[1];
          double yposEndBlock   = blockFinishPos[1];
          double xpos=xMiddle,ypos;
          
          int yposStart	= new Double(yposStartBlock - 4*this.gYIncr[BabelConst.structured]).intValue();
          int yposEnd 	= new Double(yposEndBlock + 4*gYIncr[BabelConst.structured]).intValue();
          
          ypos= yposStart; // - 4*50;
          int codeLength = ((Block) blockList.get(0)).getCodeLength();
          int resLength	 = codeLength +3;
          List[] res	 = new List[resLength];
          
          for (int i=0;i<resLength;i++)
             res[i] = new ArrayList();                           
         
          // build all elements
          String blockName 	= structureName + treeDepth + breadth;
          
          Element readyPlace   	= genPlaceElement("r_" + blockName, "Pr_" + blockName);
          Element startPlace	= genPlaceElement("s_" + blockName, "Ps_" + blockName);
          Element completePlace	= genPlaceElement("c_" + blockName, "Pc_" + blockName);
          Element finishPlace 	= genPlaceElement("f_" + blockName, "Pf_"+ blockName);
          
                    
          Element preTrans		= genTransitionElement("T_pre" + blockName,"pre_" + blockName);
          Element sFlowTrans 	= genTransitionElement("T_sFlow" + blockName,"s_" + blockName);
          Element fFlowTrans	= genTransitionElement("T_fFlow" + blockName,"f_" + blockName);
          Element postTrans		= genTransitionElement("T_post" + blockName,"post_" + blockName);
          
          
          EmitterPNMLGraphics.addGraphicsElement(readyPlace,xpos,yposStart);
          EmitterPNMLGraphics.addGraphicsElement(preTrans,xpos,yposStart + this.gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(preTrans,90);
          EmitterPNMLGraphics.addGraphicsElement(startPlace,xpos,yposStart + 2*gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addGraphicsElement(sFlowTrans,xposMax,yposStart + 3*gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(sFlowTrans,90);
          EmitterPNMLGraphics.addGraphicsElement(fFlowTrans,xposMax,yposEndBlock + gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(fFlowTrans,90);
          EmitterPNMLGraphics.addGraphicsElement(completePlace,xpos,yposEndBlock + 2*gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addGraphicsElement(postTrans,xpos,yposEndBlock + 3*gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(postTrans,90);
          EmitterPNMLGraphics.addGraphicsElement(finishPlace,xpos,yposEndBlock + 4*gYIncr[BabelConst.structured]);
          
          // generate all the arcs
          // 1. wrapping arcs 
          Element ready2Pre 	= genArcElement("Pr_" + blockName,"T_pre" + blockName);
          Element pre2s			= genArcElement("T_pre" + blockName,"Ps_" + blockName);
          Element s2sFlow		= genArcElement("Ps_" + blockName,"T_sFlow" + blockName);
          Element fFlow2c		= genArcElement("T_fFlow" + blockName,"Pc_"+ blockName);
          Element c2post		= genArcElement("Pc_"+ blockName,"T_post" + blockName);
          Element post2f		= genArcElement("T_post" + blockName,"Pf_"+ blockName);
          // 2. internal arcs
          Element [] toBlockArc 	= new Element[noBlock];
          Element [] fromBlockArc	= new Element[noBlock];
          // add wrapping code
          res[0].add(this.genAnnotationBlock(blockName,xStart,new Double(yposStart).intValue(),100,18));  
          res[1].add(readyPlace);
          res[1].add(startPlace);
          res[2].add(preTrans);
          res[2].add(sFlowTrans);
          res[3].add(ready2Pre);
          res[3].add(pre2s);
          res[3].add(s2sFlow);
          for(int i=0;i<noBlock;i++)
             {
              Element blockReady 	= (Element) ((Block)blockList.get(i)).getInput().get(0);
              Element blockFinish	= (Element) ((Block) blockList.get(i)).getOutput().get(0);
              toBlockArc[i] 		= genArcElement("T_sFlow" + blockName,blockReady.getAttributeValue("id"));
              fromBlockArc[i]		= genArcElement(blockFinish.getAttributeValue("id"),"T_fFlow" + blockName);
              res[3].add(toBlockArc[i]);
              List [] code = ((Block) blockList.get(i)).getCode();
              for(int k=0;k<codeLength;k++)                 
                 res[k].addAll(code[k]);
              res[3].add(fromBlockArc[i]);
             }
          res[1].add(completePlace);
          res[1].add(finishPlace);
          res[2].add(fFlowTrans);
          res[2].add(postTrans);
          res[3].add(fFlow2c);
          res[3].add(c2post);
          res[3].add(post2f);
          //ioBlock
          res[4].add(readyPlace);
          res[5].add(finishPlace);
          // name of the block
          res[6].add(blockName);                             
          //        Skip Path
          // List[] skipCode=this.genSkipPathSA(blockList,blockName,yposStart,
          //                                   new Double(ypos).intValue(),treeDepth,breadth);
          List[] skipCode=this.genSkipPathSA(blockList,blockName,yposStart,yposEnd,treeDepth,conNo);
          // skipCode == null => topLevel activity.
          if (skipCode!=null)
             {
	          // toSkipPlace and skipTrans of the Skip-Path of the structured activity itself.
	          String toSkipPlace		= ((Element) skipCode[1].get(0)).getAttributeValue("id");
	          String skipStartTrans		= ((Element) skipCode[2].get(0)).getAttributeValue("id");
	          String skipEndTrans		= ((Element) skipCode[2].get(1)).getAttributeValue("id");
	          List [] skipPlaces 		= getSkipPlaces(blockList);
	          //List toSkipPlacesList		= new ArrayList(); 	// List of toSkip places of subactivities of this structured activity.
	          //List skippedPlacesList	= new ArrayList();	// List of skipped places of subactivities of this structured activity.
	          // add controlLinks to the structured activity switch
	          if (!l[1].isEmpty())
	             addControlLinks(res,l[1],sjfFlag,treeDepth,conNo,
	                   			 "T_pre"+blockName,"T_post"+blockName,readyPlace.getAttributeValue("id"),
	                   			 finishPlace.getAttributeValue("id"),
	                   			 toSkipPlace,skipStartTrans,skipEndTrans,skipPlaces[0],skipPlaces[1]);  
             }          
          resBlock = new Block(res,4,BabelConst.structured);          
          // add the skip path
          resBlock.addListArray(skipCode);
          
          /* old:
          
          resBlock = new Block(res,4,BabelConst.structured);          
          // add the skip path
          resBlock.addListArray(this.genSkipPathSA(blockList,blockName,yposStart,yposEnd,treeDepth,breadth));
          */          
          // Graphical extra: increase yOffset to provide enough space for the following blocks.
          // This is due to the 2 final places and transitions of a flow Block.          
          yOffset+=200;
          addInfo(postTrans,resBlock);
          
          return resBlock;
         }           
       
     /**
      * 
      * MethodName		: genBlockWrappingElements
      * Purpose    		: generate the place,transitions and arcs for the (ready,start,complete,finish) structure. 
      * Pre		  		: y is a 2D int array with:
      * 					 y[0] = starting y pos for this block
      * 					 y[1] = end y pos of previous block
      * 
      * 				  blockName is a unique identifier for the block.
      * 
      * Post		  	: a 3D-List of elements with (places,transitions,arcs) has been generated.
      * 				  this List represents the "standard-structure" for any activity:
      * 				   
      * 
      * @param blockName
      * @param y
      * @return
      */ 
     private  List [] genBlockWrappingElements(String blockName,int ypos, int yposEnd)
         {          
          int xpos 		= xMiddle;
          
          List [] res = new List[4];
          
          for (int i=0;i<4;i++)
             res[i]= new ArrayList();
          res[0].add(this.genAnnotationBlock(blockName,xStart,ypos-4*gYIncr[BabelConst.structured],100,18));
          // places
          Element readyPlace   	= genPlaceElement("r_" + blockName, "r_" + blockName);
          Element startPlace	= genPlaceElement("s_" + blockName, "s_" + blockName);
          Element completePlace	= genPlaceElement("c_" + blockName, "c_" + blockName);
          Element finishPlace 	= genPlaceElement("f_" + blockName, "f_"+ blockName);
          
          // transitions                    
          Element preTrans		= genTransitionElement("T_pre" + blockName,"pre_" + blockName);        
          Element postTrans		= genTransitionElement("T_post" + blockName,"post_" + blockName);
          // graphical stuff          
          EmitterPNMLGraphics.addGraphicsElement(readyPlace,xpos,ypos - 4*this.gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addGraphicsElement(preTrans,xpos,ypos - 3*this.gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(preTrans,90);
          EmitterPNMLGraphics.addGraphicsElement(startPlace,xpos,ypos - 2*gYIncr[BabelConst.structured]);
         
          EmitterPNMLGraphics.addGraphicsElement(completePlace,xpos,yposEnd + 2*gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addGraphicsElement(postTrans,xpos,yposEnd + 3*gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(postTrans,90);
          EmitterPNMLGraphics.addGraphicsElement(finishPlace,xpos,yposEnd + 4*gYIncr[BabelConst.structured]);          
          // generate all the arcs
          // 1. wrapping arcs 
          Element ready2Pre 	= genArcElement("r_" + blockName,"T_pre" + blockName);
          Element pre2s			= genArcElement("T_pre" + blockName,"s_" + blockName);         
          Element c2post		= genArcElement("c_"+ blockName,"T_post" + blockName);
          Element post2f		= genArcElement("T_post" + blockName,"f_"+ blockName);
          
          res[1].add(readyPlace); res[1].add(startPlace);
          res[1].add(completePlace);res[1].add(finishPlace);
          res[2].add(preTrans);
          res[2].add(postTrans);
          res[3].add(ready2Pre);res[3].add(pre2s);
          res[3].add(c2post);res[3].add(post2f);
                    
          return res;         
         }

      /**
       * 
       * MethodName		: genSkipPathSA
       * Purpose    	: generate the skip path for an structured activity; 
       * Pre		  	: 
       * Post		  	: 
       * 
       * @param yposStart
       * @param yposEnd
       * @return
       */
      private List[] genSkipPathSA(List blockList, String blockName, int yposStart,
                                   int yposEnd,int treeDepth,int breadth)
         {          
          // removed: 16.05.05.  
          //  all structured except the top one have a skip-Path.
          // if ( treeDepth==0 )
          //   return null;
          String id 	= "" + treeDepth + breadth;
          // keep in mind for the following formula that the max. treeDepth possible
          // for a structured activity is always inputTreeHeight + 1 because the 
          // "deepest" activity of a tree is always a basic activity.
          
          // 20.04.05: unfortunately the positioning did not work here. 
           int xpos  	= this.xposMax + 20 + 100 * (this.inputTreeHeight - treeDepth);
          //int xpos=100;
          //if (alterFlag)
          //   xpos = this.xposMax + 60;                                               
          List [] res 	= new List[6];         
          for (int i=0;i<6;i++)
            res[i]= new ArrayList();                                     
          // Naming Conventions:
          String toSkipPlace 		= "toSkip_" 	+ id;
          String skippingPlace 		= "skipping_" 	+ id;
          String skippedPlace		= "skipped_" 	+ id;
          String startSkipTrans 	= "Ts_skipping_"+ id;
          String finishSkipTrans	= "Tf_skipping_"+id;
          // generate the places for the skip path.
          Element toSkip		= genPlaceElement(toSkipPlace,toSkipPlace);
          Element skipping 		= genPlaceElement(skippingPlace,skippingPlace);
          Element skipped 		= genPlaceElement(skippedPlace,skippedPlace);         
          EmitterPNMLGraphics.addGraphicsElement(toSkip,xpos,yposStart);        
          EmitterPNMLGraphics.addGraphicsElement(skipping,xpos,yposStart + (yposEnd-yposStart)/2);       
          EmitterPNMLGraphics.addGraphicsElement(skipped,xpos,yposEnd);          
          res[1].add(toSkip);
          res[1].add(skipping);
          res[1].add(skipped);          
          // transitions for skip path
          Element tStartSkipping 	= genTransitionElement(startSkipTrans,startSkipTrans);
          Element tEndSkipping		= genTransitionElement(finishSkipTrans,finishSkipTrans);
          /* old
          Element tStartSkipping 	= genTransitionElement("Ts_skipping_"+id,"s_skipping_" + id);
          Element tEndSkipping		= genTransitionElement("Tf_skipping_"+id,"f_skipping_"+id);
          */
          EmitterPNMLGraphics.addGraphicsElement(tStartSkipping,xpos,yposStart + (yposEnd-yposStart)/4 + 20);         
          EmitterPNMLGraphics.addGraphicsElement(tEndSkipping,xpos,yposStart + 3*(yposEnd-yposStart)/4 + 20);          
          res[2].add(tStartSkipping);
          res[2].add(tEndSkipping);
          // arcs for skip Path
         // old Element toSkip2TStartSkip 	= genArcElement(toSkipPlace,"Ts_skipping_"+id);
          Element toSkip2TStartSkip 	= genArcElement(toSkipPlace,startSkipTrans);
          // old Element tStartSkip2skipping  	= genArcElement("Ts_skipping_"+id,skippingPlace); 
          Element tStartSkip2skipping  	= genArcElement(startSkipTrans,skippingPlace);
          // old Element skipping2tEndSkip		= genArcElement(skippingPlace,"Tf_skipping_"+id);
          // old Element tEndSkip2skipped		= genArcElement("Tf_skipping_"+id,skippedPlace);
          Element skipping2tEndSkip		= genArcElement(skippingPlace,finishSkipTrans);
          Element tEndSkip2skipped		= genArcElement(finishSkipTrans,skippedPlace);
          res[3].add(toSkip2TStartSkip);
          res[3].add(tStartSkip2skipping);
          res[3].add(skipping2tEndSkip);
          res[3].add(tEndSkip2skipped);
          // arcs from tStartSkipping into the skipStart place of all blocks
          // and arcs from skipEnd place of all blocks into the transition tEndSkipping
          Element arcTskip2SkipStartPlace;
          Element arcSkipEndPlace2TskipEnd;
          if (blockName.startsWith("seq"))
             {
              // Skip Path for sequence activity.
              // transition startSkipTrans into the skip- StartPlace of the first Block of the sequence.
              Block b = (Block) blockList.get(0);              
              arcTskip2SkipStartPlace = genArcElement(startSkipTrans,b.getSkipStartPlace().getAttributeValue("id"));
              res[3].add(arcTskip2SkipStartPlace);
              // inner Blocks: connect the EndSkipPlace of the previous block to the startSkip Place of the
              // successor.
              Block bprev;
              Element transConnector;
              Element arcEndSkipToInner;
              Element arcInnerToStartSkip;
              for (int i=1;i<blockList.size();i++)
                 {
                  String transConnectorName="Tseq_skipping_" + id + "_" + i; 
                  bprev = b;
                  b = (Block) blockList.get(i);
                  transConnector 	= genTransitionElement(transConnectorName,transConnectorName);
                  // graphics setting for the transConnector.
                  double [] pos = bprev.getPosElement(bprev.getSkipEndPlace());
                  EmitterPNMLGraphics.addGraphicsElement(transConnector,pos[0] + 130,pos[1]);
                  // inner arcs to connect the skipBlocks sequentially.
                  arcEndSkipToInner = genArcElement(bprev.getSkipEndPlace().getAttributeValue("id"),
                        							transConnectorName);
                  arcInnerToStartSkip = genArcElement(transConnectorName,
                        							  b.getSkipStartPlace().getAttributeValue("id"));                  
                  res[2].add(transConnector);                
                  res[3].add(arcEndSkipToInner);
                  res[3].add(arcInnerToStartSkip);
                 }
              // transition
              //b = (Block) blockList.get(blockList.size()-1);
              arcSkipEndPlace2TskipEnd = genArcElement(b.getSkipEndPlace().getAttributeValue("id"),finishSkipTrans);
              res[3].add(arcSkipEndPlace2TskipEnd);
             }
          else
             {
              // Skip Path for non-sequence structured activity.
	          for(int i=0;i<blockList.size();i++)
	             {
	              Block b = (Block) blockList.get(i);
	              arcTskip2SkipStartPlace  = genArcElement(startSkipTrans,b.getSkipStartPlace().getAttributeValue("id"));
	              arcSkipEndPlace2TskipEnd = genArcElement(b.getSkipEndPlace().getAttributeValue("id"),finishSkipTrans);
	              res[3].add(arcTskip2SkipStartPlace);
	              res[3].add(arcSkipEndPlace2TskipEnd);
	             }     
             }
          //add the skip places of the structured activity (to ioBlock)
          res[4].add(toSkip);
          res[5].add(skipped);        
          
          return res;
         }
     
      /**
       * 
       * MethodName		: genWhile
       * Purpose    	: generate the While-Code 
       * Pre		  	:  l[0] ... blockList
       * 				   l[1] ... controlLinks
       * Post		  	: 
       * 
       * @param l
       * @param info
       * @return resBlock, the generated Code
       */
      public Block genWhile(List[] l,List info)
         {
          Info i_		= new Info(l,info);
          //bugFix04.1: within FaultCompHandler.
          boolean withInFC = ((Boolean) info.get(4)).booleanValue();         
          boolean sjfFlag = ((Boolean)this.sjfStack.pop()).booleanValue();
          List[] res 	= new List[7];
          List [] wrapEl = genBlockWrappingElements(i_.blockName,i_.yposStart,i_.yposEnd);
          int xpos=xMiddle,ypos;
          
          ypos = i_.yposStart - 4*gYIncr[BabelConst.structured];
          Block resBlock=null;
          Element readyPlace	= (Element) wrapEl[1].get(0);
          Element startPlace 	= (Element) wrapEl[1].get(1);
          Element completePlace	= (Element) wrapEl[1].get(2);
          Element finishPlace 	= (Element) wrapEl[1].get(3);
          Element postTrans		= (Element) wrapEl[2].get(1);
          
          
          for(int i=0;i<wrapEl.length;i++)
             res[i]=wrapEl[i];
          for(int i=wrapEl.length;i<7;i++)
             res[i]=new ArrayList();          
          // while : always has exactly one block.
          Element t		= this.genTransitionElement("Tt_"+i_.blockName,"do_"  + i_.blockName);
          EmitterPNMLGraphics.addGraphicsElement(t,new Double(1.5*xpos).intValue(),i_.yposStart - gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(t,90);
          Element notT	= this.genTransitionElement("Tf_"+i_.blockName,"not_" + i_.blockName);
          EmitterPNMLGraphics.addGraphicsElement(notT,new Double(0.5*xpos).intValue(),i_.yposStart - gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(notT,90);
          Element tEndWhile = this.genTransitionElement("TEnd_" + i_.blockName,"end_" + i_.blockName);
          EmitterPNMLGraphics.addGraphicsElement(tEndWhile,new Double(1.5*xpos).intValue(),i_.yposEnd + gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(tEndWhile,90);
          Element tSkippedWhile = this.genTransitionElement("TSkipped_"+i_.blockName,"skipped_" + i_.blockName);
          EmitterPNMLGraphics.addGraphicsElement(tSkippedWhile,new Double(0.5*xpos).intValue(),i_.yposEnd + gYIncr[BabelConst.structured]);
          EmitterPNMLGraphics.addRotationElement(tSkippedWhile,90);
          //  bugFix03.8:           
          if (!withInFC)
             this.doWhileList.add(t.getAttributeValue("id"));
          
          // get the finish and ready places of the inner block.
          Block b = (Block) i_.blockList.get(0);
          List ioBlock[] = b.getIOBlock();
          Element bReady 	= (Element) ioBlock[0].get(0);
          Element bToSkip	= (Element) ioBlock[0].get(1);
          Element bFinish 	= (Element) ioBlock[1].get(0);
          Element bSkipped	= (Element) ioBlock[1].get(1);
          // connect via arcs.
          Element arcStartPlace2notT 	= this.genArcElement(startPlace.getAttributeValue("id"),"Tf_"+i_.blockName);
          Element arcStartPlace2T		= this.genArcElement(startPlace.getAttributeValue("id"),"Tt_"+i_.blockName);                											 
          Element arcT2Ready			= this.genArcElement("Tt_"+i_.blockName,bReady.getAttributeValue("id"));
          Element arcNotT2ToSkip		= this.genArcElement("Tf_"+i_.blockName,bToSkip.getAttributeValue("id"));
          Element arcFinish2TendWhile	= this.genArcElement(bFinish.getAttributeValue("id"),"TEnd_" + i_.blockName);
          Element arcTendWhile2SP		= this.genArcElement("TEnd_" + i_.blockName,startPlace.getAttributeValue("id"));          
          Element arcSkipped2Tskip		= this.genArcElement(bSkipped.getAttributeValue("id"),"TSkipped_"+i_.blockName);
          Element arcTSkip2CP			= this.genArcElement("TSkipped_"+i_.blockName,
                											 completePlace.getAttributeValue("id"));          
          // graphics for the arc : TendWhile to the start place.
          double [] posStartPlace = b.getPosElement(startPlace);          
          addWhileArcGraphics(arcTendWhile2SP,new Double(1.5*xpos).intValue(),i_.yposEnd + gYIncr[BabelConst.structured],
                			  posStartPlace[0],posStartPlace[1]);
          res[2].add(t); res[2].add(notT); 
          res[2].add(tEndWhile);res[2].add(tSkippedWhile);
          res[3].add(arcStartPlace2notT);
          res[3].add(arcStartPlace2T);
          res[3].add(arcT2Ready);
          res[3].add(arcNotT2ToSkip);
          res[3].add(arcFinish2TendWhile);
          res[3].add(arcTendWhile2SP);
          res[3].add(arcSkipped2Tskip);
          res[3].add(arcTSkip2CP);          
          res[4].add(readyPlace);          
          res[5].add(finishPlace);
          res[6].add(i_.blockName);          
          // old: resBlock = new Block(res,4,BabelConst.structured);
          List [] code = b.getCode();
          // add the inner blocks.
          for(int k=0;k<b.getCodeLength();k++)                 
             res[k].addAll(code[k]);
          
          List[] skipCode=this.genSkipPathSA(i_.blockList,i_.blockName,
				   							 i_.yposStart - 4*this.gYIncr[BabelConst.structured],
				   							 i_.yposEnd  + 4*this.gYIncr[BabelConst.structured],
				   							 i_.treeDepth,i_.conNo);
          // skipCode == null => topLevel activity.
          if (skipCode!=null)
             {
	          // toSkipPlace and skipTrans of the Skip-Path of the structured activity itself.
	          String toSkipPlace		= ((Element) skipCode[1].get(0)).getAttributeValue("id");
	          String skipStartTrans		= ((Element) skipCode[2].get(0)).getAttributeValue("id");
	          String skipEndTrans		= ((Element) skipCode[2].get(1)).getAttributeValue("id");
	          List [] skipPlaces 		= getSkipPlaces(i_.blockList);
	          //List toSkipPlacesList		= new ArrayList(); 	// List of toSkip places of subactivities of this structured activity.
	          //List skippedPlacesList	= new ArrayList();	// List of skipped places of subactivities of this structured activity.
	          // add controlLinks to the structured activity switch
	          if (!l[1].isEmpty())
	             addControlLinks(res,l[1],sjfFlag,i_.treeDepth,i_.conNo,
	                   			 "T_pre"+i_.blockName,"T_post"+i_.blockName,readyPlace.getAttributeValue("id"),
	                   			 finishPlace.getAttributeValue("id"),
	                   			 toSkipPlace,skipStartTrans,skipEndTrans,skipPlaces[0],skipPlaces[1]);  
             }       
          resBlock = new Block(res,4,BabelConst.structured);             
          // add the skip path
          resBlock.addListArray(skipCode);                             
          /* old
          resBlock.addListArray(this.genSkipPathSA(i_.blockList,i_.blockName,
                								   i_.yposStart-4*this.gYIncr[BabelConst.structured],
                                                   i_.yposEnd+4*this.gYIncr[BabelConst.structured],
                                                   i_.treeDepth,i_.breadth));
          */
          this.addInfo(postTrans,resBlock);
          return resBlock;                 
         }
    
       
      private int hasBasicActivity(List blockList)
         {
          
          for(int i=0;i<blockList.size();i++)
             if ( ((Block)blockList.get(i)).getBlockType()==BabelConst.basic)
             	return 1;
          return 0; 
         }
      
      /**
       * MethodName	:	genTaskElement
       * Purpose	:	generate code for a basic activity element.
       *        		
       * PRE		:	info contains the following information: 
       * 						info.get(0)=conNo					INTEGER, consecutive number of tasks
       * 						info.get(1)=name of the task		STRING
       * 						info.get(2)=treeDepth				INTEGER
       * 						info.get(3)=e						Element
       * 						// bugFix03
       * 						info.get(4)=withInFC				Boolean, indicating of the BA is within a FH xor CH.
       * 						info.get(5)=withInEH				Boolean, indicating the BA is within an event handler.
       * 						info.get(6)=handlerToInvokeName		String, name of the toInvoke Place.
       * 						info.get(7)=scopeID					Stack, scopeStack
       * POST		:	Block encapsulating a List[] with: 
       * 					
       * 				List[0]		... labels (graphical annotation)
       * 				List[1]		... places
       * 				List[2]		... transition
       * 				List[3]		...	arcs
       * 				List[4]		... ioBlock input
       * 				List[5]		... ioBlock output
       * 				List[6]		... BlockName
       * 
       * 	
       * @param	info,	List with	
       * 
       * @return	Block,	object encapsulating the List with the above structure.
       */
      public Block genTaskElement(List info)
         {
          Block taskBlock;
          boolean sjfFlag = ((Boolean)this.sjfStack.pop()).booleanValue();
          boolean withInFC = ((Boolean) info.get(4)).booleanValue();
          List[] res = new List[7];
          for(int i=0;i<res.length;i++)
             res[i] = new ArrayList();
          int treeDepth = ((Integer) info.get(2)).intValue();
          int conNo		= ((Integer) info.get(0)).intValue();          
          String taskName  = (String) info.get(1);          
          String blockName = BPEL.getAbbreviationForActivity((String) info.get(1)) + treeDepth + conNo;          
          Element [] eP = new Element[6]; // places
          Element [] eA = new Element[8]; // arcs
          Element [] eT = new Element[5]; // transitions (3 for normal path + 1 SkipPath + 1 Termination        
          Element e2;
          //> bugFix06: scopeStack.
          Stack scopeStack = (Stack) info.get(7);
          // taskID is unique defined via [treeDepth conTaskNo]
          String taskId = "" + treeDepth + conNo; //((Integer) info.get(2)).toString() + ((Integer)info.get(0)).toString();                              
          // generate the code for the "graphical" Block (called annotation in Pipe).
          // build the labels
       	  // res[0].add(this.genAnnotationBlock(e3.getText(),i));
          String actInfo=getActivityInfo((Element) info.get(3));;         
          String readyPlace 	= "r_"+taskId;
          String finishPlace 	= "f_" + taskId;
          String toSkipPlace	= "toSkip_" + taskId;
          String skippedPlace	= "skipped_" + taskId;
          String skipTrans		= "skip_" + taskId;
          String startPlace		= "s_" + blockName;
          String completePlace	= "c_" + blockName;          
           // before: String transName 		= "T_" + blockName
          res[0].add(this.genAnnotationBlock(blockName + actInfo,((Integer) info.get(0)).intValue(),
                							this.xposMax-this.xStart-24));
          // generate the places: ready and finish.
          eP[0] = genPlaceElement(readyPlace,readyPlace);
          eP[3] = genPlaceElement(finishPlace,finishPlace);          
          // generate the places: start and complete.
          eP[1] = genPlaceElement(startPlace,startPlace);
          eP[2] = genPlaceElement(completePlace,completePlace);                 
          // generate the places for skip path.
          eP[4]	= genPlaceElement(toSkipPlace,toSkipPlace);
          eP[5] = genPlaceElement(skippedPlace,skippedPlace); // old: "Pskipped_"+taskId+"1");          
          for (int i=0;i<6;i++)
             res[1].add(eP[i]);                  
          // generate the transition code
          eT[0]	= genTransitionElement("pre"+taskId,"pre"+taskId);    
          if (Babel.graphicsOption)
             eT[1] = genTransitionElement(blockName,blockName);
          else 
             eT[1] = genTransitionElement(blockName,blockName + actInfo);
          //genTransitionElement("T"+taskId,blockName + actInfo); //blockName);
          eT[2] = genTransitionElement("T_post" + taskId,"post"+taskId);
          eT[3] = genTransitionElement(skipTrans,skipTrans);
          for (int i=0;i<4;i++)
             res[2].add(eT[i]);          
          // generate the arcs
          eA[0] = genArcElement(readyPlace,"pre"+taskId);
          eA[1] = genArcElement("pre"+taskId,startPlace);
          eA[2] = genArcElement(startPlace,blockName);
          eA[3] = genArcElement(blockName,completePlace);
          eA[4] = genArcElement(completePlace,"T_post" + taskId);
          eA[5] = genArcElement("T_post" + taskId,finishPlace);
          // arcs for skip Path
          eA[6] = genArcElement(toSkipPlace,skipTrans);
          eA[7]	= genArcElement(skipTrans,skippedPlace);          
          for (int i=0;i<8;i++)
             res[3].add(eA[i]);                           
          //addTermination BA 20.06.05          
          String byPassTransName = "bypass_" + taskId;	 
          // do not generate bypass if within FC.
          if(!withInFC)
             {
	          eT[4] = genTransitionElement(byPassTransName,byPassTransName); 
	     	  res[2].add(eT[4]);	 
	     	  res[3].add(genArcElement(startPlace,byPassTransName));
	     	  res[3].add(genArcElement(byPassTransName,completePlace));
             }
     	  
          // generate the graphical information at basic task level.          
          addTaskGraphics(res[1],res[2]);          
          res[4].add(eP[0]);
          res[4].add(eP[4]);
          res[5].add(eP[3]);
          res[5].add(eP[5]);        
          res[6].add((String) info.get(1));
          
          // 16/06/05 moved taskBlock from here close to the return. This should be fine, since 
          // res was added before and potentially just extended.
          // controlLinks.
          Element e = (Element) info.get(3); // see preCondition.
          List[] sourceLinks	= null;
          List[] targetLinks	= null; 
          List[] sjfPath    	= null;
          List eChildList 		= e.getChildren();
          for (int i=0;i<eChildList.size();i++) // max 2: sources and targets.
             {
              Element eChild = (Element) eChildList.get(i);
              //BPEL1.1: source
              if (Babel.BPEL11 && 
                    (eChild.getName().compareTo(BPEL.controlLinks[2])==0
                     || eChild.getName().compareTo(BPEL.controlLinks[3])==0)     )
                 eChild = BPEL.controlLinkElementTo20(eChild);                                   
              if (eChild.getName().compareTo(BPEL.controlLinks[0])==0) // sources                 
                 	sourceLinks = genSourceLinks(eChild,treeDepth,conNo,"T_post" + taskId,skipTrans);                 	                 
              else if (BPEL.hasJoinCondition(eChild)) // old: eChild.getName().compareTo(BPEL.controlLinks[1])==0) //targets
                 {
              	  targetLinks = genTargetLinks(eChild,treeDepth,conNo,"pre"+taskId,
              	                               readyPlace,finishPlace,toSkipPlace,skipTrans);  
              	  if ( sjfFlag )
              	     {              	      
              	      String jctPlace 	= (String) targetLinks[3].get(0);
              	      String jcfPlace 	= (String) targetLinks[3].get(1);
              	      sjfPath 			= genSjfPath(jctPlace,jcfPlace,readyPlace,finishPlace,null,null,treeDepth,conNo);              	                  	    
              	     }
                 }
             }                                      
          // attach the sourceLinks
          if (sourceLinks!=null)
             {
              addGraphicsSourceLinks(sourceLinks);
              for (int i=0;i<3;i++)
                 res[i].addAll(sourceLinks[i]);
              if ( sjfPath!=null ) // i.e.: sources and targets
                 {
                  // add linkNames to the HashMap for sjf - transition. 
                  // the name of the sjf-transition is returned via sjfPath[3] 
                  for (int i=0;i<sourceLinks[3].size();i++)
                     this.hMSjf.put((String) sourceLinks[3].get(i),sjfPath[3].get(0));
                 }
             }
          // attach the targetLinks
          if (targetLinks!=null)
             {
              addGraphicsTargetLinks(targetLinks);
              for (int i=0;i<3;i++)
                 res[i].addAll(targetLinks[i]);
              // add the sjf Path
              if ( sjfPath!=null )
                 for(int i=0;i<3;i++)
                    res[i].addAll(sjfPath[i]);
             }          
          taskBlock = new Block(res,4,BabelConst.basic);
          // 16.06.05: set the core operation elements to access it later during the 
          // the translation process.
          // Termination Stuff.
          
          Element tt = null;
          Element sP = null;
          if ( sourceLinks!=null )
             {
              tt = (Element) sourceLinks[1].get(0);            
              if ( !sjfFlag )
                 sP = eP[1];
             }                    
          //bugFix03: don't add if BA is within a FH xor CH.
          if (!withInFC)             
             setTerminationListBA(eT[1],eT[4],tt,sP,scopeStack);
          // 24.06.05: insert into HashMap for CompensationHandler.
          if ( StringTools.isInStringArray((String) info.get(1),BPEL.compensateCommand) )
             {
              List compList = new ArrayList();
              compList.add(eT[1]);
              compList.add(eT[2]);
              if (e.getAttributeValue("scope")!=null)                
                 hMCompHandler.put(e.getAttributeValue("scope"),compList);
              else
                 hMCompHandler.put("",compList);                
             }         
          //taskBlock.setCoreOperation(termList);
          // 16.06.05: Set exitActivity to the unique name of the first exit activity if at all.
          if ( exitActivity.compareTo("")==0 && 
               StringTools.isInStringArray(taskName,BPEL.exitCommands) )
             exitActivity = blockName;          
          //10.06.05
          // do not increase noPrevBA if it is an activity of the fault handler.
          if (xHandlerOffset == 0) 
             noPrevBA = noPrevBA + 1;          
          this.addInfo(eT[2],taskBlock);          
          // 29.05.05: if basic activity is throw activity then 
          //           insert information into the HashMap for FaultHandler
          if (taskName.compareTo(BPEL.throwActivity)==0)
             insertIntoHMFH(e,blockName,info);                             
          return taskBlock;
         }
      
      /**
       * 
       * MethodName		: setTerminationListBA
       * Purpose    	: 
       * Pre		  	:
       * Post		  	: 
       * 
       * @param coreTrans
       * @param byPassTrans
       * @param tt
       * @param startPlace
       * @param scopeId, String id of the scope of the task.
       */
      private void setTerminationListBA(Element coreTrans,Element byPassTrans,Element tt, Element startPlace,
                                        Stack scopeStack)
         {
          System.out.println("Termination BA.");
          List termList = new ArrayList();          
          termList.add(coreTrans); 		// core transition of the activity.
          termList.add(byPassTrans); 	// bypass transition.
          if ( tt!=null )
             {
              termList.add(tt);
              System.out.println("SourceLinks : TT" + tt.getAttributeValue("id"));
             }
          if ( startPlace!=null ) // sjf - Flag = true and controlLinks.
             {
              termList.add(startPlace); // startPlace.
              System.out.println("Sjf=no => included. StartPlace: " + startPlace.getAttributeValue("id"));
             }                  
          String scopeId 	= (String) scopeStack.peek();
          List tList 		= (List) this.hMScopeTList.get(scopeId);
          //bugFix06_i: add scopeStack.
          termList.add(scopeStack);                    
          if (tList==null)
             {
              tList=new ArrayList();
              tList.add(termList);
              hMScopeTList.put(scopeId,tList);
             }
          else
             tList.add(termList);          
          //old terminationList[0].add(termList);          
         }
      
      /**
       * 
       * MethodName	    : insertIntoHMFH
       * Purpose    	: Insert a new key,value pair into the Hashmap  
       * Pre		  	: e is an element representing a throw activity.
       * Post		  	: a new entry in the HashMap with:
       * 				  key is String of the following structure:
       * 					{faultName,faultVariable,faultType} 
       * 				  value is a String with: transName as entry.
       * 
       * @param e			, element containing a throw activity.
       * @param transName	, the id-name of the transition
       * @param info		, List, see SPEC before.
       */
      private void insertIntoHMFH(Element e,String transName,List info)
         {          
          String key 		=  "{" + getAttributeInfo(e,BPEL.faultAttr,",",true) + "}";         
          key = replaceVarWithType(key,e);
          System.out.println("Insert into HashMap: (" + key + "," + transName + ")" );
          this.hMFaultHandler.put(key,transName);
          // BugFix03: (faultName,toInvokePlaceName) into Hashmap.
          boolean withInEV 	= ((Boolean) info.get(5)).booleanValue();
          if ( withInEV )
             {
              System.out.println("Throw within event Handler: (" + key + "," + (String) info.get(6) + ")");  
              this.hMEventHandler.put(key,(String) info.get(6));
             }                 
         }
      
      /**
       * 
       * MethodName		: genSjfPath
       * Purpose    	: Generation of the sjf-Path for incoming control links. 
       * Pre		  	:  toSkipPlacesList is a list of Strings containing the names
       * 				   of all toSkip-Places of subactivities.
       * 				   AND
       * 				   skippedPlacesList s a list of Strings containing the names
       * 				   of all skipped-Places of subactivities.		
       * Post		  	: resList[0..2] ... code, i.e. places,transitions,arcs
       * 				  resList[3] ... sjf1 for BA , sjf2 for SA
       * 
       * @param jctPlace
       * @param jcfPlace
       * @param readyPlace
       * @param finishPlace
       * @param skipPathPlaces
       * @param treeDepth
       * @param conNo
       * @return
       */
      private List[] genSjfPath(String jctPlace, String jcfPlace,String readyPlace,String finishPlace,
            					List toSkipPlacesList,List skippedPlacesList,int treeDepth,int conNo)
         {
          List [] resList = new List[3+1]; // 3  + 1 for the name of the transition needed later to link to lst's
          for (int i=0;i<4;i++)
             resList[i] = new ArrayList();
          String sjf1 = "sjf1_" + treeDepth + conNo;
          
          Element sjf1Trans = genTransitionElement(sjf1,sjf1);
          // connections: ready-sjf, 
          Element readyToSjf1 	= genArcElement(readyPlace,sjf1);
          if (toSkipPlacesList == null) //BasicActivity
             {
              // sjf-finish,jcf-sjf 
              Element sjfToFinish	= genArcElement(sjf1,finishPlace);
              Element jcfToSjf		= genArcElement(jcfPlace,sjf1);
              resList[1].add(sjf1Trans);
              resList[2].add(readyToSjf1);
              resList[2].add(jcfToSjf);
              resList[2].add(sjfToFinish);
              resList[3].add(sjf1);
             }
          else // structured Activity 
             {
              String toFxPlace 		= "to_fx_" + treeDepth + conNo;
              String sjf2			= "sjf2_" + treeDepth + conNo;             
              Element toFx 			= genPlaceElement(toFxPlace,toFxPlace);              
              Element sjf2Trans  	= genTransitionElement(sjf2,sjf2);
              // 31.08.05 : bug-fix: jcf_X -> sjf1_X
              Element jcfToSjf1		= genArcElement(jcfPlace,sjf1);
              //Element sjf2ToFinish 	= genArcElement(sjf2,finishPlace);
              resList[0].add(toFx);
              resList[1].add(sjf1Trans);
              resList[1].add(sjf2Trans);
              resList[2].add(readyToSjf1);
              resList[2].add(jcfToSjf1);
              resList[2].add(genArcElement(sjf1,toFxPlace));              
              resList[2].add(genArcElement(toFxPlace,sjf2));
              resList[2].add(genArcElement(sjf2,finishPlace));
              resList[3].add(sjf2); // for structured activities: second sjf is used to connect to lst's.
              // general solution for skip Path of the subactivities
              // 1. sjf1 to toSkip 
              if (readyPlace.indexOf("seq")>-1)
                 {
                  // sjf Path for sequence: connect sjf1 to toSkip of the first Block
                  // and connect skippedPlace of the last Block to sjf2.
                  int lastBlockNo = skippedPlacesList.size()-1;
                  resList[2].add(genArcElement(sjf1,(String) toSkipPlacesList.get(0)));
                  resList[2].add(genArcElement((String) skippedPlacesList.get(lastBlockNo),sjf2));                  
                 }
              else
                 {  
	              for (int i=0;i<toSkipPlacesList.size();i++)
	                 resList[2].add(genArcElement(sjf1,(String) toSkipPlacesList.get(i)));                             
	              // 2. skippedPlace to sjf2
	              for (int i=0;i<skippedPlacesList.size();i++)
	                 resList[2].add(genArcElement((String) skippedPlacesList.get(i),sjf2));  
                 }
             }                              
          return resList;          
         }
      
      /**
       * 
       * MethodName		: postProcessing
       * Purpose    	: All open targets and source connections of controlLinks get connected.
       * 				  BugFix03: blue arc between event Handler and the corresponding faultHandler.	 
       * Pre		  	: code is array of elements, with code[2] contains the information about arcs.
       * 				  naming convention is followed i.e. : tt_<no> for trueTransition,
       * 				  tf_<no> for false transition and the places are named lst_<no> lsf_<no> 
       * 				          			    
       * Post		  	: code[2] = code[2] + extra arcs between sources and targets.
       * 
       * @param code
       */
      public void postProcessing(List [] code)
         {
          String trans,place;
          String sjfTrans;
          String skipTrans;
          Set keys = hMSources.keySet();
          Object [] keyObjects = keys.toArray(); // keyObjects is the Array of (source) linkNames
          for (int i=0;i<keyObjects.length;i++)
             {   
                trans 	= (String) this.hMSources.get(keyObjects[i]);
                // find the target via the linkName!             
             	place	= (String) this.hMTargets.get(keyObjects[i]);
             	System.out.println(keyObjects[i]);
             	System.out.println("trans: " + trans + "    place: " + place);
             	code[2].add(genArcElement(trans,place)); // true  
             	// now modify for the false connection. by replacing tt->tf and lst->lsf. (see preCondition). 
             	trans = trans.replaceAll("tt","tf");
             	place = place.replaceAll("lst","lsf");             	
                code[2].add(genArcElement(trans,place)); // false
                // connection of sjf transition to the correct lsf-places
                sjfTrans = (String) this.hMSjf.get(keyObjects[i]);
                if ( sjfTrans!=null )
                   code[2].add(genArcElement(sjfTrans,place)); 
                // connection of skip transition to the correct lsf-places.
                skipTrans = (String) this.hMSkip.get(keyObjects[i]);
                if ( skipTrans!=null )
                   code[2].add(genArcElement(skipTrans,place));
             }
         }
      
      /**
       * 
       * MethodName		: genTargetsLinks
       * Purpose    	: construct the petrinet structure for joinConditions.
       * 				  additionally: read of the HashMap.
       * Pre		  	:
       * Post		  	: 
       * 
       *
       */
      private List[] genTargetLinks(Element e,int treeDepth,int conNo,String preTrans,
            						String readyPlace,String finishPlace,String toSkipPlace,String skippingTrans)
         {          
          Element joinConditionElement = e.getChild(BPEL.linkConditions[0],e.getNamespace());
          String joinCondition;
          
          if ( joinConditionElement!=null )
             joinCondition = joinConditionElement.getText();
          else              
             {
              List l = e.getChildren();
              // build linkName because the BoolEvaluatorPreProcessor assumes the BPEL-logExpr syntax. 
              joinCondition= BPEL.buildLinkName((Element) l.get(0));
              for (int i=1;i<l.size();i++)
                 joinCondition = joinCondition + " or " + BPEL.buildLinkName((Element) l.get(i));
             }                    
          return buildBoolNet(joinCondition,treeDepth,conNo,preTrans,readyPlace,finishPlace,toSkipPlace,
                              skippingTrans);
         }
     /**
      * 
      * MethodName	: buildBoolNet
      * Purpose    	: build the petriNet for the joinCondition using the boolExpr.
      * 			  within the joinCondition Element.
      * Pre		  	:
      * Post		: 
      * 
      * @param joinCondition
      * @return
      */
      private List[] buildBoolNet(String joinCondition,int treeDepth,int conNo,String preTrans,
            					  String readyPlace, String finishPlace,String toSkipPlace,
            					  String skippingTrans)
         {
            List [] resList = new List[3+1]; // 3 + 1: jct/jcf Place 
            Element [][] res=new Element[3][];    
            System.out.println(joinCondition);
         	BlockNet b = new BlockNet(joinCondition);         	
         	boolean [][] tt = b.getTruthTable();         	
         	HashMap hMLinkInfo_ = b.getHashMapLink();
         	// add into the Emitter HashMap.         
         	this.hMLinkInfo.putAll(hMLinkInfo_);
         	// n ... # variables in the bool Expr. assumption : different names !!
         	int n = hMLinkInfo_.size();
         	
         	System.out.println(hMLinkInfo_.toString() + "  Size: " + n);
         	// there 2*n places, 2^n transitions and 2 outputs.
         	// each input place has 2^(n-1) outgoing arcs => 2*n*2^(n-1) = n*2^n arcs.
         	// each of the 2^n transitions has exactly one outgoing arc => 2^n arcs.
         	// => overall: 2^n + n*2^n arcs = (n+1) * 2^n
         	res[0] = new Element[2*n + 2 + 1]; // inputPlaces + output Places + betweenPlace (for skip)
         	res[1] = new Element[MathTools.intPower(2,n)+2]; // + 2 (extra "skipTrans").         	
         	res[2] = new Element[(n+1)*MathTools.intPower(2,n)+1+9]; // + 1 jct to ready + 9: for skipTrans.
         	String lst = "lst_in_" + treeDepth + conNo + "_";
         	String lsf = "lsf_in_" + treeDepth + conNo + "_";
         	String jct = "jct_" + treeDepth + conNo;
         	String jcf = "jcf_" + treeDepth + conNo;
         	String tName = "BNT_" + treeDepth + conNo + "_";
         	// input places
         	for (int i=0;i<n;i++)
         	   {
         	     // this is not nice that I assume "l" + i for the variable name but i have no time 
         	     // to think about a better way. (-> see BlockNet,BoolEvaluator)
         	     this.hMTargets.put(hMLinkInfo.get("l"+i),lst + i);
         	     res[0][2*i]   = genPlaceElement(lst + i,lst + i);         	   
         	     res[0][2*i+1] = genPlaceElement(lsf + i,lsf + i);
         	   }
         	// transitions
         	for (int i=0;i<MathTools.intPower(2,n);i++)
         	   res[1][i] = genTransitionElement(tName + i,tName + i);
         	// output places
         	res[0][2*n] 	= genPlaceElement(jct,jct);
         	res[0][2*n+1]	= genPlaceElement(jcf,jcf);
         	// arcs 
         	// tt.length = 2^n i.e. the Truth-Table has 2^n rows.
         	// the following loop performs the connection of input places to the corresponding transition
         	// and the connection of the transition to one of the two possible outputs.
         	for (int i=0;i<tt.length;i++)
         	  MathTools.assignValuesToArray(res[2],
         	        						MathTools.getIndexVector(i*n+i,(i+1)*n+i),
         	        						connectBN(tt[i],lst,lsf,tName+i,jct,jcf));
         	// add the connection jct to the preCondition of the Task.
         	res[2][(n+1)*MathTools.intPower(2,n)] = genArcElement(jct,preTrans);
         	// add two new transitions and one in-between place
         	String tjct 			= "Tjct_" + treeDepth + conNo;
         	String tjcf 			= "Tjcf_" + treeDepth + conNo;
         	String jcvPlace = "jcv_" + treeDepth + conNo;
         	res[1][MathTools.intPower(2,n)] 	= genTransitionElement(tjct,tjct);
         	res[1][MathTools.intPower(2,n)+1]	= genTransitionElement(tjcf,tjcf);
         	res[0][2*n+2] = genPlaceElement(jcvPlace,jcvPlace);
         	// now the arcs:
         	int offSet = (n+1)*MathTools.intPower(2,n);
         	res[2][offSet + 1] = genArcElement(jct,tjct);
         	res[2][offSet + 2] = genArcElement(jcf,tjcf);
         	res[2][offSet + 3] = genArcElement(tjct,jcvPlace);
         	res[2][offSet + 4] = genArcElement(tjcf,jcvPlace);
         	res[2][offSet + 5] = genArcElement(toSkipPlace,tjct);
         	res[2][offSet + 6] = genArcElement(toSkipPlace,tjcf);
         	res[2][offSet + 7] = genArcElement(tjct,toSkipPlace);
         	res[2][offSet + 8] = genArcElement(tjcf,toSkipPlace);
         	res[2][offSet + 9] = genArcElement(jcvPlace,skippingTrans);
         	// transform into List [] 
         	for (int i=0;i<3;i++) 
         	   {
         	    resList[i] = new ArrayList();
         		for (int j=0;j<res[i].length;j++)
         		   resList[i].add(res[i][j]);
         	   }         
         	// needed to pass the jctPlace and jcfPlace for the sjf-Path construction
         	resList[3] = new ArrayList();
         	resList[3].add(jct);
         	resList[3].add(jcf);
         	
         	return resList;         	
         }
      
      /**
       * 
       * MethodName		: connectBN
       * Purpose    	: build the (arc) connection between the inputPlaces and a transition
       * 				  and between the transition and of the two outputPalces.  
       * Pre		  	: row.length = n+1 , where n...# variables.
       * 				  AND 
       * 				  row[1:n]= true/false for variables AND row[n+1] = result
       * Post		  	: 
       * 
       * @param row		a row of the Truth Table.
       * @param lst
       * @param lsf       
       * @param transName
       * @param jct
       * @param jcf
       * 
       * @return res,	Element [] with res.length = row.length  
       */
      private Element[] connectBN(boolean [] row,String lst,String lsf,String transName,String jct,String jcf)
         {
          int nPlus1 = row.length;
          int n		 = nPlus1 - 1;
          Element [] res = new Element [nPlus1];
          // build the arcs to connect input places to the transition.
          for (int i=0;i<n;i++)
             if (row[i]) // true Place
                res[i] = genArcElement(lst + i, transName);
             else
                res[i] = genArcElement(lsf + i, transName);
          // build the arc transition to result place
          if (row[n]) // result is true
             res[n] = genArcElement(transName,jct);
          else
             res[n] = genArcElement(transName,jcf);
                   
          return res;
         }
          
      
      /**
       * 
       * MethodName		: genSourceLinks
       * Purpose    	: Generate the petri-net for the transition condition. 
       * Pre		  	: 
       * Post		  	: Elements for the TransitionCondition
       * 
       * @param treeDepth
       * @param conNo
       * @param tCNo
       * @return
       */
      private List [] genSourceLinks(Element e,int treeDepth,int conNo,String postCond,String skipTrans)
         {
            List [] res = new List[3+1]; // 3 + 1 for list of linkNames            
            for (int i=0;i<4;i++)               
               res[i] = new ArrayList();
            
            List eChild = e.getChildren();
        	String id 		= "" + treeDepth + conNo + "_";
         	String tC	  	= "tc_out" + id;
         	String lst		= "lst_out" + id;
         	String lsf		= "lsf_out" + id;         	
         	String tt		= "tt_" + id;
         	String tf		= "tf_" + id;
            for (int i=0;i<eChild.size();i++)
               {     
                // it is sufficient to save tt + i because of the symmetry to tf + i.
                this.hMSources.put( ((Element) eChild.get(i)).getAttributeValue("linkName"),tt + i);
                // add the skipTransition into the HashMap for the corresponding linkName.
                this.hMSkip.put(((Element) eChild.get(i)).getAttributeValue("linkName"),skipTrans);
                // list of linkNames. Potentially needed later to build the sjf HashMap.
                res[3].add(((Element) eChild.get(i)).getAttributeValue("linkName"));
	         	//Places         	
	         	res[0].add(genPlaceElement(tC + i,tC + i));
	         	// the places namce lst_out are equivalent to the places named lst_in
	         	//res[0].add(genPlaceElement(lst + i,lst + i));
	         	//res[0].add(genPlaceElement(lsf + i,lsf +i));
	         	// Transitions         	
	         	res[1].add(genTransitionElement(tt+i,tt+i));
	         	res[1].add(genTransitionElement(tf+i,tf+i));
	         	// Arcs         
	         	res[2].add(genArcElement(postCond,tC+i)); // postCondition to tc Place.
	         	res[2].add(genArcElement(tC+i,tt+i));
	         	res[2].add(genArcElement(tC+i,tf+i));
	         	// not necessary : see before.
	         	//res[2].add(genArcElement(tt+i,lst+i));
	         	//res[2].add(genArcElement(tf+i,lsf+i));	         	
               }	         	         
         	return res;
         }
      
      /**
       * 
       * MethodName		: getActivityInfo
       * Purpose    	: builds a string containing information about the Activity actions.
       * Pre		  	: an assign always has a copy from to structure.
       * Post		  	: 
       * 
       * @param e
       * @return
       */ 
      private String getActivityInfo(Element e)
         {
         	String res="{";
         	
         	// TODO: generalise. here it is BPEL-specific.
         	if (e.getName().compareTo("assign")==0)
         	   {
         	    String assignStmt="";
         	    // BPEL: we expect copy from to structure.         	             	  
         	    Element copy=e.getChild("copy",e.getNamespace());
         	   
         	    if (copy==null)
         	       assignStmt="copy is null";
         	    if (copy!=null)
         	      {
         	      	Element from 	= copy.getChild("from",copy.getNamespace());
         	      	Element to	 	= copy.getChild("to",copy.getNamespace());
         	      	if (to!=null)         	      	   
         	      	    assignStmt+=getAttributeInfo(to,BPEL.toAttr,"",false);         	      	            	      	   
         	      	else
         	      	   assignStmt="to_is_Null";
         	      	if (from!=null)
         	      	   {         	      	   	
         	      	   	Element e_expr  	= from.getChild("expression");         	      	   	
         	      	   	assignStmt = assignStmt + " = ";
         	      	   	assignStmt+=getAttributeInfo(from,BPEL.fromAttr,"",false);         	      	   
         	      	   	if (e_expr!=null)
         	      	   	   assignStmt+=e_expr.getText();         	              	      	   	            	      	   	          	      	   
         	      	   }         
         	      	else
         	      	   assignStmt=assignStmt + "from_is_Null";
         	      }         	   
         	     res=res + assignStmt;         	     
         	   }
         	else
         	   {
         	   	for (int i=0;i<BPEL.dataAttributes.length;i++)
         	   	   {
         	   	    Attribute a = e.getAttribute(BPEL.dataAttributes[i]);
         	   	    if (a!=null)
         	   	       res = res + BPEL.dataAttributesAbbrevation[i] + "=" + a.getValue() + ","; 
         	   	   }
         	   	if (res.length()>1)
         	   	   // remove last ,
         	   	   res=res.substring(0,res.length()-1);
         	   }
         	
         	res = " " + res + "}";
         	return res;
         }
      
      /**
       * 
       * MethodName	    : getAttributeInfo
       * Purpose    	: Get the values of a given set of Attributes  
       * Pre		  	:   e is a non-null Element
       * 					attrList is a non-null list of AttributeNames.
       * 					delim	is the definition for a deliminitor.
       * 						(default: ":" )
       * 					includeAttrName indicates if the name of the Attribute
       * 					will be included. If yes then the structure for an entry
       * 					is as follows:
       * 					<AttrName>Value
       * Post		  	: a single delim-separted String is returned containing the values of
       * 				  the attributes.
       * 
       * @param e
       * @param attrList
       * @param delim
       * @param includeAttrName
       * @return
       */
      private String getAttributeInfo(Element e,String[] attrList,String delim,boolean includeAttrName)
         {
           String res ="";
   	   	   boolean flag=false;
   	   	   if (delim==null || delim.compareTo("")==0)
   	   	      delim=":";
   	   	   for(int i=0;i<attrList.length;i++)
   	   	      {
   	   	   	   Attribute a_expr = e.getAttribute(attrList[i]);         	      	   	   	
   	   	   	   if (a_expr!=null)
   	   	   	      {   	   	   	   	        
   	   	   	   		if (!flag)
   	   	   	   		   {
   	   	   	   		   	if (includeAttrName)
   	   	   	   		   	   res+="<" + a_expr.getName() + ">";
   	   	   	   		    res+=a_expr.getValue();
   	   	   	   		   }
   	   	   	   		else
   	   	   	   		   {
   	   	   	   		    if (includeAttrName)
   	   	   	   		       res+=delim + "<" + a_expr.getName() + ">" + a_expr.getValue();
   	   	   	   		    else
   	   	   	   		       res+=delim+a_expr.getValue();
   	   	   	   		   }   	   	   	   		
   	   	   	   		flag = true;
   	   	   	      }
   	   	      }   	   	   
   	   	   return res;
         }
      /**
       * 
       * MethodName		: addSeqArcGraphics
       * Purpose    	: 
       * Pre		  	: 	e is representing an arc 
       * 					xpos and ypos are the position of the transition
       * 					inout ... 0 for into the transition , 1... else		
       * Post		  	: 
       * 
       * @param e
       * @param xpos
       * @param ypos
       * @param inout
       */
      private void addSeqArcGraphics(Element e,double xpos,double ypos,int inOut)
         {         	
            Element[] arc = initArcPathElement(3);
         	if (inOut==1)
         	   {
         	   	arc[0].setAttribute("x",new Integer((new Double(xpos-5)).intValue()).toString());
         	   	arc[0].setAttribute("y",new Integer((new Double(ypos+10)).intValue()).toString());
         	   	arc[1].setAttribute("x",new Integer((new Double(xStart + 11)).intValue()).toString());
         	   	arc[1].setAttribute("y",new Integer((new Double(ypos+10)).intValue()).toString());
         	   	arc[2].setAttribute("x",new Integer((new Double(xStart + 11)).intValue()).toString());
         	   	arc[2].setAttribute("y",new Integer((new Double(ypos+65)).intValue()).toString());         	   	         	   
         	   }
   	   		else   	   
   	   		   {
        	   	arc[2].setAttribute("x",new Integer((new Double(xpos+10)).intValue()).toString());
        	   	arc[2].setAttribute("y",new Integer((new Double(ypos+10)).intValue()).toString());
        	   	arc[1].setAttribute("x",new Integer((new Double(xposMax + 11 )).intValue()).toString()); // 600 + xStart + 11
        	   	arc[1].setAttribute("y",new Integer((new Double(ypos+10)).intValue()).toString());
        	   	arc[0].setAttribute("x",new Integer((new Double(xposMax + 11)).intValue()).toString());
        	   	arc[0].setAttribute("y",new Integer((new Double(ypos-65)).intValue()).toString());         	   	         	   
        	   }
         	addArcPathToElement(e,arc);   		   
         }   
      
      private void  addWhileArcGraphics(Element e,double xposEnd,double yposEnd,double xposStart,double yposStart)
         {
          Element[] arc = initArcPathElement(4);
          arc[0].setAttribute("x",new Integer((new Double(xposStart+5)).intValue()).toString());
   	   	  arc[0].setAttribute("y",new Integer((new Double(yposStart+10)).intValue()).toString());
   	   	  arc[2].setAttribute("x",new Integer((new Double(xposMax + 33)).intValue()).toString());
   	   	  arc[2].setAttribute("y",new Integer((new Double(yposStart+10)).intValue()).toString());
   	   	  arc[1].setAttribute("x",new Integer((new Double(xposMax + 33)).intValue()).toString());
   	   	  arc[1].setAttribute("y",new Integer((new Double(yposEnd + 10)).intValue()).toString());
   	   	  arc[3].setAttribute("x",new Integer((new Double(xposEnd - 5)).intValue()).toString());
	   	  arc[3].setAttribute("y",new Integer((new Double(yposEnd + 10)).intValue()).toString());	   	  
	   	  addArcPathToElement(e,arc);
         }
      
      private Element[] initArcPathElement(int n)
         {
         	Element[] arc = new Element[n];
         	for (int i=0;i<n;i++)
         	   {         	   	
         	    arc[i]=new Element("arcpath");
         	    arc[i].setAttribute("id","00"+i);   	   	
         	   }
         	
         	return arc;
         }
  
      private void addArcPathToElement(Element e, Element[] arc)
         {
         	for(int i=0;i<arc.length;i++)
	   		   {
	   		   	arc[i].setAttribute("curvePoint","false");   	   		
	   		    e.addContent(arc[i]);
	   		   }
         }
      
      /**
       * 
       * MethodName		: addTaskGraphics
       * Purpose    	: add graphical information for "basic" activities 
       * Pre		  	: consequtive number count starts with 0.
       * Post		  	: 
       * 
       * @param places
       * @param transitions
       * @param info
       */
      private void addTaskGraphics(List places, List transitions)
         {
          //position of places.
          double x=0.0,y=0.0;              
          int deltaX = (xposMax-xStart)/6; // prev: 100          	  	         
          y = 295 + 300 * noPrevBA + 200 * noPrevSA;
          xStart += xHandlerOffset;
	  	  x=xStart;
	  	  for(int i=0;i<places.size()-2;i++)
	  	     {
	  	      EmitterPNMLGraphics.addGraphicsElement((Element) places.get(i),x,y);	  	     
	  	      x=x+2*deltaX;
	  	     }	  	  	  	  
	  	  x=xStart + deltaX - 5; // prev: 130
	  	  
	  	  for(int i=0;i<transitions.size()-2;i++) // -2: skip Transition, termination Transition
	  	     {
	  	      EmitterPNMLGraphics.addGraphicsElement((Element) transitions.get(i),x,y);	  	     	  	     
	  	      x=x+2*deltaX;
	  	     }	  	  
	  	  //add skip stuff
	  	  int n=places.size()-1;
	  	  //x=230; y=y+75;	  	 	  	 	  	  
	  	  x=xStart + deltaX;y=y+75;
	  	  // 06.06.05: modify deltaX to improve the graphical layout.
	  	  deltaX = deltaX-20;
	  	  EmitterPNMLGraphics.addGraphicsElement((Element) places.get(n-1),x,y);
	  	  x=x + 3.4*deltaX; // old: 4*deltaX; 
	  	  EmitterPNMLGraphics.addGraphicsElement((Element) places.get(n),x,y);
	  	  x=xStart + 3*deltaX; 
	  	  EmitterPNMLGraphics.addGraphicsElement((Element) transitions.get(transitions.size()-2),x,y);
	  	  xStart -= xHandlerOffset;
	  	  // 20.06.05: bypass transition.
	  	  EmitterPNMLGraphics.addGraphicsElement((Element) transitions.get(transitions.size()-1),x+deltaX,y+50);
	  	  EmitterPNMLGraphics.addRotationElement((Element) transitions.get(transitions.size()-1),90);
         }
      
      private void  addGraphicsSourceLinks(List[] sourceLinks)
         {
          int x,y,deltaX,deltaY;
          y = 295 + 300 * noPrevBA + 200 * noPrevSA + 50; //  between task path and skip path          
          x=xposMax + 150;
          deltaX = 150;
          deltaY = 50;
          int n = sourceLinks[1].size()/2; // n ... # number of source Elements        
          for(int i=0;i<n;i++)
             {
              // tc -places
              EmitterPNMLGraphics.addGraphicsElement((Element) sourceLinks[0].get(i),x,y);
              // ls[t|f]_out places             
              // EmitterPNMLGraphics.addGraphicsElement((Element) sourceLinks[0].get(3*i+1),x-45,y+2*deltaY);
              //  EmitterPNMLGraphics.addGraphicsElement((Element) sourceLinks[0].get(3*i+2),x+45,y+2*deltaY);
              // transitions
              EmitterPNMLGraphics.addGraphicsElement((Element) sourceLinks[1].get(2*i),x-35,y+deltaY);
              EmitterPNMLGraphics.addRotationElement((Element) sourceLinks[1].get(2*i),90);
              EmitterPNMLGraphics.addGraphicsElement((Element) sourceLinks[1].get(2*i+1),x+35,y+deltaY);
              EmitterPNMLGraphics.addRotationElement((Element) sourceLinks[1].get(2*i+1),90);
              x=x+deltaX;
             }
         }
      
      private void addGraphicsTargetLinks(List[] targetLinks)
         {
          int x,y,deltaX,deltaY;
          y = 295 + 300 * noPrevBA + 200 * noPrevSA - 150; 
          x	= xposMax + 150;
          deltaX = 150;
          deltaY = 35;
          int n=targetLinks[0].size()/2 - 1; // n ... # number variables, we have 2*n + 2 places.
          
          for(int i=0;i<n;i++)
             {
              // input places 
              EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[0].get(2*i),x,y); // true place
              x = x + 100;
              EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[0].get(2*i+1),x,y); // false place   
              x=x+deltaX;
             }
          int xmax =x - deltaX;
          x = xposMax + 50;
          int k=1;
          for(int i=0;i<MathTools.intPower(2,n);i++)
             {
              //transitions
              EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[1].get(i),x,y+k*deltaY);
              EmitterPNMLGraphics.addRotationElement((Element) targetLinks[1].get(i),90);
              if (i%2==0)
                 x=x+100;
              else
                 x=x+ deltaX;              
             }          
          // output places 
          int xDist = xmax - (xposMax + 50); // distance between first and last element.     
          EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[0].get(2*n),xposMax+50 + xDist/2 -35 ,y+2*deltaY);
          EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[0].get(2*n+1),xposMax+50 + xDist/2 + 35,y+2*deltaY);
          // now: skip stuff
          int offSet = MathTools.intPower(2,n);
          EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[1].get(offSet),xposMax+50 + xDist/2 -35,y+3*deltaY);
          EmitterPNMLGraphics.addRotationElement((Element) targetLinks[1].get(offSet),90);
          EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[1].get(offSet+1),xposMax+50 + xDist/2 + 35,y+3*deltaY);
          EmitterPNMLGraphics.addRotationElement((Element) targetLinks[1].get(offSet+1),90);
          EmitterPNMLGraphics.addGraphicsElement((Element) targetLinks[0].get(2*n+2),xposMax+50 + xDist/2,y+4*deltaY);
         }            
      
      /**
       * 
       * MethodName		: genAnnotationBlock
       * Purpose    	: generate code for an annotation block. 
       * Pre		  	:
       * Post		  	: 
       * 
       * @param infoString
       * @param blockLevel
       * @return
       */
      private Element genAnnotationBlock(String infoString,int blockLevel,int width)
         {
          Element e 	= new Element("labels");
          // old expr. String yStr 	= (new Integer(blockLevel*300+135+yOffset)).toString();
          String yStr = new Integer(235 + 300*noPrevBA + 200 * noPrevSA).toString();
          int xStartAnnotation = xStart + xHandlerOffset + 24;
          String xStr = "" + xStartAnnotation;
          e.setAttribute("x", xStr); //"84");          
          e.setAttribute("y",yStr);
          e.setAttribute("width",(new Integer(width)).toString());
          e.setAttribute("height","18");
          e.setAttribute("border","true");
          Element e2 = new Element("text");
          e2.addContent(infoString);          
          e.addContent(e2);
                                    
          return e;
         }
      
      private Element genAnnotationBlock(String infoString,int x,int y,int width,int height)
         {
          Element e 	= new Element("labels");
       
          e.setAttribute("x",(new Integer(x)).toString() );          
          e.setAttribute("y",(new Integer(y)).toString());
          e.setAttribute("width",(new Integer(width)).toString());
          e.setAttribute("height",(new Integer(height)).toString());
          e.setAttribute("border","true");
          Element e2 = new Element("text");
          e2.addContent(infoString);          
          e.addContent(e2);
                                   
          return e;
         }
      /**
       * 
       * MethodName		: genTransitionElement
       * Purpose    	: Generate code for an Transition Element 
       * Pre		  	: TRUE
       * Post		  	: Element containing PNML code for a transition is generated.
       * 
       * @param taskId	, String tid is the unique task ID.
       * @param value	, String value is the name of the transition.
       * 
       * @return		Element containing PNML code for a transition
       */
      public Element genTransitionElement(String taskId,String value)
         {
          Element e = new Element("transition");
          e.setAttribute("id",taskId);
          Element e2 = new Element("name");
          Element e3 = new Element("value");
          e3.addContent(value);
          e2.addContent(e3);
          e.addContent(e2);
          
          return e;          
         }
      
      public Element genPlaceElement(String nameofPlace,String id)
         {
          Element e=null;
          Element e2,e3;
                           
          e = new Element("place");
          e.setAttribute("id",id);
          e2 = new Element("name");
          e3 = new Element("value");
          e3.addContent(nameofPlace);
          e2.addContent(e3);
          e.addContent(e2);
                   
          return e;	
         }
      
      public Element genArcElement(String from,String to)
         {
          Element e=null;
          Element e2;
                          
          e = new Element("arc");
          e.setAttribute("id",from + " to " + to);
          e.setAttribute("source",from);
          e.setAttribute("target",to);
                  
          return e;	
        }
      
      /**
       * 
       * MethodName		: getSkipPlaces
       * Purpose    	: return the skip Places for the (sub)activities of the blockList.  
       * Pre		  	: blockList is a List of Blocks containig the elements of activities 
       * 				  
       * Post		  	: 2-D List is returned with
       * 				 res[0] : list of (String) names for the toSkip Place of an activity
       * 				 res[1] : list of (String) names for the skipped Place of an activity
       * 
       * @param blockList
       * @return
       */
      private List[] getSkipPlaces(List blockList)
         {
          List [] res = new List[2];
          res[0] 	= new ArrayList();
          res[1] 	= new ArrayList();
          for(int i=0;i<blockList.size();i++)
            {
             Block b = (Block) blockList.get(i);
             res[0].add(b.getSkipStartPlace().getAttributeValue("id"));
             res[1].add(b.getSkipEndPlace().getAttributeValue("id"));             
            }            
          return res;
         }

/* Generalised methods  */
      
      
 private void addInfo(Element postTrans,Block resBlock)
         {
          List blockInfo = new ArrayList();
   	  	  blockInfo.add(postTrans);   	  	  
          resBlock.setInfo(blockInfo);
         }
      
      /* (non-Javadoc)
       * @see model.backEnd.Emitter#epilogue()
       */
      public String epilogue()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#taskControl()
       */
      public String taskControl()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#taskDecomposition()
       */
      public String taskDecomposition()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Emitter#netDecomposition(java.lang.String)
       */
      public String netDecomposition(String netName)
         {
            // TODO Auto-generated method stub
            return null;
         }
      
      /**
       * 
       * Info
       * Purpose: Helper Class. To manage general information data passed to the methods for code
       * 		  generation.
       * @author Stephan Breutel
       * @version 1.0
       * @created 20/04/2005
       * 
       */
      
      
      
      
                       
      private class Info      
      	{
         public int breadth;
         public int treeDepth;
         public int conNo;
         public int noBlock;
         public String structureName;
         public String blockName;
         public Block bFinish;
         public Block bReady;
         public int yposStart;
         public int yposEnd;
         public List blockList;
         public String id;
         
         	Info (List[] l, List info)
         	 {         	         	 
              breadth 					= ((Integer) info.get(0)).intValue();
              treeDepth					= ((Integer) info.get(1)).intValue();
              conNo						= ((Integer) info.get(2)).intValue();
              structureName 			= (String) info.get(3);
              // BPEL - specific!!!
              // changed: 17.05.05: conNo instead of breadth.
              blockName 				= BPEL.getAbbreviationForActivity(structureName) + treeDepth + conNo;
              
              blockList 				= l[0];                        
              noBlock					= blockList.size();
              bReady					= (Block) blockList.get(0);
              bFinish 					= (Block) blockList.get(noBlock-1);
              double [] blockReadyPos	= bReady.getPosReadyPlace();                        
              double [] blockFinishPos	= bFinish.getPosFinishPlace();
              yposStart 				= new Double(blockReadyPos[1]).intValue();
              yposEnd					= new Double(blockFinishPos[1]).intValue();
              id						= "" + treeDepth + "_" + conNo;
         	}
      	}
   }
