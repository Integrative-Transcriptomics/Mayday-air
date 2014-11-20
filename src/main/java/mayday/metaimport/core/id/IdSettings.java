package mayday.metaimport.core.id;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import mayday.core.Probe;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringMIO;
import mayday.metaimport.provider.picr.meta.types.IdSetMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 * Every AnnotationProvider has some IdSettings associated.
 * Ids can be retrieved from MIOs or Probe.getName().
 * User may decided what ids to use and hot to configure an additional necessary IdModifier.
 * Some AnnotationProvider adjustment of the the provided ids.
 * 
 */
public class IdSettings {

	/**
	 * MIPathUtilities to a MIGroup where to look for ids.
	 */
	public static String IDPATH = "Ids";

	public static String MIO_TYPE = "PAS.MIO.IdSet";
	
	/**
	 * IdModifier to be used.
	 */
	private AbstractIdModifier idModifier;

	/**
	 * MIGroupModifier
	 */
	private Map<MIGROUP, AbstractMIGroupModifier> MIGROUP2modifier;
	
	/**
	 * Options to choose from.
	 */
	public static enum SOURCE {PROBE, MIO};

	/**
	 * Relations.
	 */
	public static Map<SOURCE, String> SOURCE2NAME = new HashMap<SOURCE, String>();
	static {
		SOURCE2NAME.put(SOURCE.PROBE, "Probe");
		SOURCE2NAME.put(SOURCE.MIO, "MIO");
	}

	public static enum MIGROUP {DEFAULT, CUSTOM};
	
	/**
	 * Relations.
	 */
	public static Map<MIGROUP, String> MIGROUP2NAME = new HashMap<MIGROUP, String>();
	static {
		MIGROUP2NAME.put(MIGROUP.DEFAULT, "Default");
		MIGROUP2NAME.put(MIGROUP.CUSTOM, "Custom");
	}
	
	/**
	 * Chosen id source.
	 */
	private SOURCE sourceSetting;

	/**
	 * Chosen MIGroup
	 */
	private MIGROUP miGroupSetting;

	/**
	 * Reference to MIManager - needed by MIGroupModifier
	 */
	private MIManager miManager;
	
	/**
	 * Constructor.
	 */
	public IdSettings(MIManager miManager) {
		sourceSetting = SOURCE.PROBE;
		miGroupSetting = MIGROUP.DEFAULT;
		this.miManager = miManager;
		
		MIGROUP2modifier = new HashMap<MIGROUP, AbstractMIGroupModifier>();

		AbstractMIGroupModifier defaultMIGroupModifier = new DefaultMIGroupModifier(miManager);
		defaultMIGroupModifier.initGUI();
		MIGROUP2modifier.put(MIGROUP.DEFAULT, defaultMIGroupModifier);
		
		AbstractMIGroupModifier customMIGroupModifier = new CustomMIGroupModifier(miManager);
		customMIGroupModifier.initGUI();
		MIGROUP2modifier.put(MIGROUP.CUSTOM, customMIGroupModifier);
	}
	
	/**
	 * returns an array with the possible ids for this certain probe
	 * @param probe
	 * @return
	 */
	public ArrayList<String> getIds4Probe(Probe probe) {
		ArrayList<String> ids = new ArrayList<String>();
		
		// take special care of settings for the standard Id - that is on chip
		switch(sourceSetting) {
			case PROBE:{
				ids.add(getIdFromProbe(probe));
				break;
			}
			
			case MIO:{
				ids.addAll(getIdsFromMIO(probe));
				break;
			}
		
			default:{
				throw new RuntimeException("Only PROBE or ID allowed as Id source!");
			}
		}
		return ids;
	}

	/**
	 * Returns the processed id that is chained to the Probe.
	 * @param p the Probe
	 * @return the processed id
	 */
	private String getIdFromProbe(Probe p) {
		String id = p.getName();
		return idModifier.manufactureId(id);
	}

