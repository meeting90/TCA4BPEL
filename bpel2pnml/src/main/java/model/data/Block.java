/**
 * Block.java
 * Purpose:	This class defines methods for a compilation unit. 
 * @author Stephan Breutel
 * @version 1.0
 * @created 14/04/2005
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

import java.util.List;
import org.jdom.Element;
import org.jdom.Attribute;

public class Block
   {
   	protected List[] code;		/* 	the code of the compilation unit */
   	protected List[] ioBlock;	/*	IO into the Block  0... input 1...output */
   	protected String name;		/*  the name of the Block */
   	protected int type;			/*  type of the Block {basic,structured} = {0,1} */
   	protected List info;		/* List of additional information, used initially for access to Tpost for
   								   the integration of EventHandler */  
   	
   	
   	   	
   	
   	/**
   	 * 
   	 * MethodName : Block
   	 * Purpose	  : Constructor
   	 * Pre		  : TRUE
   	 * Post	      : a Block object has been created.
   	 * 
   	 * @param codeList
   	 */
	public Block(List[] codeList)
   	   {
   	   	for (int i=0;i<codeList.length;i++)
   	   	   this.code[i] = codeList[i];   	   
   	   }
   	/**
   	 * 
   	 * MethodName : Block
   	 * Purpose	  : Constructor
   	 * Pre		  : eList.size()=noCode + 2 
   	 * 				eList[noCode] 	is the input Interface
   	 * 				eList[noCode+1]	is the output Interface
   	 * 				eList[noCode+2] is a List with a single entry: the name of the Block.			
   	 * 				
   	 * Post	      : a Block object has been created.
   	 * @param eList
   	 * @param noCode
   	 */   	 
   	public Block(List[] eList, int noCode,int type)
   	   {
   	   	this.code 		= new List[noCode];
   	   	this.ioBlock 	= new List[2];
   	   
   	   	for(int i=0;i<noCode;i++)
   	   	   this.code[i]= eList[i];
   	   	this.ioBlock[0] = eList[noCode];
   	   	this.ioBlock[1]	= eList[noCode+1];
   	   	this.name		= (String) eList[noCode+2].get(0);
   	   	this.type		= type;
   	   }
   	
   	/**
   	 * 
   	 * MethodName	: setInfo
   	 * Purpose    	: Used to set additional information to access important information
   	 * 				  later during the translation process.
   	 * 				  For example: postTrans (the Element of the postTransition) for a structured
   	 * 				  activity is saved. 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @param i_
   	 */
 
   	public void setInfo(List i_)
   	   {
   	    info = i_;
   	   }
   	
   	/**
   	 * 
   	 * MethodName	: getInfo
   	 * Purpose    	: Get the information set previosly with setInfo. 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @return
   	 */
   	public List getInfo()
   	   {
   	    return info;
   	   }   	  
   	
   	/**
   	 * 
   	 * MethodName	: getBlockList
   	 * Purpose    	: wrap all the information  of the compilation block into an Array of Lists.
   	 * 				  IMPORTANT: DUE TO THE MIX-UP (see cleanups.txt) of the res[i] this method
   	 * 				  WOULD PRODUCE INCORRECT RESULTS.... 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @return
   	 */
/*   	
   	public List[] getBlockList()
   	   {
   	   	// transform all the Block information into an Array of Lists;
   	   	int n = code.length+ioBlock.length+1;
   	   	List [] res = new List[n];
   	   	
   	   	for(int i =0;i<n-3;i++)
   	   	   res[i] = code[i];
   	   	res[n-3] = ioBlock[0];
   	   	res[n-2] = ioBlock[1];
   	   	res[n-1] = new ArrayList();
   	   	res[n-1].add(this.name);
   	   	
   	   	return res;
   	   }
*/   	   
   	
   	public void addListArray(List[] l)
   	   {
   	    if ( l==null)
   	       return;
   	   	for (int i=0;i<l.length && i<code.length;i++)
   	   	   code[i].addAll(l[i]);
   	   	for(int i=code.length;i<l.length && i<code.length+2;i++)
   	   	   ioBlock[i-code.length].addAll(l[i]);   	
   	   }
   	/**
   	 * 
   	 * MethodName	: getPosElement
   	 * Purpose    	: 
   	 * Pre		  	: (e is an Element which has a direct child graphics
   	 * 					AND	graphics has child position AND position has Attributes x,y)
   	 * 				  XOR
   	 * 					(e has no graphics child )
   	 * Post		  	: 	e has graphics child => pos[0] = xpos of e AND pos[1] = ypos of e
   	 * 					e has no graphics child => pos[0] = -1 AND pos[1] = -1
   	 * 
   	 * @param 	e,	Element
   	 * @return pos, int[] 
   	 */   
   	public double[] getPosElement(Element e)
   	   {
   	    double [] pos = new double[2];
   	    pos[0] = -1;
   	    pos[1] = -1;
   	    
   	    Element eGraphics = e.getChild("graphics",e.getNamespace());
   	    if (eGraphics!=null)
   	       {
   	       	Element ePos 	= eGraphics.getChild("position",eGraphics.getNamespace());
   	       	Attribute xpos 	= ePos.getAttribute("x");
   	       	Attribute ypos 	= ePos.getAttribute("y");
   	       	pos[0] 			= (new Double(xpos.getValue())).doubleValue();
   	       	pos[1]			= (new Double(ypos.getValue())).doubleValue();
   	       }
   	          	      	  
	   	return pos;   	
   	   }
   	
   	public double[] getPosReadyPlace()
   	   {
   	   	return getPosElement((Element) ioBlock[0].get(0));
   	   }
   	
   	public double[] getPosFinishPlace()
   	   {
   	   	return getPosElement((Element) ioBlock[1].get(0));
   	   }
   	
   	public Element getSkipStartPlace()
   	   {
   	   	return (Element) this.ioBlock[0].get(1);
   	   }
   	public Element getSkipEndPlace()
   	   {
   	   	return (Element) this.ioBlock[1].get(1);
   	   }
   	
   	public Element getReadyPlace()
   	   {
   	    return (Element) this.ioBlock[0].get(0);
   	   }
   	
   	public Element getFinishPlace()
   	   {
   	   	return (Element) this.ioBlock[1].get(0);
   	   }
   	
   	public int getBlockType()
   	   {
   	   	return type;
   	   }
    /**
     * 
     * MethodName	: getCode
     * Purpose    	: return the code of the Block  
     * Pre		  	: 
     * Post		  	: 
     * 
     * @return	List[], code of the Block
     */
   	public List[] getCode()
   	   {
   	   	return code;
   	   }
   	/**
   	 * 
   	 * MethodName	: getName
   	 * Purpose    	: 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @return
   	 */
   	public String getName()
   	   {
   	   	return name;
   	   }
   	
   	public int getCodeLength()
   	   {
   	   	return code.length;
   	   }
   	/**
   	 * 
   	 * MethodName	: getIOBlock
   	 * Purpose    	: 
   	 * Pre		  	:
   	 * Post		  	: 
   	 * 
   	 * @return
   	 */
   	 public List[] getIOBlock()
   	   {
   	   	return ioBlock;
   	   }
   	 
   	 public List getInput()
   	    {
   	      return ioBlock[0];
   	    }
   	 
   	 public List getOutput()
   	    {
   	      return ioBlock[1];
   	    }
   	
   }
