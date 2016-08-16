/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A node that hold XML information. Maintains a hierarchy among the keys in an
 * XML file. The structure of an <code>XMLNode</code> hierarchy is a tree.
 * 
 * @author bcmartin
 */
public class XMLNode {
	private Map<String, String> _attributes;

	private List<XMLNode> _children;

	private String _name = null;

	public XMLNode(String name) {
		_attributes = new HashMap<String, String>();
		_children = new ArrayList<XMLNode>();
		_name = name;
	}

	/**
	 * @return The node's children.
	 */
	public List<XMLNode> getChildren() {
		return _children;
	}

	/**
	 * @return The node's child with the specified name.
	 * 
	 * @param tagName - The name.
	 */
	public XMLNode getChild(String tagName) {
		for (XMLNode child : _children) {
			if (child.getName().equals(tagName)) { return child; }
		}

		return null;
	}

	/**
	 * Adds the specified child to the current node.
	 * 
	 * @param node - The node to add.
	 */
	public void addChild(XMLNode node) {
		_children.add(node);
	}

	/**
	 * @return The <code>HashMap</code> that represents the attributes of the
	 * node.
	 */
	public Map<String, String> getAttributes() {
		return _attributes;
	}

	/**
	 * @return The name of the node.
	 */
	public String getName() {
		return _name;
	}

	/**
	 * @return An integer attribute of the node.
	 */
	public int getIntAttribute(String key) {
		return Integer.decode(getAttribute(key)).intValue();
	}

	/**
	 * @param key - The attribute to request.
	 * @return The requested attribute of the node.
	 */
	public String getAttribute(String key) {
		return (String) _attributes.get(key);
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return recursiveToString(0);
	}

	/**
	 * Called by toString(); prints the entire tree structure of the node.
	 * 
	 * @param level - The depth of the XML hierarchy.
	 * @return The string representation of the nodes.
	 */
	private String recursiveToString(int level) {
		StringBuffer buf = new StringBuffer();
		for (int x = 0; x < level; x++) {
			buf.append("   ");
		}
		buf.append(_name);

		if (!_attributes.toString().equals("{}")) {
			buf.append(" " + _attributes);
		}

		for (XMLNode child : _children) {
			buf.append(child.recursiveToString(level + 1));
		}

		return buf.toString();
	}
}