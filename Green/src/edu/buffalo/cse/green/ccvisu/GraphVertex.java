/* This file is part of Green.
 *
 * Copyright (C) 2010 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.ccvisu;

import edu.buffalo.cse.green.editor.model.TypeModel;

/*****************************************************************
 * Represents a vertex of the graph, including name, id, 
 * and several attributes.
 * @author   François Rey
 *****************************************************************/
public class GraphVertex extends ccvisu.GraphVertex {

  public TypeModel me;
  
  /** Constructor.*/
  public GraphVertex(TypeModel me) {
  	    super();
        this.me=me;
  }
  
  @Override
  public boolean equals( Object o )
  {
	  return (o instanceof GraphVertex) && ((GraphVertex)o).me == me;
  }

};

