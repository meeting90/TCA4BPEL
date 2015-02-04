/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.ode.bpel.compiler.bom;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Interface common to all BPEL activities. This interface provides methods for
 * manipulating the so-called "common attributes" such as source and target
 * links, activity name, and supress join failure flag.
 */
public class Activity extends JoinFailureSuppressor {

    public Activity(Element el) {
        super(el);
    }

    public String getName() {
        return getAttribute("name", null);
    }

    /**
     * Get the join condition.
     *
     * @return the join condition
     */
    public Expression getJoinCondition() {
        if (is11()) {
            return isAttributeSet("joinCondition")
                ? new Expression11(getElement(),getElement().getAttributeNode("joinCondition")) : null;
        }

        Targets targets = getFirstChild(Targets.class);
        return targets == null ? null : (Expression) targets.getFirstChild(Expression.class);
    }

    /**
     * Get the {@link LinkSource}s for this activity.
     *
     * @return set of {@link LinkSource}s
     */
    public List<LinkSource> getLinkSources() {
        if (is11()) {
            return getChildren(LinkSource.class);
        }

        Sources sources = getFirstChild(Sources.class);
        if (sources == null)
            return Collections.emptyList();
        return sources.getChildren(LinkSource.class);
    }

    /**
     * Get the {@link LinkTarget}s for this activity.
     *
     * @return set of {@link LinkTarget}s
     */
    public List<LinkTarget> getLinkTargets() {
        if (is11()) {
            return getChildren(LinkTarget.class);
        }

        Targets targets = getFirstChild(Targets.class);
        if (targets == null )
            return  Collections.emptyList() ;

        return targets.getChildren(LinkTarget.class);

    }
    /** generate xpath of activity  **/
    public String getXpath(){
    	Node parent = null;
		Stack<Node> stack = new Stack<Node>();
		StringBuffer buffer = new StringBuffer();
	
		stack.push(this.getElement());
		
		parent = this.getElement().getParentNode();
		
		while(null != parent && parent.getNodeType() != Node.DOCUMENT_NODE){
			stack.push(parent);
			parent = parent.getParentNode();
		}
		
		Node currentNode = null;
		while(! stack.isEmpty() && null != (currentNode = stack.pop())){
			boolean handled = false;
			if(currentNode.getNodeType() == Node.ELEMENT_NODE){
				Element e = (Element) currentNode;
				//root element
				if(buffer.length() == 0){
					buffer.append(currentNode.getLocalName());
					if(currentNode.hasAttributes()){
						if(e.hasAttribute("name")){
							buffer.append("[@name = '" + e.getAttribute("name") + "']");
						}
					}
				}else{
					// child element - append slash and element name
					buffer.append("/");
					buffer.append(currentNode.getLocalName());
					if(currentNode.hasAttributes()){
						// see if the element has a name or id attribute
						if(e.hasAttribute("id")){
							// id attribute found - use that
							buffer.append("[@id='" + e.getAttribute("id") + "']");
							handled = true;
						}else if(e.hasAttribute("name")){
							// name attribute found - use that
							buffer.append("[@name='" + e.getAttribute("name") + "']");
							handled = true;
						}
					}
				}
				if(!handled){
					// no known attribute we could use - get sibling index
					int pre_siblings = 1;
					Node pre_sibling = currentNode.getPreviousSibling();
					while(null != pre_sibling){
						if(pre_sibling.getNodeType() == currentNode.getNodeType()){
							if(pre_sibling.getLocalName().equalsIgnoreCase(currentNode.getLocalName())){
								pre_siblings ++;
							}
						}
						pre_sibling = pre_sibling.getPreviousSibling();
					}
					buffer.append("[" + pre_siblings + "]");
				}
				
				
			}
		}
		
		return buffer.toString();
	}
    
}
