/*
 * Created on Jun 3, 2005
 * Purpose: 
 * 
 */
package model.backEnd;

import org.jdom.Element;

/**
 * EmitterPNMLGraphics.java
 * Purpose: 
 * @author Stephan Breutel
 * @version 1.0
 * @created Jun 3, 2005
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
public class EmitterPNMLGraphics
   {

      /**
       * MethodName : EmitterPNMLGraphics
       * Purpose	  : Constructor
       * Pre		  : TRUE
       * Post	      : a EmitterPNMLGraphics object has been created.
       * 
       */
      public EmitterPNMLGraphics()
         {
            super();
            // TODO Auto-generated constructor stub
         }
      
      /**
       * 
       * MethodName		: addRotationElement
       * Purpose    	: Rotate a transition by rotation degrees.
       * Pre		  	: e is PNML-element representing a transition.
       * Post		  	: 
       * 
       * @param e
       * @param rotation
       */
      public static void addRotationElement(Element e, int rotation)
         {
          Element r = new Element("orientation");
          Element v = new Element("value");
          v.addContent((new Integer(rotation)).toString());
          r.addContent(v);
          e.addContent(r);
         }
      
      /**
       * 
       * MethodName		: addGraphicsElement
       * Purpose    	: add an graphics element to the Element e. 
       * Pre		  	:
       * Post		  	: 
       * 
       * @param e
       * @param x
       * @param y
       */
      public static void addGraphicsElement(Element e,double x,double y)
         {
          Element g 	= new Element("graphics");
	  	  Element pos 	= new Element("position");
 	      pos.setAttribute("x",new Integer(((new Double(x)).intValue())).toString());
	  	  pos.setAttribute("y",new Integer(((new Double(y)).intValue())).toString());		  	  
	  	  g.setContent(pos);
	  	  e.addContent(g);
         }    
            
   }
