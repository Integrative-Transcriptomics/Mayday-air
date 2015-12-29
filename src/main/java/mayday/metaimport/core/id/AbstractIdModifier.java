package mayday.metaimport.core.id;

import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JComponent;

import mayday.core.meta.MIManager;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 * Some ids need to modify the ids that are provided. E.g.: KEGG needs a specific prefix.
 */
public abstract class AbstractIdModifier {

	/**
	 *  JComponent for user interaction
	 */
	protected JComponent component;

	/**
	 * Type this IdModifier works on
	 */ 
	protected String idType;

	/**
	 * 
	 */
	protected MIManager miManager;
	
	/**
	 * Constructor.
	 * @param idType this IdModifier to be used for.
	 */
	public AbstractIdModifier(String idType) {
		this.idType = idType;
	}
	
	/**
	 * Constructor.
	 * @param idType
	 * @param miManager
	 */
	public AbstractIdModifier(String idType, MIManager miManager) {
		this.idType = idType;
		this.miManager = miManager;
	}
	
	/**	
	 * Initialize components of the gui for user interaction.
	 * Override it for your needs.
	 */
	public void initComponent() {
		component = null;
	}
	
	/**
	 * Implement your modification of the id
	 * @param id to be modified
	 * @return the modified id
	 */
	abstract protected String manufactureId(String id);
	
	/**
	 * Initialize the gui for user interaction.
	 * @return the gui
	 */
	public JComponent getComponent() {
		return component;
	}
	
	/**
	 * the idModifier is identified by the idType it is used for.
	 * Per AnnotationProvider and IdType there is only one idModifier allowed.
	 */
	public String toString() {
		return idType;
	}
	
	/**
	 * Returns the idType this modifies is used for.
	 * @return the idType this modifier is used for
	 */
	public String getIdType() {
		return idType;
	}

	/**
	 * 
	 * @param miManager
	 */
	public void setMIManager(MIManager miManager) {
		this.miManager = miManager;
	}
	
	/**
	 * 
	 * @return
	 */
	public MIManager getMIManager() {
		return miManager;
	}

	/**
	 * 
	 * @return
	 */
	public String getPath() {
		return  IdSettings.IDPATH + "/" + idType;
	}

	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		return getErrorLog().size() == 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection<String> getErrorLog(){
		return new ArrayList<String>();
	}
	
}
