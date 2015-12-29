package mayday.metaimport.provider.kegg.meta.types;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class MotifMapMIO extends GenericMIO<Map<String,MotifMIO>> {

	public final static String myType = "PAS.MIO.MotifMap";
	
	public MotifMapMIO() {
		Value = new HashMap<String, MotifMIO>();
	}
	
	public MotifMapMIO(Map<String, MotifMIO> map) {
		Value = map;
	}
	
	@Override
	public MIType clone() {
		MotifMapMIO motifMapMIO = new MotifMapMIO();
		motifMapMIO.deSerialize(MIType.SERIAL_TEXT, serialize(MIType.SERIAL_TEXT));
		return motifMapMIO;
	}

	@Override
	public void init() {/* NOthing to be done */}

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
				"Container for Motifs",
				"Container for Motifs"
				);

	}

	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		switch (serializationType) {
		case MIType.SERIAL_TEXT:
			String[] splits = serializedForm.split(",");
			// no error checking regarding correct format what so ever
			// assumption that correct serializedForm is given
			for (String s : splits) {
				// if everything is correct, this array should be of size 2
				String[] parts = s.split("=");
				
				String key = unprotect(parts[0].trim());
				String serializedMotif = unprotect(parts[1].trim());
				
				MotifMIO motifMIO = new MotifMIO();
				if(motifMIO.deSerialize(serializationType, serializedMotif))
					Value.put(key, motifMIO);
			}
			return true;
			
		default:
			return false;
		}
	}

	public AbstractMIRenderer<MotifMapMIO> getGUIElement() {
		return new MotifMapMIORenderer();
	}

	public String getType() {
		return myType;
	}

	public String serialize(int serializationType) {
		if (Value.size()==0)
			return "";
		
		StringBuilder ret = new StringBuilder();
		
		switch (serializationType) {
		case MIType.SERIAL_TEXT:
			for(Entry<String,MotifMIO> entry : Value.entrySet()) {
				String key = protect(entry.getKey());
				MotifMIO motifMIO = entry.getValue();
				String serialized = key + "=" + motifMIO.serialize(serializationType) + ",";
				ret.append(serialized);
			}
			return ret.substring(0, ret.length()-1);

		default:
			throw new RuntimeException("Unsupported SerializationType "+serializationType);
		}

	}

	private String protect(String s ) {
		return s.replace(",","&komma;").replace("=","&equals;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",").replace("&equals;","=");
	}
	
	public String toString() {
		// show only the keys to avoid: [motif1 = motif1, motif2 = motif2, ... ]
		return Value.keySet().toString();
	}
	
	@SuppressWarnings("serial")
	private class MotifMapMIORenderer extends AbstractMIRenderer<MotifMapMIO> {

		private MotifMapMIO value;
		
		private JPanel panel;
		private JList list;
		private AbstractMIRenderer<MotifMIO> motifMIORenderer;
		
		public MotifMapMIORenderer() {
			list = new JList();
		
			value = new MotifMapMIO();
			motifMIORenderer = new MotifMIO().getGUIElement();
			final Component editor = motifMIORenderer.getEditorComponent();

			JScrollPane listSP = new JScrollPane(list, 
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JScrollPane editorSP = new JScrollPane(editor,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			
			JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(listSP);
			splitPane.setRightComponent(editorSP);

			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					Object selectedValue = list.getSelectedValue();
						
					if(selectedValue == null)
						return;
			
					if (selectedValue instanceof MotifMIO) {
						MotifMIO motifMIO = (MotifMIO) selectedValue;
						motifMIORenderer.setEditorValue(motifMIO.serialize(SERIAL_TEXT));
						editor.repaint();
					}
				}
			});

			panel = new JPanel();
			panel.setLayout(new BorderLayout());
			panel.setMinimumSize(editor.getPreferredSize());
			panel.add(splitPane, BorderLayout.CENTER);
		}
		
		@Override
		public Component getEditorComponent() {
			return panel;
		}

		@Override
		public String getEditorValue() {
			return new String();
		}

		@Override
		public void setEditable(boolean editable) {}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			list.setListData(value.getValue().values().toArray());
		}
		
	}
	
}
