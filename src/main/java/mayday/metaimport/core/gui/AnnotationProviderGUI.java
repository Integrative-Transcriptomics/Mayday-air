package mayday.metaimport.core.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import mayday.core.pluma.PluginManager;
import mayday.core.pluma.filemanager.FMFile;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.AbstractAnnotationProvider;
import mayday.metaimport.core.AnnotationManager;
import mayday.metaimport.core.id.AbstractIdModifier;
import mayday.metaimport.core.id.AbstractMIGroupModifier;
import mayday.metaimport.core.id.IdSettings;
import mayday.metaimport.core.id.gui.IdModifierGUI;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $ 
 * 
 * The gui for each AnnotationProvider. To change
 *          use methods in AnnotationAdder and AbstractIdModifier that deal with
 *          views.
 */
@SuppressWarnings("serial")
public class AnnotationProviderGUI extends JPanel {

	/**
	 * Reference to the provider that is being displayed.
	 */
	protected AbstractAnnotationProvider provider;

	/**
	 * Reference to the annotationManager. Holds some needed settings.
	 */
	protected AnnotationManager annotationManager;

	/**
	 * Swing stuff.
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Swing stuff.
	 */
	private JPanel supportedIdsPanel;

	/**
	 * 
	 */
	private JButton optionsButton;

	/**
	 * 
	 */
	private JComboBox useIdComboBox;

	/**
	 * 
	 */
	private JRadioButton defaultMIGroupButton;

	/**
	 * 
	 */
	private JRadioButton customMIGroupButton;

	/**
	 * 
	 */
	private JButton miGroupSelectionButton;

	/**
	 * 
	 */
	private JTextField defaultMIOPath;

	/**
	 * 
	 */
	private int index;

	/**
	 * container for checkboxes to allow (de)select all container
	 */
	private Map<String, JCheckBox> adder2checkbox;

	/**
	 * container for optionButtons to allow (de)select all container
	 */
	private Map<String, JButton> adder2optionButton;

	/**
	 * Constructor.
	 * 
	 * @param provider
	 * @param annotationManager
	 */
	public AnnotationProviderGUI(AbstractAnnotationProvider provider,
			AnnotationManager annotationManager) {
		this.provider = provider;
		this.annotationManager = annotationManager;
		index = -1;

		// swing
		tabbedPane = new JTabbedPane();
		supportedIdsPanel = new JPanel();
		optionsButton = new JButton("Options");
		useIdComboBox = new JComboBox();
		defaultMIGroupButton = new JRadioButton("Default");
		customMIGroupButton = new JRadioButton("Custom");
		miGroupSelectionButton = new JButton("MIGroup Selection");
		defaultMIOPath = new JTextField();

		adder2checkbox = new HashMap<String, JCheckBox>();
		adder2optionButton = new HashMap<String, JButton>();
	}

	/**
	 * Initializes the description set in AnnotationProvider.
	 */
	protected void initDescription() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;

		JTextArea descriptionTextArea = new JTextArea();
		descriptionTextArea.setEditable(false);
		descriptionTextArea.setLineWrap(true);
		descriptionTextArea.setWrapStyleWord(true);
		descriptionTextArea.setRows(6);
		String description = provider.getDescription();
		if (description != null && description.length() > 0)
			descriptionTextArea.setText(description);
		else
			descriptionTextArea.setText("No description available!");

		gbc.weighty = .9;
		panel.add(new JScrollPane(descriptionTextArea,
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), gbc);

