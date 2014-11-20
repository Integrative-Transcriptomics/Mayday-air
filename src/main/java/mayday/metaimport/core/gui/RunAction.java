package mayday.metaimport.core.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.AbstractAnnotationProvider;
import mayday.metaimport.core.AnnotationManager;
import mayday.metaimport.core.id.IdSettings;

/**
 * The action take place when clicking Ok in the AnnotationManagerGUI
 * 
 * Starts a new thread and configures the RetrieveTask
 */
@SuppressWarnings("serial")
public class RunAction extends AbstractAction {

	/**
	 * Reference to AnnotationManagerGUI.
	 */
	private AnnotationManagerGUI annotationManagerView;
	
	/**
	 * Constructor.
	 * @param annotationManagerView
	 */
	public RunAction(AnnotationManagerGUI annotationManagerView) {
		super("RunAction");
		this.annotationManagerView = annotationManagerView;
	}

	/**
	 * Performs error checking and start retrieval.
	 */
	@SuppressWarnings("unchecked")
	public void actionPerformed(ActionEvent event) {
		Collection<String> errorLog = new ArrayList<String>();

		// if checksum = 0 no Adder was selected.
		int checkSum = 0;

		final AnnotationManager annotationManager = annotationManagerView.getAnnotationManager();
		Collection<AbstractAnnotationProvider> providers = annotationManager.getProviders();
		
		// Put all that stuff to an AbstractTast class to show progress like GO or something...			
		for(AbstractAnnotationProvider provider : providers) {
			// check for errors and prevents from starting with a malicious configuration 
			Set<AbstractAnnotationAdder> adders = annotationManager.getAdders4provider(provider);
			if(adders.size() == 0)
				continue;

			// add idSettings errors
			IdSettings idSettings = annotationManager.getIdSettings4provider(provider);
			errorLog.addAll(idSettings.getErrorLog());
			
			for(AbstractAnnotationAdder adder : adders) {
				errorLog.addAll(adder.getErrorLog());
			}
			checkSum += adders.size();
		}
		
		// checksum == null, than there are no Adders selected
		if(checkSum == 0) {
			errorLog.add("No Adders selected!");
		}

		// did we have any errors?
		if (errorLog.size() > 0) {
			String str = new String();
			for (String errorMessage : errorLog) {
				str += errorMessage + "\n";
			}
			JOptionPane.showMessageDialog((JFrame)null, str,
					"Annotation Provider Manager Error", JOptionPane.ERROR_MESSAGE);
		} else {
			// start the RetrieveTask
			annotationManagerView.setVisible(false);
		   	
			new Thread("Retrieve Task") {
				public void run() {
					annotationManager.startRetrieveTask();
					annotationManager.resetProvider();
					annotationManagerView.removeInstance();
				}
			}.start();
		}
	}
}
