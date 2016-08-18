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

import static edu.buffalo.cse.green.constants.XMLConstants.XML_TAB;

import java.util.Map;
import java.util.Stack;

/**
 * Creates XML code and decodes XML strings.
 * 
 * @author bcmartin
 */
public class XMLConverter {
	private StringBuffer _buf = new StringBuffer();

	private Stack<XMLNode> _headers = new Stack<XMLNode>();

	/**
	 * @param xml - The contents of the XML file.
	 * @return The node representation of the XML.
	 * @throws ArrayIndexOutOfBoundsException
	 * @author bcmartin
	 * @author zgwang
	 */
	public XMLNode getDecodedXML(String xml)
			throws ArrayIndexOutOfBoundsException {
		int pos;
		String line;
		XMLNode node = new XMLNode("!root");
		XMLNode currentNode = node;

		while (true) {
			pos = xml.indexOf('>');
			if (pos == -1) break;
			pos++; //Account for position of line break character (/n)
		
			line = xml.substring(0, pos);
			xml = xml.substring(pos + 1);
			
			//Trims off remaining /n before tags
			while(xml.length() > 0 && xml.charAt(0) == '\n')
				xml = xml.substring(1);
			
			if (line.length() < 3) continue;

			currentNode = addLineToNode(currentNode, line);
		}

		return node;
	}

	/**
	 * @return The encoded XML.
	 */
	public String getEncodedXML() {
		_headers.clear();
		return _buf.toString();
	}

	/**
	 * Adds a header to the stack.
	 * 
	 * @param header - The node to add.
	 */
	public void pushHeader(XMLNode header) {
		appendToBuffer("<" + header + ">");
		_headers.push(header);
	}

	/**
	 * Adds a header to the stack. A node with the given name will be created
	 * and added to the stack.
	 * 
	 * @param header - The name of the node to create and add.
	 */
	public void pushHeader(String header) {
		pushHeader(new XMLNode(header));
	}

	/**
	 * Creates an open header
	 * @param header - The name of the header.
	 * @param value - The value to assign to the header.
	 */
	public void openHeader(String header, String value) {
		appendToBuffer("<" + header + "=\"" + value + "\">");
		_headers.push(new XMLNode(header));
	}

	/**
	 * Removes a header from the stack.
	 */
	public void popHeader() {
		appendToBuffer("</" + _headers.pop() + ">");
	}

	/**
	 * Writes a closed XML header with the specified name and value.
	 * 
	 * @param header - The name.
	 * @param value - The value.
	 */
	public void writeKey(String header, Object value) {
		appendToBuffer("<" + header + "=\"" + value + "\"/>");
	}

	/**
	 * Writes all key-value pairs in the given map as closed XML headers.
	 * 
	 * @param map - The map.
	 */
	public void writeMap(Map<String, String> map) {
		for (String key : map.keySet()) {
			writeKey(key, map.get(key).toString());
		}
	}

	/**
	 * Writes the given string to the buffer, appending tabs as appropriate.
	 * 
	 * @param appendString - The string to append.
	 */
	private void appendToBuffer(String appendString) {
		for (int x = 0; x < _headers.size(); x++)
			_buf.append(XML_TAB);

		_buf.append(appendString + "\n");
	}

	/**
	 * Adds the given line of text to the current node, manipulating headers as
	 * appropriate.
	 * 
	 * @param node - The node.
	 * @param line - The text.
	 * @return - The new node.
	 */
	private XMLNode addLineToNode(XMLNode node, String line) {
		line = line.substring(line.indexOf("<")).trim();
		
		if (line.substring(0, 2).equals("</")) {
			// close header
			return _headers.pop();
		} else {
			int len = line.length();

			if (line.substring(len - 2, len).equals("/>")) {
				// write value to map
				int pos = line.indexOf("=");
				String key = line.substring(1, pos);
				String val = line.substring(pos + 2, len - 3);
				node.getAttributes().put(key, val);
				return node;
			} else {
				// open header
				pushHeader(node);
				String name = line.substring(1, len - 1);
				XMLNode newNode = new XMLNode(name);
				node.addChild(newNode);
				return newNode;
			}
		}
	}
}