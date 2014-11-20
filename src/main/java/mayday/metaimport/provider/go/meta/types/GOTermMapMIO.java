package mayday.metaimport.provider.go.meta.types;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
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

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 *
 */
public class GOTermMapMIO extends GenericMIO<Map<String,GOTermMIO>> {

	public final static String myType = "PAS.MIO.GOTermMap";

	public GOTermMapMIO() {
		Value = new TreeMap<String, GOTermMIO>();
	}
	
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
				"Container for GOTermMIOs",
				"Container for GOTermMIOs"
				);
	}

	@Override
	public MIType clone() {
		GOTermMapMIO goTermMapMIO = new GOTermMapMIO();
		goTermMapMIO.deSerialize(SERIAL_TEXT, serialize(SERIAL_TEXT));
		return goTermMapMIO;
	}

	@Override
	public void init() {/* not needed */}

	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			String[] splits = serializedForm.split(",");
			// no error checking regarding correct format what so ever
			// assumption that correct serializedForm is given
			for (String s : splits) {
				// if everything is correct, this array should be of size 2
				String[] parts = s.split("=");
				
				// otherwise we take the next string
				if(parts.length != 2)
					continue;
				
				String key = unprotect(parts[0].trim());
				String serializedGOTerm = unprotect(parts[1].trim());
				
				GOTermMIO goTermMIO = new GOTermMIO();
				if(goTermMIO.deSerialize(serializationType, serializedGOTerm))
					Value.put(key, goTermMIO);
			}
			return true;
	
		default:
			return false;
		}
	}
	
	public AbstractMIRenderer<GOTermMapMIO> getGUIElement() {
		return new GOTermMapMIORenderer();
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
			for(Entry<String,GOTermMIO> entry : Value.entrySet()) {
				String key = protect(entry.getKey());
				GOTermMIO goTermMIO = entry.getValue();
				String serialized = key + "=" + goTermMIO.serialize(serializationType) + ",";
				ret.append(serialized);
			}
			return ret.substring(0, ret.length()-1);

		default:
			throw new RuntimeException("Unsupported SerializationType "+serializationType);
		}
	}

	public String toString() {
		return Value.keySet().toString();
	}
	
	private String protect(String s ) {
		return s.replace(",","&komma;").replace("=","&equals;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",").replace("&equals;","=");
	}

	@SuppressWarnings("serial")
	private class GOTermMapMIORenderer extends AbstractMIRenderer<GOTermMapMIO> {

		private GOTermMapMIO value;
		
		private JSplitPane splitPane;
		private JList list;
		private AbstractMIRenderer<GOTermMIO> goTermMIORenderer;
		
		public GOTermMapMIORenderer() {
			list = new JList();
		
			value = new GOTermMapMIO();
			goTermMIORenderer = new GOTermMIO().getGUIElement();
			final Component editor = goTermMIORenderer.getEditorComponent();

			JScrollPane listSP = new JScrollPane(list, 
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			JScrollPane editorSP = new JScrollPane(editor,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			
			splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
			splitPane.setLeftComponent(listSP);
			splitPane.setRightComponent(editorSP);
			splitPane.setMinimumSize(editor.getPreferredSize());

			list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			list.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent e) {
					Object selectedValue = list.getSelectedValue();
						
					if(selectedValue == null)
						return;
			
					if (selectedValue instanceof GOTermMIO) {
						GOTermMIO goTermMIO = (GOTermMIO) selectedValue;
						goTermMIORenderer.setEditorValue(goTermMIO.serialize(SERIAL_TEXT));
						editor.repaint();
					}
				}
			});
		}
		
		@Override
		public Component getEditorComponent() {
			return splitPane;
		}

		@Override
		public String getEditorValue() {
			return new String();
		}

		@Override
		public void setEditable(boolean editable) {
			JOptionPane.showMessageDialog((JFrame)null, "GO Terms cannot me modified!",
					"Annotation Provider Manager Editor Error", JOptionPane.ERROR_MESSAGE);
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			list.setListData(value.getValue().values().toArray());
		}
		
	}

}
