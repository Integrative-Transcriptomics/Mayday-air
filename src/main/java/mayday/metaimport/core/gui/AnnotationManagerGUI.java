package mayday.metaimport.core.gui;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Collection;

import javax.swing.JButton;
import mayday.core.gui.MaydayDialog;
import javax.swing.JTabbedPane;

import mayday.core.Mayday;
import mayday.metaimport.core.AbstractAnnotationProvider;
import mayday.metaimport.core.AnnotationManager;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $ The gui of the AnnotationManager. Includes the
 *          vies for each successfully initialized AnnotationProviderViews.
 *          Starts RetrieveTask.
 */
@SuppressWarnings("serial")
public class AnnotationManagerGUI extends MaydayDialog {

	/**
	 * Singleton.
	 */
	private static AnnotationManagerGUI unique;

	/**
	 * Reference to the annotationManager.
	 */
	private AnnotationManager annotationManager;

	/**
	 * Constructor.
	 * 
	 * @param annotationManager
	 */
	private AnnotationManagerGUI(AnnotationManager annotationManager) {
		super(Mayday.sharedInstance);

		this.annotationManager = annotationManager;
	}

	/**
	 * Singleton.
	 * 
	 * @param annotationManager
	 * @return returns a unique AnnotationManager
	 */
	public static AnnotationManagerGUI getInstance(
			AnnotationManager annotationManager) {
		if (unique == null) {
			unique = new AnnotationManagerGUI(annotationManager);
			unique.init();
		}

		return unique;
	}

	public void removeInstance() {
		unique = null;
	}

	/**
	 * Initializes gui.
	 */
	private void init() {
		setTitle("Annotation Provider Manager");
		setLayout(new GridBagLayout());

		// initialize tabs
		initTabs();

		// initialize okay, reconnect and cancel buttons
		initButtons();

		// set the size of the dialog
		setSize(400, 500);

		// set the location - taken from some other part of Mayday
		initLocation();
	}

	/**
	 * Initializes tabs. Each AnnotationProviderGUI is assigned one tab that is
	 * referenced by its name.
	 */
	private void initTabs() {
		JTabbedPane tabbedPane = new JTabbedPane();

		/*
		 * Each provider is supposed to use the same gui. Visual modifications
		 * can be incorporated through methods in AnnotationAdder and
		 * AbstractIdModifier that initialize the views.
		 */
		Collection<AbstractAnnotationProvider> providers = annotationManager
				.getProviders();
		for (AbstractAnnotationProvider provider : providers) {
			AnnotationProviderGUI providerView = new AnnotationProviderGUI(
					provider, annotationManager);
			providerView.init();

			// add tab
			tabbedPane.addTab(provider.getName(), providerView);

			// add a tool tip
			int index = tabbedPane.getTabCount() - 1;
			tabbedPane.setToolTipTextAt(index, provider.getDescription());
		}

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = .9;
		add(tabbedPane, gbc);
	}

	/**
	 * Initialize Ok, Cancel and Restart button
	 */
	private void initButtons() {
		JButton runButton = new JButton("Ok");
		runButton.setMnemonic(KeyEvent.VK_ENTER);

		// make the OK button the default button (invoked when the user hits
		// the return key of the dialog
		getRootPane().setDefaultButton(runButton);

		// ok button ActionListener
		runButton.addActionListener(new RunAction(this));

		// Cancel button ActionListener
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_ESCAPE);
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				unique = null;
			}
		});

		// Restart button ActionListener
		JButton reconnectButton = new JButton("Reconnect");
		reconnectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				annotationManager.removeAllProviders();
				setVisible(false);
				unique = null;
			}
		});

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.weightx = .33;
		gbc.weighty = .1;

		gbc.anchor = GridBagConstraints.WEST;
		add(reconnectButton, gbc);

		gbc.anchor = GridBagConstraints.EAST;
		add(cancelButton, gbc);

		gbc.anchor = GridBagConstraints.EAST;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.NONE;
		add(runButton, gbc);
	}

	/**
	 * Initializes the location to the middle of the screen.
	 * 
	 * taken from PluginManagerDialog
	 */
	private void initLocation() {
		// get the screen resolution
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();

		// compute the upper left corner of the dialog to center it on
		// the screen
		int x = (int) ((screen.width - getWidth()) * 0.5);
		int y = (int) ((screen.height - getHeight()) * 0.5);
		setLocation(x, y);
	}

	/**
	 * Returns the actual reference of the model - AnnotationManager
	 * 
	 * @return actual reference to AnnotationManager
	 */
	public AnnotationManager getAnnotationManager() {
		return annotationManager;
	}

}
