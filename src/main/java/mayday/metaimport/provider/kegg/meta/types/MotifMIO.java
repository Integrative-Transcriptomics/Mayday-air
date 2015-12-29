package mayday.metaimport.provider.kegg.meta.types;

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
import mayday.metaimport.provider.kegg.Motif;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 * 
 */
public class MotifMIO extends GenericMIO<Motif> {
	
	public final static String myType = "PAS.MIO.Motif";
	
	/**
	 * Default constructor.
	 */
	public MotifMIO() {
		Value = new Motif();
	}
	
	/**
	 * 
	 * @param motif
	 */
	public MotifMIO(Motif motif) {
		Value = motif;
	}

	@Override
	public MIType clone() {
		MotifMIO mio = new MotifMIO();
		mio.deSerialize(SERIAL_TEXT, serialize(SERIAL_TEXT));
		return mio;
	}

	@Override
	public void init() {/* nothing to be done */}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				getClass(), 
				myType, 
				null, 
				Constants.MC_METAINFO,
				new HashMap<String, Object>(), 
				"Michael Piechotta",
				"piechott@student.informatik.tuebingen.de", 
				"Represents KEGG motifs as meta information",
				"KEGG Motif MIO"
				);
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		switch (serializationType) {
		case MIType.SERIAL_TEXT:
			// if everything is correct, this array should be of size 6
			String[] parts = serializedForm.split(":");
			
			if(parts.length != 6)
				return false;
			
			Value = new Motif(
							unprotect(parts[0].trim()),
							unprotect(parts[1].trim()),
							Integer.parseInt(unprotect(parts[2]).trim()),
							Integer.parseInt(unprotect(parts[3]).trim()),
							Double.parseDouble(unprotect(parts[4]).trim()),
							Double.parseDouble(unprotect(parts[5]).trim())
						);
			return true;
			
		default:
			return false;
		}
	}

	public AbstractMIRenderer<MotifMIO> getGUIElement() {
		return new MotifMIORenderer();
	}

	public String getType() {
		return myType;
	}

	public String toString() {
		return Value.getMotif_id();
	}
	
	public String serialize(int serializationType) {
		StringBuilder ret = new StringBuilder();
		
		switch (serializationType) {
		case MIType.SERIAL_TEXT:
			ret.append(protect(Value.getMotif_id()) + ":");
			ret.append(protect(Value.getDefinition()) + ":");
			ret.append(protect(Integer.toString((Value.getStart_position()))) + ":");
			ret.append(protect(Integer.toString(Value.getEnd_position())) + ":");
			ret.append(protect(Double.toString(Value.getScore())) + ":");
			ret.append(protect(Double.toString(Value.getEvalue())) + ":");
			return ret.substring(0, ret.length()-1);
		
		default:
			throw new RuntimeException("Unsupported SerializationType "+serializationType);
		}
	}
	
	private String protect(String s ) {
		return s.replace(",","&komma;").replace("=","&equals;").replace(":", "&collon;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",").replace("&equals;","=").replace("&collon;", ":");
	}

	@SuppressWarnings("serial")
	private class MotifMIORenderer extends AbstractMIRenderer<MotifMIO>{

		private MotifMIO value;
		
		private JPanel panel;
		private JTextField motif_id;
		private JTextArea definition;
		private JTextField start_position;
		private JTextField end_position;
		private JTextField score;
		private JTextField evalue;
		
		public MotifMIORenderer() {
			// MIO related
			value = new MotifMIO();
		
			// swing related
			panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			
			// swing-GO-Term related
			motif_id = new JTextField();
			definition = new JTextArea();
			definition.setRows(3);
			definition.setWrapStyleWord(true);
			definition.setLineWrap(true);
			
			start_position = new JTextField();
			end_position = new JTextField();
			
			score = new JTextField();
			evalue = new JTextField();
			
			// scrollPane for the definition
			JScrollPane definitionSP = new JScrollPane(definition);
			definitionSP.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			// layout
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.anchor = GridBagConstraints.LAST_LINE_START;
			gbc.weightx = 1.0;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			gbc.insets = new Insets(5, 5, 5, 5);
			
			// layout components
			panel.add(new JLabel("Motif id"),gbc);
			panel.add(motif_id, gbc);
			
			panel.add(new JLabel("Definition"), gbc);
			panel.add(definitionSP, gbc);

			gbc.gridwidth = GridBagConstraints.RELATIVE;
			panel.add(new JLabel("Start position"), gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			panel.add(new JLabel("End position"), gbc);
			
			gbc.gridwidth = GridBagConstraints.RELATIVE;
			panel.add(start_position, gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			panel.add(end_position, gbc);

			gbc.gridwidth = GridBagConstraints.RELATIVE;
			panel.add(new JLabel("Score"), gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			panel.add(new JLabel("E-Value"), gbc);

			gbc.gridwidth = GridBagConstraints.RELATIVE;
			panel.add(score, gbc);
			gbc.gridwidth = GridBagConstraints.REMAINDER;
			panel.add(evalue, gbc);
			
			// change options
			setEditable(false);
		}
		
		@Override
		public Component getEditorComponent() {
			return panel;
		}

		@Override
		public String getEditorValue() {
			// Motifs should not be changed
			return value.serialize(SERIAL_TEXT);
		}

		@Override
		public void setEditable(boolean editable) {
			// Motifs should not be changed
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			Motif motif = value.getValue();
			motif_id.setText(motif.getMotif_id());
			definition.setText(motif.getDefinition());
			start_position.setText(Integer.toString(motif.getStart_position()));
			end_position.setText(Integer.toString(motif.getEnd_position()));
			score.setText(Double.toString(motif.getScore()));
			evalue.setText(Double.toString(motif.getEvalue()));
		}

	}
}
