/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

/**
 * 
 */
package edu.buffalo.cse.green.editor.model.commands;

import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.commands.Command;

import ccvisu.GraphData;
import ccvisu.GraphEdge;
import ccvisu.Minimizer;
import ccvisu.MinimizerBarnesHut;
import ccvisu.Options;
import edu.buffalo.cse.green.ccvisu.CCVisuUtil;
import edu.buffalo.cse.green.ccvisu.GraphVertex;
import edu.buffalo.cse.green.editor.DiagramEditor;
import edu.buffalo.cse.green.editor.model.AbstractModel;
import edu.buffalo.cse.green.editor.model.RelationshipModel;
import edu.buffalo.cse.green.editor.model.RootModel;
import edu.buffalo.cse.green.editor.model.TypeModel;

/**
 * @author zgwang
 *
 */
public class AutoArrangeCommand extends Command {

	private int[][] opos;
	private int[][] npos;
	private Vector<TypeModel> _m;
	
	/**
	 * 
	 */
	public AutoArrangeCommand() {
		_m = new Vector<TypeModel>();
	}
	
	public void undo() {
		for( int i=0; i<_m.size(); i++)
			_m.get(i).setLocation(opos[i][0], opos[i][1]);
	}
	
	public void redo() {
		for( int i=0; i<_m.size(); i++)
			_m.get(i).setLocation(npos[i][0], npos[i][1]);
	}
	
