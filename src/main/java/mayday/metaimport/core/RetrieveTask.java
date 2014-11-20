package mayday.metaimport.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;
import mayday.metaimport.core.id.IdSettings;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 * Encapsulates the retrieval process. Iterates through all chosen provider, adders, probes,
 * ids and executes search to finally add the retrieved data.
 */
public class RetrieveTask extends AbstractTask {

	/**
	 * Reference to the AnnotationManager.
	 */
	private AnnotationManager annotationManager;

	/**
	 * Constructor
	 * @param annotationManager
	 */
	public RetrieveTask(AnnotationManager annotationManager) {
		super("Annotation Import");
		this.annotationManager = annotationManager;
	}

	protected void initialize() {/* nothing to be done here. See comment in doWork*/}

	/*
	 * iterates through all chosen provider, adders, probes,
	 * ids and executes search to finally add the retrieved data
	 */
	@SuppressWarnings("unchecked")
	public void doWork() throws Exception {
		// initialize progress

		// get objects from annotationManager to be used in this retrieval
		Collection<AbstractAnnotationProvider> providers = annotationManager
				.getProviders();
		List<ProbeList> probeLists = annotationManager.getProbeLists();

		Set<Probe> uniqueProbes = new HashSet<Probe>();
		for (ProbeList probeList : probeLists) {
			uniqueProbes.addAll(probeList.getAllProbes());
		}
		
		// total work = sum (#unique_probes) * #feature(provider f) over all provider
		// needs to be here, cannot go to initialize see AbstractTask. initialize() is called inside the constructor
		// some ProbeLists may have overlapping probes - count them only once!!
		int total = 0;
		for(AbstractAnnotationProvider provider : providers){
			total += annotationManager.getAdders4provider(provider).size();
		}
		// FIXME if denominator is bigger than 10000 
		int step = 10000 / (uniqueProbes.size() * total);
		
		/*
		 * start actual work here 
		 */

		// iterate over all data provider and add necessary stuff
		for (AbstractAnnotationProvider provider : providers) {

			// set of active Adders a provider has
			Set<AbstractAnnotationAdder> adders = annotationManager
					.getAdders4provider(provider);
			// IdSettings for the provider
			IdSettings idSettings = annotationManager
					.getIdSettings4provider(provider);

			// iterate over all adder this data provider has
			for (AbstractAnnotationAdder adder : adders) {
				
				// the MIGroup to store the retrieved annotation from a data provider
				// defined here because each adder can provide it's own MIGroup where 
				// it stores annotation data
				MIGroup group = null;

				// iterate over all probes
				for (Probe probe : uniqueProbes) {

					setProgress(getProgress() + step, provider.getName() + " -> " + adder.getName());
					
					// get the ids to search for a probe
					// each probe may have different ids that are needed for some specific provider
					ArrayList<String> ids = idSettings.getIds4Probe(probe);

					// iterate over all ids that have been selected
					for (String id : ids) {

						// only checked if a probe has some ids associated with it
						if(getTaskState() == TaskStateEvent.TASK_CANCELLING)
							return;
							
						// this is the time consuming step since it
						// connects to the provider source and executes some query
						// one could pass this worker to enable finer progress visualization
						List annotationData = adder.getAnnotation(id);
			
						// create a MIGroup if it does not exist and there 
						// is actually annotation data to be added
						if (annotationData.size() > 0 && group == null)
							group = adder.createMIGroup(annotationManager
									.getMIManager());

						// iterate over the results and add the appropriate
						// relations between probe and annotation data
						// don't care about type, since getAnnotation gives
						// the type for element addAnnotation expects
						// @see{AbstractAnnotationAdder}
						
						for (Object element : annotationData)
							adder.addAnnotation(element, group, probe);
					}
				}
			}
		}
		
		setProgress(10000);
	}
	
	public void doCancel() {
		
	}

}
