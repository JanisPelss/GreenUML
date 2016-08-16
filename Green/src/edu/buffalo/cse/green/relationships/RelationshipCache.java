/* This file is part of Green.
 *
 * Copyright (C) 2005 The Research Foundation of State University of New York
 * All Rights Under Copyright Reserved, The Research Foundation of S.U.N.Y.
 * 
 * Green is free software, licensed under the terms of the Eclipse
 * Public License, version 1.0.  The license is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package edu.buffalo.cse.green.relationships;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IType;

import edu.buffalo.cse.green.editor.model.RelationshipModel;

/**
 * Provides a convenient way to store information about which relationships are
 * displayed in the editor, which ones should be added/removed, and cardinality
 * 
 * @author bcmartin
 */
public class RelationshipCache {
	private Map<IType, Map<IType, Map<String, RelationshipModel>>> _models; 
	private Set<RelationshipModel> _changes;
	
	public RelationshipCache() {
		_models = new HashMap<IType, Map<IType, Map<String, RelationshipModel>>>();
		_changes = new HashSet<RelationshipModel>();
	}

	/**
	 * Removes a model from the cache.
	 * 
	 * @param rModel - The model. 
	 */
	public void removeRelationshipModel(RelationshipModel rModel) {
		_models.get(rModel.getSourceType()).get(rModel.getTargetType()).remove(rModel.getPartClass().getName());
	}
	
	/**
	 * Gets the relationship model representing the given relationship
	 * 
	 * @param source - The source <code>IType</code> of the relationship
	 * @param target - The target <code>IType</code> of the relationship
	 * @param name - The name of the part class of the kind of relationship
	 * @return The unique <code>RelationshipModel</code> representing the given
	 * kind of relationship between the given <code>IType</code>s, or null if
	 * it doesn't exist
	 */
	public RelationshipModel getRelationshipModel(IType source, IType target, String name) {
		Map<IType, Map<String, RelationshipModel>> targetMap =
			_models.get(source);
		if (targetMap == null) {
			return null;
		}
		
		// find the class map
		Map<String, RelationshipModel> classMap = targetMap.get(target);
		if (classMap == null) {
			return null;
		}
		
		// find the model
		return classMap.get(name);
	}
	
	/**
	 * Gets the relationship model representing the given relationship or
	 * creates the model if it doesn't exist
	 * 
	 * @param source - The source <code>IType</code> of the relationship
	 * @param target - The target <code>IType</code> of the relationship
	 * @param klass - The part class of the kind of relationship 
	 * @return The unique <code>RelationshipModel</code> representing the given
	 * kind of relationship between the given <code>IType</code>s
	 */
	private RelationshipModel getRelationshipModel(IType source, IType target, Class klass) {
		// find the target map (create it if it doesn't exist)
		Map<IType, Map<String, RelationshipModel>> targetMap =
			_models.get(source);
		if (targetMap == null) {
			targetMap = new HashMap<IType, Map<String, RelationshipModel>>();
			_models.put(source, targetMap);
		}
		
		// find the class map (create it if it doesn't exist)
		Map<String, RelationshipModel> classMap = targetMap.get(target);
		if (classMap == null) {
			classMap = new HashMap<String, RelationshipModel>();
			targetMap.put(target, classMap);
		}
		
		// find the model (create it if it doesn't exist)
		RelationshipModel rModel = classMap.get(klass.getName());
		if (rModel == null) {
			rModel = new RelationshipModel(source, target, klass);
			classMap.put(klass.getName(), rModel);
		}
		
		return rModel;
	}
	
	/**
	 * Adds a relationship to the cache of relationships. 
	 * 
	 * @param source - The source type of the relationship
	 * @param target - The target type of the relationship
	 * @param klass - The class representing the controller part
	 * @param relationship - The identifying features of the relationship
	 */
	public void add(IType source, IType target, Class klass,
			Relationship relationship) {
		RelationshipModel rModel = getRelationshipModel(source, target, klass);
		// get the relationship, if it already exists in the model
		Relationship eRelationship = rModel.contains(relationship);
		
		/* check whether or not the relationship already exists in the model;
		 * if it does, mark it as retained (so that it doesn't get removed from
		 * the cache); if it doesn't, add it to the list of changed
		 * relationships and mark it as retained    
		 */
		if (eRelationship == null) { // new relationship
			rModel.addRelationship(relationship);
			relationship.setRetained(true);
			_changes.add(rModel);
		} else { // existing relationship
			eRelationship.setRetained(true);
		}
	}
	
	/**
	 * Looks through the cache for relationships that have not been marked as
	 * retained; those relationships were not found on this pass through the
	 * relationship recognizers and should be removed from the editor; all
	 * models that have relationships removed are added to the list of changes
	 * 
	 * @return The list of models that have been altered
	 */
	public Set<RelationshipModel> processChanges() {
		for (Map<IType, Map<String, RelationshipModel>> map1
				: _models.values()) {
			for (Map<String, RelationshipModel> map2: map1.values()) {
				for (RelationshipModel rModel : map2.values()) {
					for (Iterator iter = rModel.getRelationships().iterator(); iter.hasNext();) {
						Relationship relationship = (Relationship) iter.next();
						
						if (!relationship.isRetained()) {
							iter.remove();
							_changes.add(rModel);
						} else {
							relationship.setRetained(false);
						}
					}
				}
			}
		}
		
		return _changes;
	}

	/**
	 * @param klass - The part <code>Class</code> that represents the kind of
	 * relationship
	 * @return All relationships of the given kind
	 */
	public List<RelationshipModel> getRelationships(Class klass) {
		List<RelationshipModel> relationships =
			new ArrayList<RelationshipModel>();
		
		for (Map<IType, Map<String, RelationshipModel>> map : _models.values()) {
			for (Map<String, RelationshipModel> map2 : map.values()) {
				if (map2.get(klass.getName()) != null) {
					relationships.add(map2.get(klass.getName()));
				}
			}
		}
		
		return relationships;
	}
	
	/**
	 * @param type - The source type to find relationships for 
	 * @return All relationships that have the given type as their source
	 */
	public Set<RelationshipModel> getRelationships(IType type) {
		Set<RelationshipModel> models = new HashSet<RelationshipModel>();
		
		Map<IType, Map<String, RelationshipModel>> map = _models.get(type);
		if (map == null) {
			return models;
		}
		
		for (Map<String, RelationshipModel> map2 : map.values()) {
			models.addAll(map2.values());
		}
		
		return models;
	}
}
