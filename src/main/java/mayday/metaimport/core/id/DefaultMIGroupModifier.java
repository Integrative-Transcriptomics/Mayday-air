package mayday.metaimport.core.id;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.miotree.Directory;

/**
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 *
 */
public class DefaultMIGroupModifier extends AbstractMIGroupModifier {

	/**
	 * Constructor.
	 * @param idType
	 * @param miManager
	 */
	public DefaultMIGroupModifier(MIManager miManager) {
		super(miManager);
	}

	@Override
	public MIGroup getMIGroup4IdType(String idType) {
		Directory root = miManager.getTreeRoot();
		Directory node = root.getDirectory(getMIPath4IdType(idType), false);
		
		if(node == null)
			return null;
		
		return node.getGroup();
	}
	
	@Override
	public String getMIPath4IdType(String idType) {
		return "/" + IdSettings.IDPATH + "/" + idType;
	}

}
