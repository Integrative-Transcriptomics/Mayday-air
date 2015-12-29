package mayday.metaimport.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.JComponent;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:56 $
 * 
 * Class uses the connection established by the provider it belongs to,
 * retrieves one specific type of annotation and adds it to some MIO that
 * implements at least MIType under some specific MIPathUtilities.
 * 
 */
abstract public class AbstractAnnotationAdder<T> {

	/**
	 * The name of the annotation, like GO-Term.
	 */
	private String name;

	/**
	 * Some description.
	 */
	private String description;

	/**
	 * Reference to the AnnotationProvider this Adder belongs to.
	 */
	private AbstractAnnotationProvider provider;

	/**
	 * Some optional gui if an Adder offers some adjustment
	 */
	protected JComponent gui;

	/**
	 * Constructor.
	 * 
	 * @param name
	 *            Sets the name for the AnnotationAdder.
	 * @param description
	 *            Sets the description for the AnnotationAdder.
	 */
	public AbstractAnnotationAdder(String name, String description,
			AbstractAnnotationProvider provider) {
		this.name = name;
		this.description = description;

		// ensure that there is always a provider
		if (provider == null)
			throw new RuntimeException(
					"Provider cannot be null! An Annotation adder needs to belong to a Provider!");

		this.provider = provider;
	}

	/**
	 * Returns the description for the AnnotationAdder.
	 * 
	 * @return the description for the AnnotationAdder
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the name for the AnnotationAdder
	 * 
	 * @return the name for the AnnotationAdder
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds a relation of some data of type T and a mioExtendable in group.
	 * 
	 * @param object
	 *            the data to be added
	 * @param group
	 *            the group to establish the MIType <-> mioExtendable relation
	 * @param mioExtendable
	 *            some mioExtendable to be related to object
	 */
	abstract public void addAnnotation(T object, MIGroup group,
			Object mioExtendable);

	/**
	 * Retrieves annotation data referenced by id and wraps it up in a List
	 * 
	 * @param id
	 *            the id to be used to retrieve annotation data
	 * @return the retrieved data wrapped up in a List
	 */
	abstract public List<T> getAnnotation(String id);

	/**
	 * Creates a MIGroup in the specified miManager. Ideally one uses this
	 * MIGroup in addAnnotation.
	 * 
	 * @see{RetrieveTask}
	 * @param miManger
	 *            the miManager to be used
	 * @return the MIGroup where to store, or retrieve annotation data this
	 *         AnnotationAdder handles.
	 */
	abstract public MIGroup createMIGroup(MIManager miManger);

	/**
	 * Initialize the gui to adjust some settings. Override in your subclass to
	 * change behavior.
	 */
	public void initGUI() {
		gui = null;
	}

	/**
	 * Returns the gui to change parameters for this AnnotationAdder.
	 * 
	 * @return the gui where some options are stored
	 */
	public JComponent getGUI() {
		return gui;
	}

	/**
	 * Returns the provider this AnnotationAdder belongs to.
	 * 
	 * @return the Provider this AnnotationAdder belongs to
	 */
	public AbstractAnnotationProvider getProvider() {
		return provider;
	}

	/**
	 * Return true if adder is setup correctly and false otherwise.
	 * 
	 * @return
	 */
	public boolean isValid() {
		return getErrorLog().size() == 0;
	}

	/**
	 * Returns a collection of error messages that were generated due to false
	 * adder configuration. Override to satisfy your needs.
	 * 
	 * @return Returns a collection of error messages.
	 */
	public Collection<String> getErrorLog() {
		return new ArrayList<String>();
	}

}
