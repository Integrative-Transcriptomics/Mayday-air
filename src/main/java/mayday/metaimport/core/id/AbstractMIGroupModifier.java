package mayday.metaimport.core.id;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.metaimport.core.id.gui.AbstractMIGroupModifierGUI;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 *
 */
public abstract class AbstractMIGroupModifier {

	/**
	 * 
	 */
	protected AbstractMIGroupModifierGUI gui; 

	/**
	 * 
	 */
	protected MIManager miManager;
	
	/**
	 * 
	 * @param idType
	 * @param miManager
	 */
	public AbstractMIGroupModifier(MIManager miManager) {
		this.miManager = miManager;
	}

	/**
	 * 
	 * @return
	 */
	abstract public MIGroup getMIGroup4IdType(String idType);
	
	/**
	 * 
	 */
	public void initGUI() {
		gui = null;
	}
	
	/**
	 * 
	 * @return
	 */
	public AbstractMIGroupModifierGUI getGUI() {
		return gui;
	}
	
	/**
	 * 
	 * @return
	 */
	public abstract String getMIPath4IdType(String idType);
		
	/**
	 * 
	 * @return
	 */
	public MIManager getMIManager() {
		return miManager;
	}

}
