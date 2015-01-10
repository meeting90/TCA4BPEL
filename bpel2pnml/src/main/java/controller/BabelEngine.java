/**
 * BabelEngine.java
 * Purpose: 	Controls the translation process. Design-wise the engine is between
 * 				the frontEnd (parsing, constructing the XML-Trees, constructing the SymbolTables)
 * 				and the backEnd (EmitterClasses, ResultTree). This class is general usable
 * 				for the translation process. 
 * 				IMPORTANT REMARKS: current implementation is PNML specific. Generalise later.
 * 				e.g. dispatch visit according to the target language.
 * 			
 * General Changes:
 * - 11.10.2005: unknown bpel-tags handling.
 * 				 - genCode() modification (check for BPEL.basicActivities, else unknown)
 *   			 - for all sub-Block generations added:  
 * 					Block b_ = genCode(...) 
 * 					if (b_!=null) addList.... 
 * 
 * @author Stephan Breutel
 * @version 1.0
 * @created 7/04/2005
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
package controller;

import java.lang.reflect.Method;
import java.util.*;

import org.jdom.Element;




import model.frontEnd.*;
import model.languageDefinition.*;
import model.backEnd.*;
import model.data.*;

import tools.ListTools;
import tools.StringTools;

public class BabelEngine implements Visitor
   {
   	/* Input */
   	XMLTree inputTree;		/* the complete ParseTree of the inputFile, e.g. XMLBPELTree */
   	XMLTree tasksTree;		/* subTree of the inputTree with topActivity as root */
   	Emitter	eY;				/* EmitterClass responsible to produce the correct code */   				
   	SymbolTable idTable;	/* symbolTable containing the names of var/par and their types */
   	SymbolTable typeTable;	/* symbolTable containing the types and their construction of primitive types */
   	SymbolTable taskTable;	/* symbolTable containing the names and properties of tasks */
   	String processName;		/* name of the process */
   	
   	/* Output */
   	ResultTree resTree;		/* the resulting Tree */
   	
   	/* Translation Process */   	
   	int conNo;				/* consecutive number of basic task */
   	List[] elementsList;	/* Array of Lists of Blocks */
   	List   taskNames;		/* (String) List of taskNames of the complete process */
   	List   taskNo;			/* (int) List of consecutive numbers of a task. This List is synchronised to the
   							  above List.*/
   	Stack  todoStack;		/* Stack used for the structured activities */		
   	
   	
   	/* Protocol */
   	String protocolString;	/* String used to store the protocol Information */	
   	/* BugFix 03 */
    boolean withInFault_Comp_Handler=false; /* flag indicating if the current translation process is within a fault 
                                      xor compensation Handler */
    boolean withInEventHandler=false; /* flag indicating if the current translation process is within an event handler */
    String handlerToInvokeName; /* name of the toInvoke place for a handler Code, important this has
    							   to be the same as in EmitterPNML.genHandler(..)*/
   	/*Bugfix 05 : core-transaction and bypass transaction have to be connected to the corresponding
   	 *            toStop,toContinue places of the scope.   	 
   	 */  
   	Stack scopeStack;
    
      /**
       * MethodName   : BabelEngine
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a BabelEngine object has been created.
       * 
       */
      public BabelEngine(XMLTree input,Emitter eY, SymbolTable idTable,SymbolTable typeTable, SymbolTable taskTable,
            			String processName,String fromLanguage,String toLanguage)
         {
            this.inputTree 	= input;
            this.eY			= eY;
            this.idTable	= idTable;
            this.typeTable	= typeTable;
            this.taskTable	= taskTable;
            this.processName= processName;
            this.resTree	= new ResultTree(this.eY);                        
            this.conNo		= 0;
            this.taskNames	= new ArrayList();
            this.taskNo		= new ArrayList();
            this.scopeStack	= new Stack();
            this.protocolString = 	" Protocol of the BabelEngine \n" +
            						"-----------------------------\n";            
            System.out.println("TreeHeight: " + input.getTreeHeight());
            System.out.println("Relevant TreeHeight : " + input.getRelevantTreeHeight());
            eY.setTreeHeight(input.getRelevantTreeHeight()-1); //BPEL-specific -1 for the BPEL language
         }
      /**
       * MethodName   : translateBPEL
       * Purpose	  : controls the translation process.
       * Pre		  : TRUE
       * Post	      : ResultTree is constructed.
       *        
       */
      public void translateBPEL()
         {
         	/* control of the translation process */              	   
  	    	// 1. add the prologue
  	    	resTree.genPrologue(processName);     	   
  	    	// 2. Traverse the BPEl-Tree and add the activities to the ResultTree     	    
  	    	// topActivity for BPEL: first child of <process> which is a structured activity. 
  	    	Element topActivity	= inputTree.getTopActivity(BPEL.topActivity);  	 
  	    	System.out.println("TopActiviyName: " + topActivity.getName());
  	    	// 16.06.05 preChecks.
  	        PreChecker pC = new PreChecker(inputTree);  	       
  	        if ( pC.hasCommand(BPEL.exitCommands) )
  	           {
  	            System.out.println("Exit commands are included.");  	             	            
  	            eY.setProperty( new Boolean("true"),0 );
  	           }
  	        if ( pC.hasCommand(BPEL.compensateCommand) )
  	           {
  	            System.out.println("Compensate command exits.");
  	            eY.setProperty( new Boolean("true"),1 );  	            
  	           }
  	    	List l = new ArrayList();
  	    	l.add(inputTree.getRoot());
  	    	eY.preProcessing(l);
  	    	Block b 		= genCode(topActivity,0,0,null);  	    	
  	    	List [] code	= b.getCode();
  	    	eY.postProcessing(code);
  	    
  	    	for (int i=0;i<code.length;i++)
  	    	   resTree.addList(code[i],resTree.getRoot().getChild("net"));
  	    	/* OLD: using the visitor pattern + own stack construction.
  	    	 * 		either i don't use it correctly or the visitor pattern in this 
  	    	 * 		case is total bullshit!!!
  	    	 * 
  	    	System.out.println("TOPACTIVITY  >>>" + topActivity.getName());
  	    	tasksTree = new XMLTree(topActivity);
  	    	visit(tasksTree);
  	    	*/
         }
      
      public ResultTree getResultTree()
         {
          return resTree;
         }
      
      /**
       * 
       * MethodName		: genCode
       * Purpose    	:  
       * Pre		  	: 
       * Post		  	: 
       * 
       * @param e
       * @param breadth
       * @param treeDepth
       * @param info
       * @return
       */
      public Block genCode(Element e,int breadth, int treeDepth,List info)
         {         
          updateProtocol("genCode" + e.getName() + "   TreeDepth:" + treeDepth);         	
          if (e.getParentElement()==null)
	      	  updateProtocol("Parent is null.");
	      else
	        System.out.println("e.getParent() :" + e.getParentElement().getName());	         
          List l = new ArrayList();
          l.add(e);          
	      eY.preProcessing(l);	
	      if ( e.getName().compareTo("scope")==0 || e.getName().compareTo("process")==0 ) 	          
	         return genCodeScope(e,breadth,treeDepth);
	      
	      if ( ( (StringTools.isInStringArray(e.getName(),BPEL.events) || 
	            StringTools.isInStringArray(e.getName(),BPEL.catches))
	           &&  e.getParentElement()!=null 
	           &&  StringTools.isInStringArray(e.getParentElement().getName(),BPEL.handlers) ) 
	           ||
	           e.getName().compareTo(BPEL.compensationHandler)==0
	          )	    	         
	          return genCodeHandler(e,breadth,treeDepth,info);	            
          if (e.getName().compareTo("sequence")==0)
         	 return genCodeSeq(e,breadth,treeDepth);
          else if (e.getName().compareTo("switch")==0)
         	 return genCodeSwitch(e,breadth,treeDepth);
          else if (e.getName().compareTo("pick")==0)
             // pick is considered as special switch: see formal spec.
             return genCodeSwitch(e,breadth,treeDepth);
          else if (e.getName().compareTo("flow")==0)
             return genCodeFlow(e,breadth,treeDepth);
          else if (e.getName().compareTo("while")==0)
             return genCodeWhile(e,breadth,treeDepth);    
          else if (StringTools.isInStringArray(e.getName(),BPEL.dataItem))
             return genCodeDataItem(e,breadth,treeDepth);
          // extended 11.10.05
          else if (StringTools.isInStringArray(e.getName(),BPEL.basicActivities))// basic Activity             
             { 
               System.out.println("BA: " + e.getName());
               // build the List defining the information for the task generation.
   	      	   List infoList  = new ArrayList();
   	           infoList.add(new Integer(conNo));	      
         	   infoList.add(e.getName()); // before: treeDepth + conNo before: 2del + conLevelTaskNo[treeDepth]);         	
         	   infoList.add(new Integer(treeDepth));
         	   infoList.add(e);  
         	   infoList.add(new Boolean(this.withInFault_Comp_Handler));
         	   infoList.add(new Boolean(this.withInEventHandler));
         	   infoList.add(handlerToInvokeName);
         	   // bugFix06: pass the complete scopeStack.
         	   infoList.add(scopeStack);
               conNo++;               
         	   return eY.genTaskElement(infoList);
             }           
          else 
             {
              System.out.println("Unknown Element: " + e.getName());
              return null;
             }
         }

      /**
       * 
       * MethodName		: genCodeScope
       * Purpose    	: Generate the code for a scope. A <process> is considerd as top-level scope.
       * 				  Important is that all dataParts (for BPEL: {"partnerLinks","variables","correlations"})
       * 				  get ignored. 
       * 
       * Pre		  	:	Handlers are followed by <onMessage> or <onAlarm> elements.
       * 
       * Post		  	: 
       * 
       * @param e
       * @param breadth
       * @param treeDepth
       * @return
       */
      public Block genCodeScope(Element e,int breadth, int treeDepth)
         {
         
          List info = new ArrayList();                
	      info.add(new Integer(breadth));	      
	      info.add(new Integer(treeDepth));	  
	      info.add(new Integer(conNo));   	      
	      info.add(e.getName());
	      String scopeId = "" + treeDepth + "_" + conNo;
	      scopeStack.push(scopeId);
	      conNo++;
	      eY.preProcessNode(null);  	     	      
	      List eChild 			= e.getChildren();
	      List blockList 		= new ArrayList();	
	      List controlLinks 	= new ArrayList();
	      List handlerList		= new ArrayList();
	      List eventList		= new ArrayList();
	      List catchList		= new ArrayList();		      
	      List compList			= new LinkedList(); // to distinguish in EmitterPNML.genHandler 
	      											// between Event and Compensation-Handler.
	      for (int i=0;i<eChild.size();i++)
	         {
	          Element eKid 		= (Element) eChild.get(i);
	          if ( StringTools.isInStringArray(eKid.getName(),BPEL.dataParts) )
	             // ignore data-Parts of a process.
	             System.out.println("Ignored: " + eKid.getName());
	          	// controlLinks. : postpone the construction of places,transitions and arcs to 
	          	// code-generation time of the structured activity.
	          else if (StringTools.isInStringArray(eKid.getName(),BPEL.controlLinks))
	             addControlLinks(controlLinks,eKid);
	             /*{
	              if (Babel.BPEL11)
	                 {
	                  Element e_ = new Element("sources");
	                  e_.addContent(eKid);
	                  controlLinks.add(eKid);
	                 }
	              else   
	                 controlLinks.add(eKid);
	             }*/
	          else if ( eKid.getName().compareTo(BPEL.eventHandlers)==0)
	             //if (StringTools.isInStringArray(eKid.getName(),BPEL.handlers))
	             {
	              // EVENT-HANDLER
	              this.withInEventHandler = true;
	              // according to the preCondition check for <onMessage>, <onAlarm>	             
	              // PreCondition : onAlaram and onMessage
	              System.out.println("SCOPE: EventHandler.");
	              List eKidChild = eKid.getChildren();
	              int last;
	              for(int j=0;j<eKidChild.size();j++)  
	                {
	                 if (StringTools.isInStringArray(((Element) eKidChild.get(j)).getName(),BPEL.events))	               	                   
	                  {	               	                   	                  
	                   eventList.add(eY.genCaseName((Element) eKidChild.get(j),j,treeDepth+1));
	                   last = eventList.size() - 1;
	                   // sublist returns a RandomAccessSubList (?)
	                   Block b_ = genCode((Element) eKidChild.get(j),j,treeDepth+2,eventList.subList(last,last+1));
	                   if (b_!=null)
	                      handlerList.add(b_);               
	                  }	                
	                 else
	                   System.out.println("Error. Direct Childs of an EventHandler are either " +
	                    				  "<onMessage> or <onAlarm>.");
	                }
	              this.withInEventHandler=false;
	             }
	          else if (eKid.getName().compareTo(BPEL.faultHandlers)==0)
	             {
	              //FAULT-HANDLER
	              List eKidChild = eKid.getChildren();
	              withInFault_Comp_Handler=true;
	              int last;
	              for(int j=0;j<eKidChild.size();j++)  
	                {
	                 if (StringTools.isInStringArray(((Element) eKidChild.get(j)).getName(),BPEL.catches))	               	                   
	                  {	               	                   
	                   catchList.add(eY.genCatchList((Element) eKidChild.get(j),j,treeDepth+1));
	                   last = catchList.size()-1;	  
	                   Block b_ = genCode((Element) eKidChild.get(j),j,treeDepth+2,catchList.subList(last,last+1));
	                   if (b_!=null)
	                      handlerList.add(b_);               
	                  }	  
	                }
	              withInFault_Comp_Handler=false;
	             }	             
	          else if ( eKid.getName().compareTo(BPEL.compensationHandler)==0 )
	             {
	             withInFault_Comp_Handler=true;
	              String scopeName="";
	              if (e.getAttributeValue("name")!=null)
	                 scopeName=e.getAttributeValue("name");
	              System.out.println("CompensationHandler for scope: " + scopeName);
	              compList.add(scopeName);
	              compList.add("CompensationHandler");  // neccessary to define the difference between
	              										// compHandler and EventHandler in EmitterPNML.genHandler.
	              Block b_ = genCode(eKid,i,treeDepth+1,compList);
	              if (b_!=null)
	                 handlerList.add(b_);
	              withInFault_Comp_Handler=false;
	             }
	          else	          
	             {
	              Block b_ = genCode(eKid,i,treeDepth+1,null);
	              if ( b_!=null )
	                 blockList.add(b_);
	             }
	         } 	    
	      // remove all null Blocks.
	      blockList 				= ListTools.removeNull(blockList);
	      List[] subActivityList 	= new List[5];
	      subActivityList[0]		= blockList;
	      subActivityList[1]		= controlLinks;	
	      subActivityList[2]		= handlerList;
	      subActivityList[3]		= eventList;
	      subActivityList[4]		= catchList;
	      
	      eY.preProcessNode(null);
	      scopeStack.pop();
	      // BugFix_Anna_1: pass scopeStack so that eNormal gets bidirectional arc
	      // to all its parent scopes
	      info.add(scopeStack);
	      System.out.println("ScopeStack before passing to EmitterPNML.");
	      System.out.println(scopeStack.toString());
	      return eY.genScope(subActivityList,info);	      
         }
      /**
       * 
       * MethodName		: genCodeHandler
       * Purpose    	: Code generation for an <onMessage> or <onAlarm> Handler.
       * Pre		  	:  
       * Post		  	: 
       * 
       * @param e
       * @param breadth
       * @param treeDepth
       * @param info
       * @return
       */
      public Block genCodeHandler(Element e,int breadth,int treeDepth,List actionList)
         {        
          List info = new ArrayList();
          info.add(new Integer(breadth));
          info.add(new Integer(treeDepth));         
          info.add(new Integer(conNo));
          info.add(e.getName());   
          //        bugFix03: 10.09.05: toInvokeName
          handlerToInvokeName = "toInvoke_"	+  treeDepth + "_" + conNo; 
          conNo++;          
          /* switch handler ON */
          List preProcessInfo = new ArrayList();
          preProcessInfo.add(new Integer(1));
          eY.preProcessNode(preProcessInfo);          
          List eChild 		= e.getChildren();
          List blockList	= new ArrayList();	  
          
          for (int i=0;i<eChild.size();i++)        
             {
              Block b_ = genCode((Element) eChild.get(i),i,treeDepth+1,null);
              if (b_!=null)
                 blockList.add(b_);
             }
          blockList 	 		= ListTools.removeNull(blockList);
          List[] handlerList 	= new List[2];
          handlerList[0]		= blockList;              
          handlerList[1]		= actionList;
          Block resBlock 		= eY.genHandler(handlerList,info);
          // post processing: remove the xoffset for the eventHandler 
          // to positing the next Node correctly.
          // preProcess for the next Node...                    
          preProcessInfo.set(0,new Integer(2));
          eY.preProcessNode(preProcessInfo);
          
          return resBlock;
         }
                       
      
      public Block genCodeSeq(Element e,int breadth, int treeDepth)
         {       
          List info = new ArrayList();
          info.add(new Integer(breadth));
          info.add(new Integer(treeDepth));         
          info.add(new Integer(conNo));
          info.add(e.getName());          
          conNo++;
          eY.preProcessNode(null);          
          List eChild = e.getChildren();
          List blockList = new ArrayList();	
          List controlLinks = new ArrayList();
          for (int i=0;i<eChild.size();i++)
             {
              Element eKid 		= (Element) eChild.get(i);             
              // controlLinks. : postpone the construction of places,transitions and arcs to 
              // code-generation time of the structured activity.
              if (StringTools.isInStringArray(eKid.getName(),BPEL.controlLinks))
                addControlLinks(controlLinks,eKid); // controlLinks.add(eKid);
              else
                 {
                  Block b_ = genCode((Element) eChild.get(i),i,treeDepth+1,null);
                  if (b_!=null)
                     blockList.add(b_);
                 }
             }                  
          // remove all null Blocks. why???
          blockList 	= ListTools.removeNull(blockList);
          List[] seqList 	= new List[2];
          seqList[0]	= blockList;
          seqList[1]	= controlLinks;
                          
          eY.preProcessNode(null);
          return eY.genSequence(seqList,info);
         }
      
      /**
       * 
       * MethodName		: genCodeSwitch
       * Purpose    	: 
       * Pre		  	: switch has direct childs: case/otherwise
       * 				  directly under case/otherwise are the other activities.
       * Post		  	: 
       * 
       * @param e
       * @param breadth
       * @param treeDepth
       * @return
       */
      public Block genCodeSwitch(Element e,int breadth, int treeDepth)
         {
          List info = new ArrayList();
          info.add(new Integer(breadth));
          info.add(new Integer(treeDepth));
          info.add(new Integer(conNo));
          info.add(e.getName());
          conNo++;
          eY.preProcessNode(null);          
          List[] switchList = new List[3];
          List eChild 		= e.getChildren();
          List blockList 	= new ArrayList();
          List condList	 	= new ArrayList();
          List controlLinks = new ArrayList();
          for (int i=0;i<eChild.size();i++)
             {
              Element eKid 		= (Element) eChild.get(i);             
              // controlLinks. : postpone the construction of places,transitions and arcs to 
              // code-generation time of the structured activity.
              if (StringTools.isInStringArray(eKid.getName(),BPEL.controlLinks))
                 addControlLinks(controlLinks,eKid); //controlLinks.add(eKid);
              else
                 // "jump" over case/otherwise/onMessage/onEvent
                 {
                  List eKidChild	= eKid.getChildren();
                  condList.add(eY.genCaseName(eKid,i,treeDepth));              
                  for(int j=0;j<eKidChild.size();j++)
                     // in BPEL: always just one eKidChild!!!
                     {
                      Block b_ = genCode((Element) eKidChild.get(j),j,treeDepth+2,null);
                      if (b_!=null)
                         blockList.add(b_);
                     }
                 }
             }          
          switchList[0]=condList;
          // remove all null Blocks.
          blockList = ListTools.removeNull(blockList);
          switchList[1]=blockList;
          switchList[2]=controlLinks;
          
          eY.preProcessNode(null);
          return eY.genSwitch(switchList,info);                    
         }
      
      
      
      public Block genCodeFlow(Element e,int breadth, int treeDepth)
         {
          List info = new ArrayList();
          info.add(new Integer(breadth));
          info.add(new Integer(treeDepth));
          info.add(new Integer(conNo));
          info.add(e.getName());
          conNo++;    
          eY.preProcessNode(null);
          Block flowBlock;
          List eChild 		= e.getChildren();
          List blockList 	= new ArrayList();          
          List controlLinks = new ArrayList();
          for (int i=0;i<eChild.size();i++)
             {
              Element eKid 		= (Element) eChild.get(i);             
              // controlLinks. : postpone the construction of places,transitions and arcs to 
              // code-generation time of the structured activity.
              if (StringTools.isInStringArray(eKid.getName(),BPEL.controlLinks))
                 addControlLinks(controlLinks,eKid); //controlLinks.add(eKid);
              else
                 {
                  Block b_ = genCode((Element) eChild.get(i),i,treeDepth+1,null);
                  if (b_!=null)
                     blockList.add(b_);
                 }
             }                             
          List [] flowList = new List[2];
          // remove all null Blocks.
          blockList   = ListTools.removeNull(blockList);
          flowList[0] = blockList;
          flowList[1] = controlLinks;
          eY.preProcessNode(null);
          return eY.genFlow(flowList,info);         
         }
      
      public Block genCodeWhile(Element e, int breadth,int treeDepth)
         {
          List info = new ArrayList();
          info.add(new Integer(breadth));
          info.add(new Integer(treeDepth));
          info.add(new Integer(conNo));
          info.add(e.getName());
          //bugFix04.1: withInFault_Comp_Handler
          info.add(new Boolean(this.withInFault_Comp_Handler));
          conNo++;
          eY.preProcessNode(null);          
          Block res;
          List eChild = e.getChildren();
          List blockList = new ArrayList();	
          List controlLinks = new ArrayList();
          for (int i=0;i<eChild.size();i++)
             {
              Element eKid 		= (Element) eChild.get(i);             
              // controlLinks. : postpone the construction of places,transitions and arcs to 
              // code-generation time of the structured activity.
              if (StringTools.isInStringArray(eKid.getName(),BPEL.controlLinks))
                 addControlLinks(controlLinks,eKid); //controlLinks.add(eKid);
              else
                 {
                  Block b_ = genCode((Element) eChild.get(i),i,treeDepth+1,null);
                  if (b_!=null)
                     blockList.add(b_);
                 }
             }                                            
          List [] whileList = new List[2];
          // remove all null Blocks.
          blockList = ListTools.removeNull(blockList);
          whileList[0] = blockList;
          whileList[1] = controlLinks;
          eY.preProcessNode(null);
          return eY.genWhile(whileList,info);
         }
      
      public Block genCodeDataItem(Element e, int breadth,int treeDepth)
         {
          System.out.println("Ignored: " + e.getName() + " and its children.");
          return null;
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#dispatchVisit(java.lang.Object)
       */
      public void dispatchVisit(Object o)
         {                  
            try {
              Method method = getMethod(o.getClass());
              method.invoke(this, new Object[] {o});
            } catch (Exception e) { }
         }

      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.XMLTree)
       */
      public void visit(XMLTree x)      
         {         	
         /* OBSOLETE SOON? */
            updateProtocol("BabelEngine: Visit the XML Tree. " + x.getClass().getName());
            System.out.println("TreeHeight: " +x.getTreeHeight());
            elementsList 	= new List[x.getTreeHeight()+1];          
            todoStack 		= new Stack();
            for (int i=0;i<x.getTreeHeight()+1;i++)
               {
               	elementsList[i] = new ArrayList();                                    
               }
            // does the traversal
            // -> leads to call of visit(Element e,int treeDepth)
            x.accept(this);                      
            // after the traversal: add to the result Tree.          
            while (!todoStack.isEmpty())
               {
               	updateProtocol("Stack is not empty. Start Phase2.");
               	String operation = (String) todoStack.pop();
               	updateProtocol("2do:" + operation);
               	// TODO: get the correct parts of elementsList[i][j]
                List[] eBlock=eY.genStructureCode(operation,elementsList[todoStack.size()+1],todoStack.size());
                for (int i=0;i<eBlock.length-3;i++)
                   // -2 because the last two elements are: BlockIO and BlockName
                   // BPEL->PNML specific add under the net-element.                   
                   resTree.addList(eBlock[i],resTree.getRoot().getChild("net"));
                }                     
         }
      
      public void visit(Element e,int breadth,int treeDepth)
         {         	       
            // visit here is the phase1 of the translation processing.
         	// it does: * build code for basic activities 
            //			* build 2 do stack for structured activities                 	
         	updateProtocol("visit Element:" + e.getName() + "   TreeDepth:" + treeDepth);         	
         	if (e.getParent()==null)
         	   updateProtocol("Parent is null.");
         	else
         	   updateProtocol(" Name Parent: " + ((Element) e.getParent()).getName());
         	
         	// build the List defining the information for the task generation.
         	List infoList  = new ArrayList();
         	infoList.add(new Integer(breadth));
         	// >> numbering system for tasks
         	// obsolete because replaced by [treeDepth conNo]
         	// possible uses: - graphical display, - petrinet analysis, - translation help
         	if (taskNames.isEmpty())
         	   {
         	   	taskNames.add(e.getName());
         	   	taskNo.add(new Integer(0));
         	   }	
         	else
         	   {
         	    int index=StringTools.findInList(e.getName(),taskNames);         	    
         	   	if ( index > -1 )
         	   	   taskNo.set(index,new Integer(((Integer) taskNo.get(index)).intValue() + 1 ));
         	   	else
         	   	   {
         	   	   	taskNames.add(e.getName());
         	   	   	taskNo.add(new Integer(0));
         	   	   }         	   	
         	   }         
         	// <<< numbering system for tasks
         	infoList.add(e.getName() + treeDepth + breadth); //2del + conLevelTaskNo[treeDepth]);         	
         	infoList.add(new Integer(treeDepth));
         	infoList.add(e);         
         	// generate the taskElement
         	// TODO: generalise to Language independent, i.e by introducing an dipatcher
         	// >>> this code is BPEL specific but can be generalised later.
         	if ( BPEL.isStructuredActivity(e.getName()) )
         	   // onStack: name of Activity, treeDepth, conLeveltaskNo # children.
         	   // 			compute the indexes : first+last [place|trans|arc]
         	   	todoStack.push(e.getName());
         	else if ( BPEL.isBasicActivity(e.getName()))
         	   {
         	   	Block taskBlock = eY.genTaskElement(infoList);
         	   	elementsList[treeDepth].add(taskBlock);
         		/* 2del
         	   	List [] eTask 	= taskBlock.getBlockList();         	   
         	   	for (int i=0;i<eTask.length;i++)
         	   	   {
         	   	   	System.out.println("no: " +i);
         	   	   	System.out.println(eTask[i].toString());
         	   	   	elementsList[treeDepth][i].addAll(eTask[i]);
         	   	   }
         	   */
         	   }
         	else
         	   {
         	   	updateProtocol("Ignored: " + e.getName());
         	   }
         	//<<< BPEL specific.
         }
      

      /* (non-Javadoc)
       * @see controller.Visitor#visit(model.frontEnd.SymbolTable)      
       */
      public void visit(SymbolTable s)
         {
          updateProtocol("SymbolTable Traversal.");
       	  s.accept(this);       	          
         } 
      
      public void visit(String key, Object value)
         {
         	String type	= value.toString();
         	
         	protocolString	= protocolString + "(" + key + ",";
         	dispatchVisit(value);
         	protocolString = protocolString + ")\n";   
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.util.Collection)
       */
      public void visit(Collection c)
         {
         	Iterator iterator = c.iterator();
         	while (iterator.hasNext()) 
         	   {
         	   	Object o = iterator.next();
         	   	if (o instanceof Visitable)
         	   	   ((Visitable)o).accept(this);
         	   }
         }
      
      /* (non-Javadoc)
       * @see controller.Visitor#visit(java.lang.Object)
       */
      public void visit(Object o)
         {
          System.out.println("Call of the Default visit.");
          protocolString = protocolString + o.toString();
         }
      
      /**
       * MethodName		: visit
       * Purpose    	: visit a ListArray. 
       * Pre		  	: TRUE
       * Post		  	: 
       * 
       * @param l
       */
      public void visit(List [] l)
         {
         	for(int i=0;i<l.length;i++)
         	   {
         	   	protocolString = protocolString + "{";
         	   	visit(l[i]);
         	   	protocolString = protocolString + "}";
         	   }
         }
      
      /**
       * MethodName		: visit
       * Purpose    	: visit a list 
       * Pre		  	: TRUE
       * Post		  	: the list is visited. 
       * 
       * @param l
       */
      public void visit(List l)
         {
         	//TODO visitable interface for traversal ?         	
         	for (int i=0;i<l.size()-1;i++)
         	   {
         	   System.out.println(l.get(i));
         	   	protocolString = protocolString + l.get(i).toString() + ",";
         	   }
         	protocolString = protocolString + l.get(l.size()-1).toString();
         }
      
      protected Method getMethod(Class c) {
         Class newc = c;
         Method m = null;
         // Try the superclasses
         while (m == null && newc != Object.class) 
            {
            String method = newc.getName();
            method = "visit"; // + method.substring(method.lastIndexOf('.') + 1);
            try {
               	m = getClass().getMethod(method, new Class[] {newc});
            	} catch (NoSuchMethodException e) 
            	{
            	newc = newc.getSuperclass();
            	}
            }
         // Try the interfaces.  If necessary, you
         // can sort them first to define 'visitable' interface wins
         // in case an object implements more than one.
         if (newc == Object.class) {
            Class[] interfaces = c.getInterfaces();
            for (int i = 0; i < interfaces.length; i++) {
               String method = interfaces[i].getName();
               method = "visit";// + method.substring(method.lastIndexOf('.') + 1);
               try {
                  m = getClass().getMethod(method, new Class[] {interfaces[i]});
               } catch (NoSuchMethodException e) {}
            }
         }
         if (m == null) {
            try {
                 m = getClass().getMethod("visit", new Class[] {Object.class});
            } catch (Exception e) {                
            }
         }
         return m;
      }

/* simple get and set methods */
      
      public String getProtocol()
         {
          return protocolString;
         }
      
/* private helper methods */
      private void updateProtocol(String s)
         {
            protocolString = protocolString + s + "\n";
         }
      
      private void addControlLinks(List controlLinks,Element eKid)
         {
          if (Babel.BPEL11)                         
             controlLinks.add(BPEL.controlLinkElementTo20(eKid));            
          else   
            controlLinks.add(eKid);
         }
      
   }
