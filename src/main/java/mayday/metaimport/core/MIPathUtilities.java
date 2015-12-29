package mayday.metaimport.core;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIGroupSelection;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.miotree.Directory;

public class MIPathUtilities {

	/**
	 * 
	 * @param miManager
	 * @param path
	 * @return
	 */
	public static boolean exists(MIManager miManager, String path) {
		Directory root = miManager.getTreeRoot();
		return root.getDirectory(path, false) != null;
	}
	
	/**
	 * 
	 * @param miManager
	 * @param treePath
	 * @param myType
	 * @return
	 */
	public static MIGroup createMIGroup(MIManager miManager, String treePath, String myType) {
		// coordinates of the "last" MGroup e.g.: /Ids/refseq would be refseq
		int start = treePath.lastIndexOf("/");
		int end = treePath.length();
		
		// get MIGroup
		MIGroup miGroup = getMIGroup(miManager, treePath);

		// if there is no MIGroup under treePath
		// create one
		if(miGroup == null) {
			String name, path;
			
			if(start < 0) {
				name = treePath;
				miGroup = miManager.newGroup(myType, name);
			} else {
				name = treePath.substring(start + 1, end);
				path = treePath.substring(0, start);
				miGroup = miManager.newGroup(myType, name, path);
			}
		}
		
		return miGroup;
	}

	/**
	 * 
	 * @param miManager
	 * @param treePath
	 * @return
	 */
	public static MIGroup getMIGroup(MIManager miManager, String treePath) {
		if(exists(miManager, treePath)) {
			MIGroupSelection<MIType> miGroupSelection = miManager.getGroupsForPath(treePath, false);
			return miGroupSelection.get(0);
		}
		else
			return null;
	}

}
