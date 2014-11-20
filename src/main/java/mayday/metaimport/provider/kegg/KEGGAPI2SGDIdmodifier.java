package mayday.metaimport.provider.kegg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.metaimport.core.id.AbstractIdModifier;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 *
 */
public class KEGGAPI2SGDIdmodifier extends AbstractIdModifier {

	/**
	 * 
	 */
	private String prefix;
	
	/**
	 * 
	 */
	private Map<String, String> organism2prefix;
	
	/**
	 * 
	 * @param idType
	 * @param organism2prefix
	 */
	public KEGGAPI2SGDIdmodifier(String idType, Map<String, String> organism2prefix) {
		super(idType);
		prefix = new String();
		this.organism2prefix = organism2prefix;
	}
	
	@Override
	public void initComponent() {
		component = new JPanel();
		component.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = GridBagConstraints.REMAINDER;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		
		component.add(new JLabel("Organims:"), gbc);
		
		List<String> names = new ArrayList<String>(organism2prefix.keySet());
		Collections.sort(names);
		names.add(0, "none");
		JComboBox comboBox = new JComboBox(names.toArray());
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox selectedChoice = (JComboBox) e.getSource(); 
		        String name = (String)selectedChoice.getSelectedItem();
		        // bad style - none is artifically added in order to force the user to make a selection
		        if(name == "none")
		        	return;
		        
		        if(!organism2prefix.containsKey(name)){
		        	throw new RuntimeException();
		        }
		        
		        prefix = organism2prefix.get(name);
			}
		});
		component.add(comboBox, gbc);
	}

	/**
	 * for some reason KEGG needs the organisms prefix
	 */
	@Override
	protected String manufactureId(String id) {
		return prefix + id;
	}

	@Override
	public Collection<String> getErrorLog(){
		Collection<String> errorLog = new ArrayList<String>();
		
		if(prefix.length() == 0)
			errorLog.add("KEGG needs a organism-prefix for its id!");
		
		return errorLog;
	}
	
}