		tabbedPane.addTab("Provider", panel);
	}

	/**
	 * Initializes adders with the following format: checkbox | name of the
	 * adder | JPanel that comes form the adder
	 */
	@SuppressWarnings("unchecked")
	protected void initAdders() {
		JPanel annotationPanel = new JPanel();

		// layout
		annotationPanel.setLayout(new GridBagLayout());

		// constraints
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.weighty = 0.1;

		// select all checkboxes
		final JCheckBox changeAllCheckBox = new JCheckBox("select all/none");
		changeAllCheckBox.setName("select all/none");
		changeAllCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JCheckBox cb = (JCheckBox) e.getSource();
				boolean isSelected = cb.isSelected();

				// access to inner variables - bad style
				if (isSelected) {
					annotationManager.addAllAdders4Provider(provider);
					// bad style - direct reference to tab that holds id stuff -
					// depended on addition of tabs
					tabbedPane.setEnabledAt(index, true);
				} else {
					annotationManager.removeAllAdders4Provider(provider);
					// bad style - direct reference to tab that holds id stuff -
					// depended on addition of tabs
					tabbedPane.setEnabledAt(index, false);
				}
				for (String adderName : adder2checkbox.keySet()) {
					adder2checkbox.get(adderName).setSelected(isSelected);

					// check if this adder has some gui to show
					if (provider.getAdders().get(adderName).getGUI() != null)
						adder2optionButton.get(adderName)
								.setEnabled(isSelected);
				}
			}
		});

		// initialize the gui
		// check if all possible adders are selected
		if (annotationManager.getAdders4provider(provider) != null)
			if (annotationManager.getAdders4provider(provider).size() == provider
					.getAdders().size())
				changeAllCheckBox.setSelected(true);
			else
				changeAllCheckBox.setSelected(false);
		else
			changeAllCheckBox.setSelected(false);

		// add to Component
		annotationPanel.add(changeAllCheckBox, gbc);

		gbc.weighty = .9 / provider.getAdders().size();
		// access to inner variables - bad style
		Collection<AbstractAnnotationAdder> adders = provider.getAdders()
				.values();
		for (final AbstractAnnotationAdder adder : adders) {
			final JCheckBox checkBox = new JCheckBox(adder.getName());
			checkBox.setToolTipText(adder.getDescription());
			checkBox.addActionListener(new ActionListener() {
				// watch out ugly code - inner class accesses variables final
				// checkBox, adder
				public void actionPerformed(ActionEvent e) {
					boolean isSelected = checkBox.isSelected();
					if (isSelected) {
						annotationManager.addAdder4Provider(adder, provider);
						// bad style - direct reference to tab that holds id
						// stuff - depended on addition of tabs
						tabbedPane.setEnabledAt(index, true);
					} else {
						annotationManager.removeAdder4Provider(adder, provider);

						// we deselect the SelectAllCheckbox for obvious reasons
						// (we have just remove one adder there is no way
						// everything is selected ;-)
						changeAllCheckBox.setSelected(false);

						// if there is no adder selected disable the id tab
						if (annotationManager.getAdders4provider(provider)
								.size() == 0)
							tabbedPane.setEnabledAt(index, false);
					}
					if (adder.getGUI() != null)
						adder2optionButton.get(adder.getName()).setEnabled(
								isSelected);
				}
			});
			// initialize the gui
			checkBox.setSelected(annotationManager.containsAdder4Provider(
					adder, provider));

			// configure any extra options this adder offers
			JButton optionsButton = new JButton("Options");
			// initialize the gui
			optionsButton.setEnabled(annotationManager.containsAdder4Provider(
					adder, provider));

			// if the feature has some extra gui, add it
			adder.initGUI();
			if (adder.getGUI() != null) {
				optionsButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						AnnotationAdderOptionsGUI aaov = new AnnotationAdderOptionsGUI(
								adder);
						aaov.init();
						aaov.setVisible(true);
					}
				});
			} else
				optionsButton.setEnabled(false);

			// add components
			gbc.weightx = .5;
			gbc.gridwidth = GridBagConstraints.RELATIVE;
			annotationPanel.add(checkBox, gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			annotationPanel.add(optionsButton, gbc);

			// reference checkboxes and optionButtons
			adder2checkbox.put(adder.getName(), checkBox);
			adder2optionButton.put(adder.getName(), optionsButton);
		}

		tabbedPane.addTab("Adder", new JScrollPane(annotationPanel));
	}

	/**
	 * Build the selection for supported ids.
	 */
	protected void initSupportedIds() {
		supportedIdsPanel.setBorder(new TitledBorder("Supported Ids"));
		final IdSettings idSettings = annotationManager
				.getIdSettings4provider(provider);

		// layout
		supportedIdsPanel.setLayout(new GridBagLayout());

		// constraints
		GridBagConstraints g_gbc = new GridBagConstraints();
		g_gbc.insets = new Insets(5, 5, 5, 5);
		g_gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		g_gbc.gridwidth = GridBagConstraints.REMAINDER;
		g_gbc.fill = GridBagConstraints.BOTH;
		g_gbc.weightx = 1.0;
		g_gbc.weighty = .33;

		GridBagConstraints panel_gbc = new GridBagConstraints();
		panel_gbc.insets = new Insets(5, 5, 5, 5);
		panel_gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		panel_gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel_gbc.fill = GridBagConstraints.NONE;
		panel_gbc.weightx = .5;
		panel_gbc.weighty = 1.0;

		JPanel sourcePanel = new JPanel();
		sourcePanel.setBorder(new TitledBorder("Id source"));
		sourcePanel.setLayout(new GridBagLayout());

		ButtonGroup sourceButtonGroup = new ButtonGroup();
		IdSettings.SOURCE selectedSource = idSettings.getSourceSetting();

		// button to chose id as the source
		final JRadioButton idSourceButton = new JRadioButton(
				IdSettings.SOURCE2NAME.get(IdSettings.SOURCE.PROBE),
				selectedSource == IdSettings.SOURCE.PROBE);

		idSourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (idSourceButton.isSelected()) {
					idSettings.setSourceSetting(IdSettings.SOURCE.PROBE);
					defaultMIGroupButton.setEnabled(false);
					customMIGroupButton.setEnabled(false);
					miGroupSelectionButton.setEnabled(false);
					defaultMIOPath.setEnabled(false);
				}
			}
		});

		// button to chose MIO as the source
		final JRadioButton mioSourceButton = new JRadioButton(
				IdSettings.SOURCE2NAME.get(IdSettings.SOURCE.MIO),
				selectedSource == IdSettings.SOURCE.MIO);
		mioSourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (mioSourceButton.isSelected()) {
					idSettings.setSourceSetting(IdSettings.SOURCE.MIO);

					defaultMIGroupButton.setEnabled(true);
					customMIGroupButton.setEnabled(true);

					if (idSettings.getMIGroupSetting() == IdSettings.MIGROUP.DEFAULT) {
						miGroupSelectionButton.setEnabled(false);
						defaultMIGroupButton.setSelected(true);
						defaultMIOPath.setText(idSettings.getMIPath());
						defaultMIOPath.setEnabled(true);
					} else if (idSettings.getMIGroupSetting() == IdSettings.MIGROUP.CUSTOM) {
						miGroupSelectionButton.setEnabled(true);
						customMIGroupButton.setEnabled(true);
						defaultMIOPath.setEnabled(false);
					}
				}
			}
		});

		// add buttons to group
		sourceButtonGroup.add(idSourceButton);
		sourceButtonGroup.add(mioSourceButton);

		// add source buttons
		panel_gbc.gridwidth = GridBagConstraints.RELATIVE;
		sourcePanel.add(idSourceButton, panel_gbc);
		panel_gbc.gridwidth = GridBagConstraints.REMAINDER;
		sourcePanel.add(mioSourceButton, panel_gbc);

		supportedIdsPanel.add(sourcePanel, g_gbc);

		// IdType
		JPanel typePanel = new JPanel();
		typePanel.setBorder(new TitledBorder("Id type"));
		typePanel.setLayout(new GridBagLayout());
		// build the options to choose from
		// container - a list to be able to sort them
		List<AbstractIdModifier> idOptions = new ArrayList<AbstractIdModifier>(
				provider.getSupportedIds().values());

		// sort the entries
		Collections.sort(idOptions, new Comparator<AbstractIdModifier>() {
			public int compare(AbstractIdModifier o1, AbstractIdModifier o2) {
				return o1.getIdType().compareTo(o2.getIdType());
			}
		});

		// select supported IdType to be used
		useIdComboBox.setModel(new DefaultComboBoxModel(idOptions.toArray()));
		useIdComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// update the selected IdModifier
				AbstractIdModifier idModifier = (AbstractIdModifier) useIdComboBox
						.getSelectedItem();
				idSettings.setIdModifier(idModifier);

				if (idSettings.getSourceSetting() == IdSettings.SOURCE.MIO
						&& idSettings.getMIGroupSetting() == IdSettings.MIGROUP.DEFAULT)
					defaultMIOPath.setText(idSettings.getMIPath());

				// enable/disable button when Modifier has any additional
				// gui
				if (idModifier.getComponent() == null)
					optionsButton.setEnabled(false);
				else
					optionsButton.setEnabled(true);
			}
		});
		idSettings.setIdModifier((AbstractIdModifier) useIdComboBox
				.getSelectedItem());

		// we have only one button and change its behavior when IdType is
		// changed
		optionsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractIdModifier idModifier = (AbstractIdModifier) useIdComboBox
						.getSelectedItem();
				IdModifierGUI idModifierView = new IdModifierGUI(idModifier);
				idModifierView.init();
				idModifierView.setVisible(true);
			}
		});
		AbstractIdModifier idModifier = idSettings.getIdModifier();
		idModifier.initComponent();
		optionsButton.setEnabled(idModifier.getComponent() != null);

		// add to gui
		// the selection menu for the id
		panel_gbc.gridwidth = GridBagConstraints.RELATIVE;
		typePanel.add(useIdComboBox, panel_gbc);
		// options
		panel_gbc.gridwidth = GridBagConstraints.REMAINDER;
		typePanel.add(optionsButton, panel_gbc);
		supportedIdsPanel.add(typePanel, g_gbc);

		// MIGroup source
		JPanel miGroupPanel = new JPanel();
		miGroupPanel.setBorder(new TitledBorder("MIGroup"));
		miGroupPanel.setLayout(new GridBagLayout());
		supportedIdsPanel.add(miGroupPanel, g_gbc);

		ButtonGroup miGroupButtonGroup = new ButtonGroup();

		// Button to chose Default MIGroup
		defaultMIGroupButton = new JRadioButton(IdSettings.MIGROUP2NAME
				.get(IdSettings.MIGROUP.DEFAULT));
		defaultMIGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (defaultMIGroupButton.isSelected()) {
					idSettings.setMIGroupSetting(IdSettings.MIGROUP.DEFAULT);
					miGroupSelectionButton.setEnabled(false);
					defaultMIOPath.setEnabled(true);
					defaultMIOPath.setText(idSettings.getMIPath());
				}
			}
		});
		// init button - only if MIO is selected as source it makes sense to
		// alter MIGroups.
		defaultMIGroupButton
				.setEnabled(idSettings.getSourceSetting() == IdSettings.SOURCE.MIO);

		customMIGroupButton = new JRadioButton(IdSettings.MIGROUP2NAME
				.get(IdSettings.MIGROUP.CUSTOM));
		customMIGroupButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (customMIGroupButton.isSelected()) {
					idSettings.setMIGroupSetting(IdSettings.MIGROUP.CUSTOM);
					miGroupSelectionButton.setEnabled(true);
					defaultMIOPath.setEnabled(false);
				}
			}
		});
		// init button - only if MIO is selected as source it makes sense to
		// alter MIGroups.
		customMIGroupButton
				.setEnabled(idSettings.getSourceSetting() == IdSettings.SOURCE.MIO);

		// add buttons to group
		miGroupButtonGroup.add(defaultMIGroupButton);
		miGroupButtonGroup.add(customMIGroupButton);

		// Default MIGroup selection
		panel_gbc.fill = GridBagConstraints.NONE;
		panel_gbc.weighty = .5;
		panel_gbc.gridwidth = GridBagConstraints.RELATIVE;
		miGroupPanel.add(defaultMIGroupButton, panel_gbc);
		defaultMIOPath = new JTextField(idSettings.getMIPath());
		defaultMIOPath.setEditable(false);
		defaultMIOPath.setColumns(15);
		defaultMIOPath
				.setEnabled(idSettings.getMIGroupSetting() == IdSettings.MIGROUP.DEFAULT);

		panel_gbc.gridwidth = GridBagConstraints.REMAINDER;
		panel_gbc.fill = GridBagConstraints.HORIZONTAL;
		miGroupPanel.add(defaultMIOPath, panel_gbc);
		panel_gbc.fill = GridBagConstraints.NONE;

		// Custom MIGroup selection
		miGroupSelectionButton
				.setEnabled(idSettings.getMIGroupSetting() == IdSettings.MIGROUP.CUSTOM);

		miGroupSelectionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				AbstractMIGroupModifier miGroupModifier = idSettings
						.getMIGroupModifier(IdSettings.MIGROUP.CUSTOM);
				miGroupModifier.initGUI();
				miGroupModifier.getGUI().setVisible(true);
			}
		});

		// add components
		panel_gbc.gridwidth = GridBagConstraints.RELATIVE;
		miGroupPanel.add(customMIGroupButton, panel_gbc);
		panel_gbc.gridwidth = GridBagConstraints.REMAINDER;
		miGroupPanel.add(miGroupSelectionButton, panel_gbc);

		tabbedPane.addTab("Id", supportedIdsPanel);
		index = tabbedPane.getTabCount() - 1;
	}

	/**
	 * Calls other methods that initializes the components of the gui.
	 */
	public void init() {
		setLayout(new GridBagLayout());

		// order important
		initTabs();
		initLogo();
	}

	/**
	 * Initialize the tabs consisting of one tab for the adder and an other for
	 * the id management.
	 */
	protected void initTabs() {
		// order important
		initDescription();
		initAdders();
		initSupportedIds();

		// init tabs - hide Id until at least one adder is selected
		tabbedPane.setEnabledAt(index, annotationManager.getAdders4provider(
				provider).size() > 0);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weighty = .5;
		gbc.weightx = 1.0;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		add(tabbedPane, gbc);
	}

	/**
	 * Initialize the logo if there is one.
	 */
	protected void initLogo() {
		String path = provider.getLogoPath();
		if (path == null || path.length() == 0)
			return;

		FMFile logo = PluginManager.getInstance().getFilemanager()
				.getFile(path);
		BufferedImage bfr = null;
		InputStream splash_is = null;

		if (logo != null) {
			splash_is = logo.getStream();
		}

		if (splash_is != null) {
			try {
				bfr = ImageIO.read(splash_is);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (bfr != null) {
			ImageIcon ico = new ImageIcon(bfr);
			JLabel logoLabel = new JLabel();
			logoLabel.setIcon(ico);
			// TODO resize all logos to the same dimensions

			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.anchor = GridBagConstraints.EAST;
			gbc.insets = new Insets(5, 5, 5, 5);
			gbc.weightx = 1.0;
			add(logoLabel, gbc);
		}
	}

	/**
	 * Returns the provider that is being viewed.
	 * 
	 * @return
	 */
	public AbstractAnnotationProvider getProvider() {
		return provider;
	}

}