	public void execute() {
		DiagramEditor editor = DiagramEditor.getActiveEditor();
		RootModel root = editor.getRootModel();
//		List<AbstractModel> allModels = root.getChildren();
		List<RelationshipModel> rels = root.getRelationships();
		
		/*
		 * 
			Dimension dim = m.getSize();
			if(dim.height == -1 && dim.width == -1) {
				//Box is default size, need to get real size instead.
				dim = f.getLayoutManager().getPreferredSize(f.getParent(), -1, -1);
			}		 * 
for(AbstractModel m : allModels) {
			IFigure f = activeEditor.getRootPart().getPartFromModel(m).getFigure();
		 * 
		 */
		
		/*for(RelationshipModel relModel : rels) {
			String[] interfaces = relModel.getSourceType().getSuperInterfaceNames();
			IType source = relModel.getSourceType();
			IType target = relModel.getTargetType();
			TypeModel sModel = root.getModelFromType(source);
			TypeModel tModel = root.getModelFromType(target);

			
			
			System.out.println(sModel.getLocation());
			System.out.println(tModel.getLocation());
			
			for(int a = 0; a < interfaces.length; a++) {
				if(interfaces[a].equals(target.getElementName())) {
					IFigure sFig = editor.getRootPart().getPartFromModel(sModel).getFigure();
					IFigure tFig = editor.getRootPart().getPartFromModel(tModel).getFigure();
					sFig.setLocation(new Point(50, 50));
					tFig.setLocation(new Point(50, 50));

					Dimension sDim = getRealSize(sFig);
					Dimension tDim = getRealSize(tFig);
					
					int horizCenter = tModel.getLocation().x + (tDim.width / 2);
					sModel.setLocation(horizCenter - (sDim.width / 2),
							tModel.getLocation().y + tDim.height + 50);
				}
				
			}
		}*/
		List<AbstractModel> mods = root.getChildren();
		//List<Map.Entry<Integer, TypeModel>> l = new LinkedList<Map.Entry<Integer, TypeModel>>();
		GraphData gd = new GraphData();
		for(AbstractModel m : mods) {
			//TODO Insert heuristic layout algorithm here.
			//new org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm();
			
			/*System.out.println(models.getBounds() + "; " +
					models.getDrawnSize() + "; " +
					models.getSize());*/
			//models.setLocation(new Point(60, 60));
			if(m instanceof TypeModel)
			{
				final TypeModel n = (TypeModel)m;
				GraphVertex me = new GraphVertex(n);
				me.id = gd.vertices.size();
				me.name = "" + me.id;
				Dimension size = editor.getRootPart().getPartFromModel(n).getFigure().getSize(); 
				me.degree = n.getIncomingEdges().size() +
							n.getOutgoingEdges().size() + (size.height * size.width /20000.0f);
				//System.out.println("1: " + size.height + ", 2: " + size.width);
				me.isSource = me.degree > 0;
				me.pos.x = n.getLocation().x/200.0f;
				me.pos.y = n.getLocation().y/200.0f;
				if( !gd.vertices.contains(me) )
				{
					gd.vertices.add(me);
					_m.add(n);
				}
				else
					me = (GraphVertex)gd.vertices.get( gd.vertices.indexOf(me) );
				/*int card = 0, i=0;
				card += n.getIncomingEdges().size();
				card += n.getOutgoingEdges().size();
				for(Map.Entry<Integer, TypeModel> o : l)
				{
					if( o.getKey() <= card )
						break;
					i++;
				}
				final int c = card;
				l.add(i, new Map.Entry<Integer, TypeModel>() {
					private Integer _k;
					private TypeModel _v;
					{ _k = c; _v = n; }
					public Integer getKey() { return _k; }
					public TypeModel getValue() { return _v; }
					public TypeModel setValue(TypeModel value) { return _v = value; }
					@Override public boolean equals(Object o) { return ((Map.Entry<Integer, TypeModel>)o).getValue() == this.getValue(); }
				});*/
				for(RelationshipModel e : n.getOutgoingEdges())
				{
					TypeModel y = e.getTargetModel();
					if (n.equals(y)) continue; // no reflexive graph
					GraphVertex you = new GraphVertex(y);
					you.id = gd.vertices.size();
					you.name = "" + you.id;
					you.degree = y.getIncomingEdges().size() +
								 y.getOutgoingEdges().size() /*(y.getSize().height + y.getSize().width)*/;
					you.isSource = you.degree > 0;
					you.pos.x = y.getLocation().x/200.0f;
					you.pos.y = y.getLocation().y/200.0f;
					if( !gd.vertices.contains(you) )
					{
						gd.vertices.add(you);
						_m.add(y);
					}
					else
						you = (GraphVertex) gd.vertices.get( gd.vertices.indexOf(you) );
					
					// have me, you
					// create edge
					
					if (me.id!=you.id) {
						GraphEdge ed = new GraphEdge();
						ed.x = me.id;
						ed.y = you.id;
						ed.w = 1.0f;
						gd.edges.add(ed);
					}
				}
			}
		}
		
		//final GraphData fn = gd;
		
		//CCVisu.initializeLayout(gd, 2, null);
		
		Options options = CCVisuUtil.newOptions(gd, 100, 3, 1, false, false, 2.001f, null, false);
		Minimizer me = new MinimizerBarnesHut(options);
		/* new GraphEventListener() {
		public void onGraphEvent(GraphEvent evt) {
				for( int i=0; i<fn.vertices.size(); i++ )
				{
					System.out.println("putting vertex " + i + " to (" + fn.pos[i][0] + ", " + fn.pos[i][1] + ").");
					fn.vertices.get(i).me.setLocation((int)(fn.pos[i][0]*1000), (int)(fn.pos[i][1]*1000));
				}
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		}*/
		
		me.minimizeEnergy();
		
		//System.out.println("computed " + gd.vertices.size() + " vertices.");
		
		// normalize
		float lx = 999, ly = 999;
		for( ccvisu.GraphVertex v : gd.vertices )
		{
			if( v.pos.x < lx )
				lx = v.pos.x;
			if( v.pos.y < ly )
				ly = v.pos.y;
		}
		
		for( ccvisu.GraphVertex v : gd.vertices )
		{
			v.pos.x -= lx;
			v.pos.y -= ly;
		}
		
		opos = new int[_m.size()][2];
		npos = new int[_m.size()][2];
		
		for( int i=0; i<gd.vertices.size(); i++ )
		{
			//System.out.println("putting vertex " + i + " to (" + gd.pos[i][0] + ", " + gd.pos[i][1] + ").");
			opos[i][0] = _m.get(i).getLocation().x;
			opos[i][1] = _m.get(i).getLocation().y;
			GraphVertex v = (GraphVertex) gd.vertices.get(i); 
			v.me.setLocation((int)(v.pos.x*200), (int)(v.pos.y*200));
			npos[i][0] = v.me.getLocation().x;
			npos[i][1] = v.me.getLocation().y;
		}
		
		/*class noode {
			public List<noode> childs;
			public TypeModel me;
			public noode(TypeModel m)
			{
				me = m;
				childs = new ArrayList<noode>();
			}
		};
		
		noode dad = new Object() {

			private List<Map.Entry<Integer, TypeModel>> l;
			
			public noode go(List<Map.Entry<Integer, TypeModel>> li) {
				l = li;
				noode r = new noode(null);
				while(!l.isEmpty())
				{
					noode me = new noode(l.get(0).getValue());
					r.childs.add(me);
					l.remove(me);
					recursive(me);
				}
				return r;
			}

			private void recursive(noode r) {
				
			}
			
		}.go( l );
		for(Map.Entry<Integer, TypeModel> e : l)
		{
			//e.getValue().setLocation(60, 60);
			
		}*/
		
		editor.checkDirty();
	}

}
