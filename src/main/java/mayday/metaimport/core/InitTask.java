package mayday.metaimport.core;

import java.util.Set;

import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.tasks.AbstractTask;
import mayday.core.tasks.TaskStateEvent;

/**
 * 
 * separate thread for initializing AnnotationProvider since it takes some
 * time for certain AnnotationProvider to initialize properly:
 * 
 * check internet connection and so on...
 * 
 * It uses Abstract Task.
 */
public class InitTask extends AbstractTask {

	private AnnotationManager annotationManager;
	
	/**
	 * Constructor.
	 */
	public InitTask(AnnotationManager annotationManager) {
		super("Initialize Annotation Provider");
		this.annotationManager = annotationManager;
	}

	protected void initialize() {/* nothing to be done here */}

	public void doCancel() {
		// "delete" all providers initialized so far
		annotationManager.removeAllProviders();
	}
	
	protected void doWork() throws Exception {
		// get plugins that pretend to be a AnnotationProvider
		Set<PluginInfo> plis = PluginManager.getInstance()
				.getPluginsFor(Defaults.MC_METAINFORMATION_PROVIDER);

		setProgress(0, "Searching ...");
		
		int step = 10000 / plis.size();		
		
		for (PluginInfo pli : plis) {

			// check if task has being canceled
			if(getTaskState() == TaskStateEvent.TASK_CANCELLING) {
				doCancel();
				return;
			}
			
			// instantiate it
			AbstractPlugin abstractPlugin = pli.getInstance();

			// check if this is a real AnnotationProvider
			if (abstractPlugin instanceof AbstractAnnotationProvider) {
				AbstractAnnotationProvider abstractProvider = (AbstractAnnotationProvider) abstractPlugin;
	
				setProgress(getProgress(), abstractProvider.getName());
				
				// try to initialize it
				if (!abstractProvider.getStatus())
					abstractProvider.initProvider();

				
				// if it worked, add it to the working AnnotationProviders
				// and add IdSettings
				if (abstractProvider.getStatus())
					annotationManager.addProvider(abstractProvider);
				else
					// write a short error log
					writeLog(abstractProvider.getName()
							+ " initialization failed!\n");
			
					if(abstractProvider.getErrorLog().size() > 0) {
						for(String errorMessage : abstractProvider.getErrorLog()) {
						writeLog(abstractProvider.getName()
								+ " -> " + errorMessage + "!\n");
						}
					}
				
				setProgress(getProgress() + step, abstractProvider.getName());
			}
		}
		setProgress(10000);
	}
}