package mayday.metaimport.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mayday.core.pluma.AbstractPlugin;
import mayday.metaimport.core.id.AbstractIdModifier;
import mayday.metaimport.core.gui.AnnotationProviderGUI;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $ This class represents an Provider where you setup
 *          your Connection and add some AnnotationAdders that use this
 *          Connection to retrieve some annotation data.
 */
abstract public class AbstractAnnotationProvider extends AbstractPlugin {

	/**
	 * holds the status of the provider - true: can be used, false: inactive
	 * mainly depends on initConnection - if connections succeeds, set to true
	 */
	protected boolean status;

	/**
	 * All possible annotation data that this AnnotationProvider has to offer
	 */
	@SuppressWarnings("unchecked")
	// don't care about type
	protected Map<String, AbstractAnnotationAdder> adders;

	/**
	 * Name for the AnnotationProvider
	 */
	protected String name;

	/**
	 * Short description for the annotation provider
	 */
	protected String description;

	/**
	 * MIPathUtilities to a logo to be displayed, if there is one
	 */
	protected String logoPath;

	/**
	 * View to be used for this AnnotationProvider
	 */
	protected AnnotationProviderGUI panel;

	/**
	 * Ids supported by this AnnotationProvider Some AnnotationProvider may
	 * require an IdModifier. E.g.: GO database works fine with SGD ids where
	 * KEGG needs some extra prefix
	 */
	protected Map<String, AbstractIdModifier> supportedIds;

	/**
	 * 
	 */
	protected Collection<String> errorLog;
	
	/**
	 * Constructor.
	 * 
	 * @param name
	 *            for the AnnotationProvider
	 * @param decription
	 *            for the AnnotationProvider
	 */
	@SuppressWarnings("unchecked")
	// don't care about type of AbstractAnnotationAdder
	protected AbstractAnnotationProvider(String name, String decription) {
		this.name = name;
		this.description = decription;

		// pessimistic - check delegated to implementing class in initConnection
		status = false;

		// initializations
		adders = new HashMap<String, AbstractAnnotationAdder>();
		supportedIds = new HashMap<String, AbstractIdModifier>();
		errorLog = new ArrayList<String>();
	}

	/**
	 * Returns a map referenced by the name of all possible AnnotationAdders
	 * this AnnotationProivder offers.
	 * 
	 * @return all possible AnnotationAdders this AnnotationProivder offers
	 */
	@SuppressWarnings("unchecked")
	public Map<String, AbstractAnnotationAdder> getAdders() {
		return adders;
	}

	/**
	 * Returns name for thisw AnnotationProvider.
	 * 
	 * @return name for this AnnotationProvider
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns description for this AnnotationProvider.
	 * 
	 * @return description for this AnnotationProvider
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the path path to a logo to be used in the gui. Set it in the
	 * constructor of your subclass.
	 * 
	 * @return path to a logo to be used in the gui
	 */
	public String getLogoPath() {
		return logoPath;
	}

	/**
	 * Initialize the AnnotationProvider. Call initConnection where status is
	 * set to true upon on success. When the connection is correctly
	 * established, proceed with initialization, quit otherwise.
	 */
	protected final void initProvider() {
		// try to initialize the connection to your AnnotationProvider
		// (database, service ...) and show the success
		status = initConnection();

		// it this did not work cancel any further initialization
		if (status == false)
			return;

		// initialize the AnnotationAdder
		initAdders();

		// set the supported ids for this provider
		initSupportedIds();
	}

	/**
	 * Initialize the connection to be used by the AnnotationProvider.
	 * 
	 * @return true, when the connection could be established and false
	 *         otherwise
	 */
	abstract protected boolean initConnection();

	/**
	 * Initialize the AnnotationAdders.
	 */
	abstract protected void initAdders();

	/**
	 * Initialize/adjust supportedIds that can be used by this
	 * AnnotationProvider.
	 */
	abstract protected void initSupportedIds();

	/**
	 * Returns a map of IdModifier referenced by the id name that can be used by
	 * this AnnotationProvider.
	 * 
	 * @return ids that can be used to query the AnnotationProvider
	 */
	public Map<String, AbstractIdModifier> getSupportedIds() {
		return supportedIds;
	}

	/**
	 * Returns the status of the AnnotationProvider. Call initProvider to
	 * eventually change the status.
	 * 
	 * @return the status of the AnnotationProvider
	 */
	public boolean getStatus() {
		return status;
	}

	public Collection<String> getErrorLog(){
		return errorLog;
	}
	
	/**
	 * 
	 */
	public void resetAdder() {
		adders.clear();
		supportedIds.clear();
		
		initAdders();
		initSupportedIds();
	}
	
}
