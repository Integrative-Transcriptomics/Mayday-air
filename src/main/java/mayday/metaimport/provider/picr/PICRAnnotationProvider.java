package mayday.metaimport.provider.picr;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import picrapi.AccessionMapperInterface;
import picrapi.AccessionMapperService;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.AbstractAnnotationProvider;
import mayday.metaimport.core.Defaults;
import mayday.metaimport.core.id.DummyIdModifier;
import mayday.metaimport.provider.picr.meta.types.IdSetMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 *
 */
public class PICRAnnotationProvider extends AbstractAnnotationProvider {
	
	public final static String myType = "PAS.MIO.IMPORT.PICR";
	
	/**
	 * 
	 */
	AccessionMapperService service;
    
	/**
	 * 
	 */
	AccessionMapperInterface port;
	
    /**
     * Default constructor
     */
	public PICRAnnotationProvider() {
		super("PICR (BETA)",
				"Protein Identifier Cross-Reference Service");

		logoPath = Defaults.PLUGIN_DIR + "picr/image/ims-logo-small.jpg";
	}
	
	/*
	 * 
	 */
	protected boolean initConnection() {
		try {
			//System.out.println("Initializing Service...");
			service = new AccessionMapperService();
			//System.out.println("Initializing Port...");
			port = service.getAccessionMapperPort();
			//System.out.println("Everything done so far...");
		}
		catch (Exception e) {
			description = "Connection to PICR-Service failure - check your connection!";
			System.out.println("PICR Exception:");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * IdAdder is paramertized with a database name.
	 * Each Instance with database D is responible for retrieving annotation from database D
	 */
	protected void initAdders() {
		// add the adders
		try {
			List<String> databases = port.getMappedDatabaseNames();
			for(String database : databases) {
				AbstractAnnotationAdder<String> annotation = new IdAdder(database, "", port, this); 
				adders.put(annotation.getName(), annotation);
			}
		}
		catch (Exception e) {
			e.printStackTrace(); // pimp my code sophisticated exception handling
		}
	}
	
	/*
	 * picr knows almost everything... ;-)
	 * we just give it the whole list
	 */
	protected void initSupportedIds() {
		Set<String> ids = Defaults.IDS.keySet();
		for(String id : ids) {
			supportedIds.put(id, new DummyIdModifier(id));
		}
	}

	@Override
	public void init() {/* nothing to be done here */}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(), 
				myType, 
				new String[] {"LIB.PICR", "PAS.MIO.IMPORT.MANAGER", IdSetMIO.myType}, 
				Defaults.MC_METAINFORMATION_PROVIDER, 
				new HashMap<String, Object>(), 
				"Michael Piechotta", 
				"piechott@student.uni-tuebingen.de", 
				"PICR", 
				"PICR" 
				);
		return pli;
	}
	
}
