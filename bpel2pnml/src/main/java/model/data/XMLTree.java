/**
 * XMLTree.java
 * Purpose: 	Construction of an XML-Tree of an XML text file.
 * 				Provision of general useful methods on an XML Tree.
 * 				Provision of specific methods relevant within the context of the BABEL project.
 * 
 * 				Important remarks:
 * 				 05.04.2005:
 * 							implement all methods in XML-Tree abstract and specialise later
 *							(e.g.: BPELTree extends XMLTree).
 *							For the moment BPELTree specific things are also included in the
 *							XML-Tree.							
 * @author Stephan Breutel
 * @version 1.0
 * @created 27/02/2005
 * @Further Development, especially usuage for BPMN2BPEL: June/July 2006.
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

package model.data;
import controller.Visitable;
import controller.Visitor;
import controller.PrintVisitor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.*;

import model.frontEnd.Node;
import model.languageDefinition.BPMN;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.jdom.filter.ElementFilter;

import tools.MathTools;
import tools.StringTools;


public class XMLTree implements Tree,Visitable
   {
   	protected Document 	doc;					/* JDOM-Document */
   	protected Element  	rootElement;			/* RootElement: JDOM Element */
   	protected String 	name; 					/* Name of the tree */
   	protected int 	 	treeHeight=-1;			/* Height of the tree */
   	protected int 		relevantTreeHeight=-1;	/* some elements of the tree are aggregated into one Task,
   												   we name those elements: remElements,
   												   e.g.: BPEL: copy/from/to */
   	protected String [] remElements;   												   
   	protected Element arcParent;			// direct parent node of arcs.
   	
 	public XMLTree()
   	   {
 	    
   	   }
   	
   	/**
   	 * MethodName : XMLTree
   	 * Purpose	  : Constructor
   	 * Pre		  : TRUE
   	 * Post	      : an XMLTree object has been created.
   	 * 
   	 * @param e,	Element 
   	 */
   	public XMLTree(Element e)
   	   {
   	   	rootElement = e; 
   		treeHeight  = calcTreeHeight(rootElement);
   	   }
   	
   	/**
   	 * MethodName : XMLTree
   	 * Purpose	  : Constructor
   	 * Pre		  : xmlString is a String following XML format.
   	 * Post	      : a XMLTree object has been created AND
   	 * 				the attributs doc,rootElement and treeHeight are set.	
   	 * @param xmlString
   	 */
   	public XMLTree(String xmlFileName)
   	   {
   	   	/* TODO : protocol Class with flag to switch on/off the monitoring of the program behaviour. 
   	   	System.out.println("Build the XML Tree of the file\n");
   	   	System.out.println("==============================");
   	   	System.out.println(xmlFileName);
   	   	System.out.println("==============================");
   	   	String result="";
   	   	*/
   	   	constructXMLTree(xmlFileName);
   	   }
   	
   	public XMLTree(String xmlFileName, String name)
   	   {
   	   	constructXMLTree(xmlFileName);
   	   	this.name = name;
   	   }
   	public XMLTree(String xmlFileName,String name, String [] remElements)
   	   {
   	    constructXMLTree(xmlFileName);
   	    this.name = name;
   	    this.remElements = remElements;
   	    this.relevantTreeHeight = calcTreeHeight(rootElement,remElements);
   	   }   	   	  
   	
   	
   	public void initArcParent(Element arcParent)
   	   {
   	    this.arcParent = arcParent; 
   	   }
   	
   	private void constructXMLTree(String xmlFileName)
   	   {
   	   	SAXBuilder builder = new SAXBuilder();
	   
	   	try {
	   	   	doc 		= builder.build(new File(xmlFileName));
	   	   	rootElement = doc.getRootElement();
	   	   	treeHeight  = calcTreeHeight(rootElement);
	   	   	System.out.println("Build finished successfully.");
	   		}
	   	catch (JDOMException e) {
	   	   	e.printStackTrace();
	   		}          	
	   	catch (IOException e) {
	   	   e.printStackTrace();
	   	   }	      	    
   	   }
   	
   	/**
   	 * 
   	 * MethodName	: update
   	 * Purpose    	: delete all Elements contained in the BPD-Component
   	 * 				  add a new element representing this component as a task.
   	 * 				  modify the arcs correspondingly	
   	 * Pre		  	: 
   	 * 				  - assumes that the list of component-Elements is in the correct
   	 * 					order, e.g. for split/join structures that the join
   	 *  				Element is the last one.
   	 * 				  - arcParent initialised
   	 * 				  - bC.getComponentList().size()>=2 // a component consists of at least two elements. 
   	 * Post		  	: 
   	 * 
   	 * @param bC
   	 */
   	public void update(BPDComponent bC)
   	   {
   	    // 1. Delete all elements.
        List elList 	= bC.getComponentList();
        System.out.println("XMLTree.update");
        System.out.println("elList.size()=" + elList.size());
        List idList 	= new ArrayList();
        String compId 	= bC.getComponentId();
        List bCInArcs 	= bC.getInArcs();
        List bCOutArcs 	= bC.getOutArcs();
        HashMap hMIdElement = bC.getHMidElement();
        for (int i=0;i<elList.size();i++)
          {
           Element e 		= (Element) elList.get(i);
           String id 		= e.getAttributeValue(BPMN.attrId);
           Element parent 	= (Element) e.getParentElement();
           idList.add(id);
           int index;
           if (parent==null)
              System.out.println("Parent is null for " + id);
           if ((index=parent.indexOf(e))==-1)
              {
               Iterator it = doc.getDescendants(new ElementFilter());
               while(it.hasNext())
                  {
                   Element e_= (Element) it.next();
                   System.out.println("Element e " + e_.getName() + " index is: " + doc.indexOf(e_));
                  }               
               System.out.println("Expected an entry for this element having id " + id + " and index  " + index );
               System.out.println("Index of the parent is :" + doc.indexOf(e.getParentElement().getParentElement()));
               System.out.println("Remove Element: " + doc.removeContent(e)); 
              }
           else
              {
               List inArcs 		= (List) bCInArcs.get(i);
               List outArcs 	= (List) bCOutArcs.get(i);
               if (i==0)
                 {
                  System.out.println("Replace the first element of the Component with the ComponentElement.");
                  Element e_New = bC.getComponentElement();                 
                  parent.removeContent(e);
                  parent.addContent(e_New);
                  System.out.println("Update Tree: eNew.parent is " + e_New.getParentElement());
                  // parent.setContent(index,);
                  // modify all ingoing arcs.                                 
                  System.out.println("Update ingoing arcs for Component with Id:" + compId); 
                  for(int k=0;k<inArcs.size();k++)
                     {
                      Element e_ = (Element) inArcs.get(k);
                      // (I) important in case the first element is a join element
                      // then just modify the attrTarget for the "outside" incoming arc
                      // Example: while, do-while
                      if (!hMIdElement.containsKey(e_.getAttributeValue(BPMN.attrSource)))
                         e_.setAttribute(BPMN.attrTarget,compId);                      
                     }
                   // removeArcs(outArcs) not required because this will be done via 
                   // the inner elements. This is the case because the outgoing arcs of
                   // the first element are the ingoing for the next element in the 
                   // component.
                 }
               else
                  {
                   System.out.println("Eliminate element with id " + id + " in the Tree.");
                   parent.removeContent(e);
                   if (i==elList.size()-1) // last element.
                      {
                       // Delete all incoming arcs.
                       System.out.println("Delete incoming arcs.");
                       removeArcs(inArcs,arcParent);	
                       // modify all outgoing arcs.
                      for(int k=0;k<outArcs.size();k++)
                         {
                          System.out.println("Modify outgoing arc.");
                          Element e_ = (Element) outArcs.get(k);
                          // symmetrical point as above for the first arc (see (I))
                          if (!hMIdElement.containsKey(e_.getAttributeValue(BPMN.attrTarget)))
                             e_.setAttribute(BPMN.attrSource,compId);
                          // remove a guard if there was one before
                          if (e_.getAttributeValue(BPMN.attrGuard)!=null)
                             e_.removeAttribute(BPMN.attrGuard);
                         }
                      }
                   else 
                      {
                       // remove all inner arcs, assuming arcParent was initialised.
                       System.out.println("Delete inner arcs within the component.");
                       removeArcs(inArcs,arcParent);
                       removeArcs(outArcs,arcParent);
                      }
                  }              
              }
          }                  
   	   }
  
   	
   	
   	private void removeArcs(List arcList,Element parent)
   	   {
   	    for(int i=0;i<arcList.size();i++)
   	       {
   	        Element e = (Element) arcList.get(i);
   	        parent.removeContent(e);
   	       }
   	   }
   	/**
   	 * MethodName	: getTopActivity
   	 * Purpose    	: Get the top activity of the (BPEL) XML-Tree. 
   	 *  
   	 * Pre		  	: the topActivity is a child of this.rootElement 
   	 * 					AND
   	 * 				  the top activity is always a structured activity.  
   	 * Post		  	: (an element of nameofTA is the name of a topActivity in this XML-Tree
   	 * 				   AND  an Element object (hence a subTree) is returned containing the topActiviy as root)
   	 * 				  XOR
   	 * 				  (no element of nameofTA is the name of topActivity AND null is returned)
   	 * 			
   	 * 
   	 * @param nameofTA	, Array of Strings of names valid for a topActivity.
   	 * 						BPEL:(flow,pick,scope,sequence,switch,while)
   	 * 
   	 * @return	Element, the SubTree of the topActivity. 
   	 */
   	public Element getTopActivity(String[] nameofTA)
   	   {
   	   	return this.findElement(rootElement,nameofTA);
   	   /* obsolete implementation.
   	   	Element res		= null;
   	   	List childList	= rootElement.getChildren();   	   	   	   
   	   	for(int i =0;i<childList.size();i++)
	       {
   	   	   	Element child  = (Element) childList.get(i);	
   	   	   	String  str	  = child.getName();
   	   	   	if (isInStringArray(str,nameofTA))
		      {		      	  
		      	res = child;
		      	break;
		      }   	   	   
	       }   	     	   	
   	   	return res;
   	   	*/
   	   }
   	
