package mayday.metaimport.provider.go.meta.types;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.metaimport.provider.go.GOTerm;

/**
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 *
 */
public class GOTermMIO extends GenericMIO<GOTerm> {

	public final static String myType = "PAS.MIO.GOTerm";
	
	/**
	 * Default constructor.
	 */
	public GOTermMIO() {
		Value = new GOTerm();
	}
	
	/**
	 * 
	 * @param term
	 */
	public GOTermMIO(GOTerm term) {
		Value = term;
	}

	@Override
	public MIType clone() {
		GOTermMIO goTermMIO = new GOTermMIO();
		goTermMIO.deSerialize(SERIAL_TEXT, serialize(SERIAL_TEXT));
		return goTermMIO;
	}

	@Override
	public void init() {/* nothing to be done */}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Michael Piechotta",
				"piechott@informatik.uni-tuebingen.de",
				"Represents GOTerms as meta informations",
				"GOTerm MIO"
				);
	}

	public boolean deSerialize(int serializationType, String serializedForm) {
		// reset Value
		switch (serializationType) {
		case MIType.SERIAL_TEXT:
			// if everything is correct, this array should be of size 5
			String[] parts = serializedForm.split(":");
			if(parts.length != 5)
				return false;
			
			Value = new GOTerm(
							unprotect(parts[0].trim()),
							unprotect(parts[1].trim()),
							unprotect(parts[2].trim()),
							unprotect(parts[3].trim()),
							unprotect(parts[4].trim())
						);
			return true;
			
		default:
			return false;
		}
	}

	public AbstractMIRenderer<GOTermMIO> getGUIElement() {
		return new GOTermMIORenderer();
	}

	public String getType() {
		return myType;
	}

	public String serialize(int serializationType) {
		StringBuilder ret = new StringBuilder();
		
		switch (serializationType) {
		case MIType.SERIAL_TEXT:
			ret.append(protect(Value.getAcc()) + ":");
			ret.append(protect(Value.getName()) + ":");
			ret.append(protect(Value.getTerm_type()) + ":");
			ret.append(protect(Value.getTerm_definition()) + ":");
			ret.append(protect(Value.getCode()) + ":");
			return ret.substring(0, ret.length()-1);
		
		default:
			throw new RuntimeException("Unsupported SerializationType "+serializationType);
		}
	}

	public String toString() {
		return Value.getAcc();
	}
	
	private String protect(String s ) {
		return s.replace(",","&komma;").replace("=","&equals;").replace(":", "&collon;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",").replace("&equals;","=").replace("&collon;", ":");
	}

	@SuppressWarnings("serial")
	private class GOTermMIORenderer extends AbstractMIRenderer<GOTermMIO>{

		private GOTermMIO value;
		
		private JPanel panel;
		private JTextField acc;
		private JTextArea name;
		private JTextField term_type;
		private JTextArea term_definition;
		private JTextField code;
		
		public GOTermMIORenderer() {
			// MIO related
			value = new GOTermMIO();
			
			// swing related
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			
			// swing-GO-Term related
			acc = new JTextField();
			acc.setColumns(10);
			name = new JTextArea();
			name.setRows(2);
			name.setWrapStyleWord(true);
			name.setLineWrap(true);
			
			term_type = new JTextField();

			term_definition = new JTextArea();
			term_definition.setRows(6);
			term_definition.setWrapStyleWord(true);
			term_definition.setLineWrap(true);
			
			code = new JTextField();
			code.setColumns(3);
			// scrollPane for the term_definition
			JScrollPane term_definitionSP = new JScrollPane(term_definition);
			term_definitionSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			// scrollPane for the name
			JScrollPane nameSP = new JScrollPane(name);
			nameSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
			// layout
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.FIRST_LINE_START;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.insets = new Insets(5, 5, 5, 5);
			
			// layout components
			panel.add(new JLabel("Acc"),gbc);
			panel.add(acc, gbc);

			panel.add(new JLabel("Name"), gbc);
			panel.add(nameSP, gbc);
			
			panel.add(new JLabel("Term definition"), gbc);
			panel.add(term_definitionSP, gbc);

			panel.add(new JLabel("Term type"), gbc);
			panel.add(term_type, gbc);
			
			panel.add(new JLabel("Code"), gbc);
			panel.add(code, gbc);

			// change options
			setEditable(false);
		}
		
		@Override
		public Component getEditorComponent() {
			return panel;
		}

		@Override
		public String getEditorValue() {
			// GO-Terms should not be changed
			return value.serialize(SERIAL_TEXT);
		}

		@Override
		public void setEditable(boolean editable) {
			// GO-Terms should not be changed			
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			GOTerm goTerm = value.getValue();
			acc.setText(goTerm.getAcc());
			name.setText(goTerm.getName());
			term_type.setText(goTerm.getTerm_type());
			term_definition.setText(goTerm.getTerm_definition());
			code.setText(goTerm.getCode());
		}
		
	}
	
}
