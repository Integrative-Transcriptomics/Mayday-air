package mayday.metaimport.provider.picr.meta.types;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mayday.core.meta.GenericMIO;
import mayday.core.meta.MIType;
import mayday.core.meta.gui.AbstractMIRenderer;
import mayday.core.meta.gui.AbstractMITableRenderer;
import mayday.core.pluma.Constants;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

/**
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:58 $
 *
 */
public class IdSetMIO extends GenericMIO<Set<String>> {

	public final static String myType = "PAS.MIO.IdSet";

	/**
	 * 
	 */
	public IdSetMIO() {
		Value = new HashSet<String>();
	}

	/**
	 * 
	 * @param idSet
	 */
	public IdSetMIO(Set<String> idSet) {
		Value = new HashSet<String>();
	}
	
	@Override
	public MIType clone() {
		IdSetMIO mio = new IdSetMIO();
		mio.deSerialize(MIType.SERIAL_TEXT, this.serialize(SERIAL_TEXT));
		return mio;
	}

	@Override
	public void init() {/* nothing to be done here */}

	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(),
				myType,
				new String[0],
				Constants.MC_METAINFO,
				new HashMap<String, Object>(),
				"Michael Piechotta",
				"piechott@uni-tuebingen.de",
				"Represents a set of ids as meta informations",
				"Id Set MIO"
				);
	}

	private String protect(String s ) {
		return s.replace(",","&komma;");
	}
	
	private String unprotect(String s) {
		return s.replace("&komma;",",");
	}
	
	public boolean deSerialize(int serializationType, String serializedForm) {
		Value.clear();
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			String[] splits = serializedForm.split(",");
			for (String s : splits)
				Value.add(unprotect(s.trim()));
			return true;
		case MIType.SERIAL_XML:
			// TODO
			return false;
		}
		return true;
	}

	public AbstractMIRenderer<IdSetMIO> getGUIElement() {
		return new IdSetMIORenderer();
	}

	public String getType() {
		return myType;
	}

	public String serialize(int serializationType) {
		StringBuilder ret = new StringBuilder();
		if (Value.size()==0) 
			return "";
		switch(serializationType) {
		case MIType.SERIAL_TEXT:
			for (String i: Value)
				ret.append(protect(i)+",");
			return ret.substring(0, ret.length()-1);
		case MIType.SERIAL_XML:
			return new String();
		}
		throw new RuntimeException("Unsupported SerializationType "+serializationType);
	}

	@SuppressWarnings("serial")
	private class IdSetMIORenderer extends AbstractMITableRenderer<IdSetMIO> {

		private IdSetMIO value; 
		
		public IdSetMIORenderer() {
			tableModel.setColumnCount(1);
			value = new IdSetMIO();
		}

		@Override
		public String getEditorValue() {
			Set<String> theSet = new HashSet<String>();
			for (int i=0; i!=tableModel.getRowCount(); ++i)
				theSet.add((String)tableModel.getValueAt(i, 0));
			value.setValue(theSet);
			return value.serialize(MIType.SERIAL_TEXT);
		}

		@Override
		public void setEditorValue(String serializedValue) {
			value.deSerialize(MIType.SERIAL_TEXT, serializedValue);
			tableModel.setRowCount(value.getValue().size());
			int position=0;
			for (String s : value.getValue()) {
				tableModel.setValueAt(s, position++, 0);
			}
		}
	}
}
