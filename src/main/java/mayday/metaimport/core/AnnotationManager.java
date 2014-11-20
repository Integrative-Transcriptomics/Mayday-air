package mayday.metaimport.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mayday.core.MasterTable;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.meta.MIManager;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.pluma.prototypes.ProbelistPlugin;
import mayday.core.tasks.TaskStateEvent;
import mayday.metaimport.core.id.IdSettings;
import mayday.metaimport.core.gui.AnnotationManagerGUI;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.4 $, $Date: 2014/11/19 13:46:37 $ Initializes and administers the
 *          AnnotationProviders that can be used to retrieve annotation data.
 * 
 * Abbreviation: adder -> AnnotationAdder provider -> AnnotationProvider
 * 
 */
public class AnnotationManager extends AbstractPlugin implements
		ProbelistPlugin {

	public final static String myType = "PAS.MIO.IMPORT.MANAGER";

	/**
	 * Map of AnnotationProviders that can be used for retrieval referenced by
	 * the name of the provider
	 */
	private Map<String, AbstractAnnotationProvider> name2provider;

	/**
	 * IdSettings for each provider the to be used upon retrieval
	 */
	private Map<AbstractAnnotationProvider, IdSettings> provider2idSettings;

	/**
	 * Selected adders for each provider
	 */
	@SuppressWarnings("unchecked")
	// don't care about type
	private Map<AbstractAnnotationProvider, Set<AbstractAnnotationAdder>> provider2adders;

	/**
	 * ProbeList to be used for the retrieval. Currently AIR supports retrieval
	 * of annotation data on Probe level. Probes are selected by the
	 * functionality of this class being a ProbeList plugin.
	 */
	private List<ProbeList> probeLists;

	/**
	 * MasterTable to be used for the retrieval. Important, since gives the
	 * important reference to the MIManager to be used for this retrieval -
	 * defines where the retrieved annotation data is gone be stored.
	 */
	private MasterTable masterTable;

	/**
	 * 
	 */
	private RetrieveTask retrieveTask;

	/**
	 * Constructor.
	 */
	@SuppressWarnings("unchecked")
	// don't care about type
	public AnnotationManager() {

		// initializations.
		name2provider = new HashMap<String, AbstractAnnotationProvider>();
		provider2idSettings = new HashMap<AbstractAnnotationProvider, IdSettings>();
		// we don't need the generic type AbstractAnnotationAdder here, that is
		// why we SuppressWarnings
		provider2adders = new HashMap<AbstractAnnotationProvider, Set<AbstractAnnotationAdder>>();
	}

	/**
	 * Initialize AnnotationManager. Calls a subclass of AbstractTask to
	 * visualize progress. Searches for classes that implement
	 * AbstractAnnotationProvider, instantiates and calls their initProvider
	 * methods. Show dialogs when no AnnotationProvider can be found.
	 */
	public void startInitTask() {
		// instantiate a new task that takes cares of preparing the retrieveTask
		InitTask initTask = new InitTask(this);

		initTask.start();
		initTask.waitFor();
	}

	/**
	 * Call this method when everything (ProbeLists, MasterTable, IdSettings,
	 * AnnotationAdder) has/have been setup to start the retrieval of the
	 * annotation data.
	 */
	public void startRetrieveTask() {
		// needs to be here and not in the constructor
		retrieveTask = new RetrieveTask(this);

		// register at the global TASK_MONITOR
		retrieveTask.start();
		retrieveTask.waitFor();
	}

	/**
	 * Resets the AnnotationProvider and their views, without breaking
	 * connection.
	 */
	@SuppressWarnings("unchecked")
	public void resetProvider() {
		for (AbstractAnnotationProvider provider : name2provider.values()) {
			// reset the IdSettings of each AnnotationProvider
			provider2idSettings.put(provider, new IdSettings(getMIManager()));

			provider.resetAdder();
		}

		// reset the selected AnnotationAdders
		provider2adders = new HashMap<AbstractAnnotationProvider, Set<AbstractAnnotationAdder>>();
	}

	/**
	 * Returns a Collection of providers that could be successfully initialized
	 * and can be used for retrieval.
	 * 
	 * @return available provider
	 */
	public Collection<AbstractAnnotationProvider> getProviders() {
		return name2provider.values();
	}

	@SuppressWarnings("unchecked")
	public void addProvider(AbstractAnnotationProvider provider) {
		name2provider.put(provider.getName(), provider);
		provider2adders.put(provider, new HashSet<AbstractAnnotationAdder>());
		provider2idSettings.put(provider, new IdSettings(getMIManager()));
	}

	@SuppressWarnings("unchecked")
	public void removeAllProviders() {
		name2provider = new HashMap<String, AbstractAnnotationProvider>();
		provider2adders = new HashMap<AbstractAnnotationProvider, Set<AbstractAnnotationAdder>>();
		provider2idSettings = new HashMap<AbstractAnnotationProvider, IdSettings>();
	}

	@Override
	public void init() {/* don't need it */
	}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(getClass(), myType, new String[] {},
				Constants.MC_PROBELIST, new HashMap<String, Object>(),
				"Michael Piechotta", "piechott@student.uni-tuebingen.de",
				"Annotation Import Manager", "Annotation Import Manager");
		pli.addCategory(MaydayDefaults.Plugins.CATEGORY_METAINFORMATION);
		pli.setMenuName("Import from online databases");
		return pli;
	}

	/*
	 * tries to initialize the AnnotationManager and shows dialog upon success
	 */
	public List<ProbeList> run(List<ProbeList> probeLists,
			MasterTable masterTable) {
		// return value
		List<ProbeList> list = null; // we don't change nor do we generate a
		// new ProbeList

		// focus to TaskManager as long as there is an retrieval
		if (retrieveTask != null
				&& (retrieveTask.getTaskState() == TaskStateEvent.TASK_NEW
						|| retrieveTask.getTaskState() == TaskStateEvent.TASK_RUNNING || retrieveTask
						.getTaskState() == TaskStateEvent.TASK_CANCELLING)) {
			retrieveTask.getGUI().setVisible(true);
			return list;
		}

		// set the values to be used for the queries
		setProbeLists(probeLists); // needed for probes
		setMasterTable(masterTable); // needed for reference to MIManager

		// if there are no AnnotationProviders try to initialize the
		// AnnotationManager
		if (getProviders().size() == 0) {
			startInitTask();
		}

		// If there is still no AnnotationProvider, something is wrong
		if (getProviders().size() == 0) {
			JOptionPane
					.showMessageDialog(
							(JFrame)null,
							"No working annotation provider found! Check your Internet-Connection!",
							"Annotation provider manager error",
							JOptionPane.ERROR_MESSAGE);
			return list;
		}

		// show the AnnotationProviderGUI
		AnnotationManagerGUI annotationManagerView = AnnotationManagerGUI
				.getInstance(this);

		// focus to the existing instance
		annotationManagerView.setVisible(true);

		return list;
	}

	/**
	 * Sets the MasterTable to be used.
	 * 
	 * @param masterTable
	 *            to be used
	 */
	public void setMasterTable(MasterTable masterTable) {
		this.masterTable = masterTable;
	}

	/**
	 * Returns the probeListst that this plugin has been called with.
	 * 
	 * @return the probeLists that this plugin has been called with
	 */
	public List<ProbeList> getProbeLists() {
		return probeLists;
	}

	/**
	 * Sets the probeLists to be used.
	 * 
	 * @param sets
	 *            the probeLists to be used
	 */
	public void setProbeLists(List<ProbeList> probeLists) {
		this.probeLists = probeLists;
	}

	/**
	 * Returns the miManager that is used to store annotation data - changed by
	 * setMasterTable Each DataSet has its own miManager
	 * 
	 * @return the miManager that is actually used
	 */
	public MIManager getMIManager() {
		return masterTable.getDataSet().getMIManager();
	}

	/**
	 * Adds an AnnotationAdder to the Set to be used for the retrieval.
	 * 
	 * @param adder
	 *            to be added
	 * @param provider
	 *            adder belongs to
	 */
	@SuppressWarnings("unchecked")
	public void addAdder4Provider(AbstractAnnotationAdder adder,
			AbstractAnnotationProvider provider) {
		if (adder.getProvider() != provider)
			throw new RuntimeException("Adder belongs to an other provider!");

		Set<AbstractAnnotationAdder> adders = provider2adders.get(provider);
		if (adders == null) {
			adders = new HashSet<AbstractAnnotationAdder>();
			provider2adders.put(provider, adders);
		}

		provider2adders.get(provider).add(adder);
		adders.add(adder);
	}

	/**
	 * Adds all AnnotationsAdders this providers has to the Set to be used for
	 * the retrieval.
	 * 
	 * @param provider
	 */
	@SuppressWarnings("unchecked")
	public void addAllAdders4Provider(AbstractAnnotationProvider provider) {
		Set<AbstractAnnotationAdder> adders = new HashSet<AbstractAnnotationAdder>();
		adders.addAll(provider.getAdders().values());
		provider2adders.put(provider, adders);
	}

	/**
	 * Removes AnnotationAdder from the Set to be used for the retrieval.
	 * 
	 * @param adder
	 *            the to be removed
	 * @param provider
	 *            adder belongs to
	 */
	@SuppressWarnings("unchecked")
	public void removeAdder4Provider(AbstractAnnotationAdder adder,
			AbstractAnnotationProvider provider) {
		if (adder.getProvider() != provider)
			throw new RuntimeException("Adder belongs to an other provider!");

		Set<AbstractAnnotationAdder> adders = provider2adders.get(provider);
		if (adders == null)
			throw new RuntimeException(
					"Cannot remove an adder from an not existing provider!");

		adders.remove(adder);
	}

	/**
	 * Removes all adders provider has.
	 * 
	 * @param annotationProvider
	 */
	public void removeAllAdders4Provider(
			AbstractAnnotationProvider annotationProvider) {
		provider2adders.remove(annotationProvider);
	}

	/**
	 * Returns the active/selected adders for provider.
	 * 
	 * @param provider
	 *            to be queried
	 * @return the adders that are selected and belong to provider
	 */
	@SuppressWarnings("unchecked")
	public Set<AbstractAnnotationAdder> getAdders4provider(
			AbstractAnnotationProvider provider) {
		Set<AbstractAnnotationAdder> adders = provider2adders.get(provider);
		return adders == null ? new HashSet<AbstractAnnotationAdder>() : adders;
	}

	/**
	 * Returns the IdSettings for a AnnotationProvider
	 * 
	 * @param provider
	 *            to be used
	 * @return IdSettings for annotationProvider
	 */
	public IdSettings getIdSettings4provider(AbstractAnnotationProvider provider) {
		return provider2idSettings.get(provider);
	}

	/**
	 * Returns true if there are active adder that belong to provider
	 * 
	 * @param adder
	 *            to be checked
	 * @param provider
	 *            to be contained in
	 * @return true if adder belongs to provider and is selected
	 */
	@SuppressWarnings("unchecked")
	public boolean containsAdder4Provider(AbstractAnnotationAdder adder,
			AbstractAnnotationProvider provider) {
		Set<AbstractAnnotationAdder> adders = provider2adders.get(provider);
		if (adders == null)
			return false;

		return adders.contains(adder);
	}

}