/* methods before holiday: please check vs. new methods and claen up soon. */   	
   	
   	
   	/**
   	 * MethodName	: getChildren of an element under the rootElement.
   	 * Purpose    	: behaviour is different to the JDOM's method. This method also returns an element
   	 *                "if the target element has no nested elements with the given name outside a namespace".
   	 * 			 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @param topElement   	 
   	 * @return List, list of children elements
   	 */
   	public List getChildren(String topElement)
   	   {   	   	 
   	   	//TODO : all Levels : currently: only one level down the root.      	    
   	    List result=null;
   	    List childList=rootElement.getChildren(); //topElement);
   	    List topList=new ArrayList();
   	    for(int i =0;i<childList.size();i++)
   	       {
   	       Element child  = (Element) childList.get(i);	
		   String  str	  = child.getName();
		   if (str.compareTo(topElement)==0)
		      {
		      	topList.add(child);		      	
		      	System.out.println("Element found and added to the List.");
		      	break;
		      }
		   System.out.println(str);
   	       }   	    
   	    //TODO more than one topElement.
   	    if (topList==null || topList.isEmpty())
   	       {
   	       System.out.println("Element "+ topElement + " is not in the Tree.");
   	       return null;
   	       }
   	    Element e = (Element) topList.get(0);
   	    // get the children.
   	    result = e.getChildren();   	       
   	    return result;
   	   }
   	   	
   	
   
   	/**
   	 * MethodName	: getNodes
   	 * Purpose    	: Get all Nodes with the specified tagName and return an Element List.
   	 * Pre		  	: 
   	 * Post		  	: 
   	 * 
   	 * @param tagName
   	 * @return
   	 */
   	public List getNodes(String tagName)
   	   {
   	   	// TODO : all Levels : currently: only one level down the root.   	      	   
  	    List childList=rootElement.getChildren(); 
  	    List result=new ArrayList();
  	    for(int i=0;i<childList.size();i++)
  	       {
	  	       Element child  = (Element) childList.get(i);	
			   String  str	  = child.getName();
			   if ( str.compareTo(tagName)==0 )		      
			      	result.add(child);		      			      		      
			   System.out.println(str);
  	       }   	      	    	          
  	    return result;
   	   }
   	
   	/**
   	 * 
   	 * MethodName	: getParentElement
   	 * Purpose    	: get the Parent of a given element.
   	 * Pre		  	: TRUE
   	 * Post		  	: null is returned if e is the root 
   	 * 					XOR
   	 * 				  parent Element of e is returned.
   	 * 
   	 * @param e	,	Element of the Tree
   	 * 
   	 * @return	parent Element XOR null
   	 */
   	public Element getParentElement(Element e)
   	   {
   	   	return (Element) e.getParent();
   	   }
   	/**
   	 * MethodName	: calcTreeHeight
   	 * Purpose    	: Calculation of the height of a Tree with root Element e.
   	 * Pre		  	: TRUE
   	 * Post		  	: The height of the Tree with root Element e is calculated.
   	 * 				  The height for a single node tree is defined as 0.
   	 * 				  XOR
   	 * 					-1 is returned if the Element e is null. 
   	 * 
   	 * @param e	, Element e the root Element of an XML Tree
   	 * @return	int, the height of the Tree XOR -1 if the Element e is null.
   	 */
   	public int calcTreeHeight(Element e)
   	   {   	   	
   	   	if (e==null)
   	   	   return -1;
   	   	List childList	= e.getChildren();  
   	   	if (childList==null || childList.isEmpty())
   	   	   return 0;
   	   	else 
   	   	   {
   	   	   	int[] childTreeHeight = new int[childList.size()];
   	   	   	for (int i=0;i<childList.size();i++)
   	   	   		childTreeHeight[i]=calcTreeHeight((Element) childList.get(i));
   	   	   return 1 + MathTools.max(childTreeHeight);   	   	
   	   	   }   	   	
   	   }   	
   	/**
   	 * 
   	 * MethodName	: calcTreeHeight
   	 * Purpose    	: same as calcTreeHeight above, but ignoreing elements which are remElements 
   	 * Pre		  	: childs-elements of an remElement are again remElements. 
   	 * Post		  	: 
   	 * 
   	 * @param e
   	 * @param remElements
   	 * @return
   	 */
   	public int calcTreeHeight(Element e, String [] remElements)
   	   {
    	if (e==null || StringTools.isInStringArray(e.getName(),remElements) )
   	   	   return -1; // because later 1 + childHeight (and we want 0).
   	   	List childList	= e.getChildren();  
   	   	if (childList==null || childList.isEmpty()  )
   	   	   return 0;
   	   	else 
   	   	   {
   	   	   	int[] childTreeHeight = new int[childList.size()];
   	   	   	for (int i=0;i<childList.size();i++)
   	   	   		childTreeHeight[i]=calcTreeHeight((Element) childList.get(i),remElements);   	   	   	  	   	   	   	   	   		   	   	  
   	   	   	 //System.out.println(" h(" + e.getName() +")=" + (1 + MathTools.max(childTreeHeight)) );
   	   	   	 return 1 + MathTools.max(childTreeHeight);   	   	   	 
   	   	   }   	   	
   	   }
   	
   	/**
   	 * MethodName	: getAllNodes
   	 * Purpose    	: Get a list of highest Elements matching the names in an String Array. 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @param str
   	 * @return
   	 */
   	public List getAllNodes(String [] str)
   	   {
   	   	List result = new ArrayList();
   	   	List [] l   = new List[str.length];
   	   	for (int i=0;i<str.length;i++)
   	   	   {   	   	    
   	   	    String single 	= str[i];
   	   	   	l[i] 			= getNodes(single);   	   	   	 	   	  
   	   	   }
   	   	 result = concList(l);  
   	   	 return result;
   	   }
   	
   	/**
   	 * MethodName	: findElement
   	 * Purpose    	:  
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @param e
   	 * @param nodeNames
   	 * @return returns the Element of the first nodeName found AND  highest Level in the Tree.
   	 * 				XOR null when not found 
   	 */
   	public  Element findElement(Element e,String[] nodeNames)
   	   {
   	    int level = 0;
   	    // recursive traversal level-wise.   	   	
   	   	if (e==null)
   	   	   return null;
   	   	else if (StringTools.isInStringArray(e.getName(),nodeNames))
   	   	   return e;
   	   	else 
   	   	   {
   	   	   	// search per level.
   	   	   	do
   	   	   	   {
   	   	   	   	level = level + 1;
   	   	   	   	List nextLevelElements = getLevelNodes(e,level);
   	   	   	   	if (nextLevelElements.isEmpty())
   	   	   	   	   return null;
   	   	   	   	else
   	   	   	   	   {
   	   	   	   	   	for (int i=0;i<nextLevelElements.size();i++)
   	   	   	   	   	   {
   	   	   	   	   	   	Element child  = (Element) nextLevelElements.get(i);		   	   	   	   	   	     	   	   	
				   	   	if ( StringTools.isInStringArray(child.getName(),nodeNames) )   	   	   	   
				   	   	   return child;   	   	   	   
   	   	   	   	   	   }   	   	   	   	   	  
   	   	   	   	   }
   	   	   	   } while(true); // either we find it or nextLevelElements will be empty at some stage.
   	   	   }	      
   	   }
   	
   	/**
   	 * MethodName	: getLevelNodes
   	 * Purpose    	: Get all Nodes of the Level level 
   	 * Pre		  	: XML-Tree has been build and is not empty AND   	  				  
   	 * 				  level >= 0
   	 * Post		  	: all nodes of the Level leval are returned as a List of Elements.
   	 * 
   	 * @param e		,  Element the root Element of the XML-Tree
   	 * @param level ,  the depth/level in the (Sub)Tree. The root is 0.
   	 * @return	List of Elements. If no elements on the specified level then an empty List will be
   	 * 			returned.		
   	 */
   	public List getLevelNodes(Element e, int level)
   	   {
   	   	List l = new ArrayList();
   	   	if ( e==null )
   	   	   return l; // empty List
   	   	else if (level==0)   	   	   
   	   	    l.add(e);   	   	       	   	      	   		   	   	      	  
   	   	else if (level==1)   	   	   
   	   	   return e.getChildren();
   	   	else
   	   	   {
   	   	   List childList = e.getChildren();
   	   	   if (childList.isEmpty())
   	   	      return l; // empty List 
   	   	   List[] resList 	= new List[childList.size()];   	   	   
   	   	   for (int i=0;i<childList.size();i++)
   	   	      resList[i]=getLevelNodes((Element) childList.get(i),level-1);
   	   	   // concatenate the results.
   	   	   l = concList(resList);   
   	   	   }   	   	
   	   	return l;   	   	
   	   }
   	
    /**
     * MethodName		: toFile
     * Purpose    	: write the YAWL-Tree into an XML-file.
     * Pre		  	: 
     * Post		  	: 
     * 
     * @param fileName
     */
    public void toFile(String fileName)
       {
       	try{
       	   FileWriter writer = new FileWriter(fileName);
       	   new XMLOutputter(Format.getPrettyFormat()).output(doc, writer);
       	   writer.close();
       	}catch(IOException e)
       	{
       	  e.printStackTrace(); 
       	}
       }
           
   	
   	/*>>> HELPER METHODS */
   	/**
   	 * MethodName	: concList
   	 * Purpose    	: Concatenate a List of elements.
   	 * Pre		  	: l is ListArray where non of the entries is null.
   	 * Post		  	: a single list is returned.
   	 * 
   	 * @param l	, ListArray
   	 * @return
   	 */
   	private List concList(List [] l)
   	   {
   	   	List result = new ArrayList();
   	   	for (int i=0;i<l.length;i++)
   	   	   for (int j=0;j<l[i].size();j++)
   	   	      result.add(l[i].get(j));
   	   	return result;
   	   }
	   
   	/* <<< HELPER Methods */
   	
   	public void printTree()
   	   {
   	   	// TODO: should be consistent with toString() ?   	    
   	   	PrintVisitor pV = new PrintVisitor();
   	   	System.out.println(name);
	    pV.visit(this);
	    System.out.println(pV.getOutputString());
   	   }
      /* (non-Javadoc)
       * @see model.backEnd.Tree#insert(model.backEnd.Node)
       */
      public void insert(Node n)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Tree#delete(model.backEnd.Node)
       */
      public void delete(Node n)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Tree#root()
       */
      public Object root()
         {
            // TODO Auto-generated method stub
            return rootElement;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Tree#getSubTrees()
       */
      public Tree[] getSubTrees()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Tree#deleteTree(model.backEnd.Node)
       */
      public void deleteTree(Node n)
         {
            // TODO Auto-generated method stub

         }

      /* (non-Javadoc)
       * @see model.backEnd.Tree#visit()
       */
      public Node visit()
         {
            // TODO Auto-generated method stub
            return null;
         }

      /* (non-Javadoc)
       * @see model.backEnd.Tree#traverse()
       */
      public void traverse()
         {
            // TODO Auto-generated method stub

         }
      
      /* (non-Javadoc)
       * @see java.lang.Object#toString()
       */
      public String toString()
         {
          return rootElement.toString();
         }
      
  	
      /* (non-Javadoc)
       * @see controller.Visitable#accept(controller.Visitor)
       */
      public void accept(Visitor v)
  	   {
         // traverse the XML Tree and do s.th. according to the impl. of the Visitor.
         // traversal is left to right
         traverseElement(v,rootElement,0,0);     
  	   }
      
      /**
       * MethodName		: traverseElement
       * Purpose    	: traverse the XML-Element Tree
       * Pre		  	: correct input Parameter
       * Post		  	: all elements of the XML-Tree have been visited.
       * 				  Actions according to the implementation of the vistors visit-method
       *				  have been started. 					
       * 
       * @param v		, Visitor the visitor object
       * @param e		, Element the XML-(Sub)Tree 
       * @param breadth , int 
       * @param depth	, int the level (depth) in the Tree.
       */
      protected void traverseElement(Visitor v, Element e,int breadth,int depth)
         {
         	List childList	= e.getChildren(); 
         	v.visit(e,breadth,depth);
         	for (int i=0;i<childList.size();i++)
         	   traverseElement(v,(Element) childList.get(i),i,depth + 1);                     	
         }
                 
      
/* simple get and set methods */
   	public Element getRoot()
   	   {
   	    return rootElement;
   	   }
   	
   	public int getTreeHeight()
   	   {
   	   	return treeHeight;
   	   }
   	public int getRelevantTreeHeight()
   	   {
   	    if (this.relevantTreeHeight==-1)
   	       return getTreeHeight();
   	    return this.relevantTreeHeight;
   	   }
   	
   }