	/**
	 * Returns a list of processed ids stored in some MIO under sourceIdMIGroup
	 * @param p the Probe
	 * @return a List of processed ids retrieved from MIO referenced by sourceIdMIGroup
	 */
	private ArrayList<String> getIdsFromMIO(Probe p) {
		// the result object
		ArrayList<String> ids = new ArrayList<String>();
		
		// get the IdType
		String idType = idModifier.getIdType();
		
		// get the group where to look for ids
		MIGroup miGroup = MIGROUP2modifier.get(miGroupSetting).getMIGroup4IdType(idType);
		
		// if there is one miGroup
		if(miGroup == null)
			return ids;
		
		// get the MIO that stores the ids
		MIType miType =	miGroup.getMIO(p);
		
		// check its type - actually not necessary since getMIGroup4IdType returns instances of StringListMIO
		if (miType instanceof IdSetMIO) {
			IdSetMIO idSetMIO = (IdSetMIO) miType;
			for(String id : idSetMIO.getValue()) {
				ids.add(idModifier.manufactureId(id));
			}
		} else 
			if (miType instanceof StringMIO) {
				StringMIO idSetMIO = (StringMIO) miType;
				ids.add(idSetMIO.getValue());
			}
		
		return ids;
	}

	/**
	 * Set a "new" idModifier - sourceIdMIGroup may change.
	 * @param idModifier to be used.
	 */
	public void setIdModifier(AbstractIdModifier idModifier) {
		this.idModifier = idModifier;
	}
	
	/**
	 * Returns the actual IdModifier.
	 * @return the actual IdModifier.
	 */	
	public AbstractIdModifier getIdModifier() {
		return idModifier;
	}

	/**
	 * 
	 * @return
	 */
	public AbstractMIGroupModifier getMIGroupModifier(MIGROUP migroupSetting) {
		return MIGROUP2modifier.get(migroupSetting);
	}
	
	/**
	 * Set the source for the id.
	 * @param sourceSetting 
	 */
	public void setSourceSetting(SOURCE sourceSetting) {
		this.sourceSetting = sourceSetting;
	}
	
	/**
	 * 
	 * @return
	 */
	public SOURCE getSourceSetting() {
		return sourceSetting;
	}
	
	/**
	 * 
	 * @param miGroupSetting
	 */
	public void setMIGroupSetting(MIGROUP miGroupSetting) {
		this.miGroupSetting = miGroupSetting;
	}
	
	/**
	 * Returns the MIGROUP setting, this is only valid if MIO is selected as the IdSource
	 * @return the MIGROUP setting
	 */
	public MIGROUP getMIGroupSetting() {
		if(sourceSetting == SOURCE.MIO)
			return miGroupSetting;
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public MIManager getMIManager() {
		return miManager;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getMIPath() {
		String idType = idModifier.getIdType();
		switch (miGroupSetting) {
			case DEFAULT:{
				return MIGROUP2modifier.get(MIGROUP.DEFAULT).getMIPath4IdType(idType);
			}
			case CUSTOM:{
				return MIGROUP2modifier.get(MIGROUP.CUSTOM).getMIPath4IdType(idType);
			}
	
		default:
			return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public MIGroup getMIGroup() {
		String idType = idModifier.getIdType();
		switch (miGroupSetting) {
			case DEFAULT:{
				return MIGROUP2modifier.get(MIGROUP.DEFAULT).getMIGroup4IdType(idType);
			}
			case CUSTOM:{
				return MIGROUP2modifier.get(MIGROUP.CUSTOM).getMIGroup4IdType(idType);
			}
		
			default:
				return null;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean isValid() {
		return getErrorLog().size() == 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public Collection<String> getErrorLog(){
		ArrayList<String> errorLog = new ArrayList<String>();
		
		// add any errors from the idModifier
		errorLog.addAll(idModifier.getErrorLog());
		
		// add any errors from the miGroupModifier
		String idType = idModifier.getIdType();
		AbstractMIGroupModifier miGroupModifier = MIGROUP2modifier.get(miGroupSetting);
		MIGroup miGroup = miGroupModifier.getMIGroup4IdType(idType);
		
		if(sourceSetting == SOURCE.MIO) {
			if(miGroup == null) // does the selected MIGroup actually exist?
				errorLog.add(miGroupModifier.getMIPath4IdType(idType) + " does not exist in the MIManger!");
			else { // if yes, are there any MIOs - we assume the correct type StringListMIO
				if(miGroup.getMIOs().size() == 0)
					errorLog.add(miGroupModifier.getMIPath4IdType(idType) + " does not have any MIOs -> NO IDS!");
			}
		}
			
		return errorLog;
	}
	
}
