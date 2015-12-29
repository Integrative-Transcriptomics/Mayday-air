package mayday.metaimport.provider.kegg;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import keggapi.KEGGPortType;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.core.meta.types.StringListMIO;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.MIPathUtilities;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 * 
 * 'ko_id' is a KO identifier consisting of 'ko' and a ko number used in KEGG/KO. 
 * KO (KEGG Orthology) is an classification of orthologous genes defined by KEGG 
 * (e.g. 'ko:K02598' means a KO group for nitrite transporter NirC genes)
 *
 */
public class KoIdAdder extends AbstractAnnotationAdder<String> {

	public static final String PATH = "KEGG/KO Ids";
	
	/**
	 * 
	 */
	private KEGGPortType serv;

	/**
	 * 
	 * @param serv
	 * @param miManager
	 */
	public KoIdAdder(KEGGPortType serv, KEGGAPIAnnotationProvider provider){
		super("KO identifier", 
			   "'ko_id' is a KO identifier consisting of 'ko' and a ko number used in " +
			  "KEGG/KO. KO (KEGG Orthology) is an classification of orthologous genes " +
			  "defined by KEGG (e.g. 'ko:K02598' means a KO group for nitrite " +
			  "transporter NirC genes)", provider);
		this.serv 	= serv;
	}
	
	/*
	 * nothing to be done here yet
	 */
	@Override
	public void initGUI() {
		gui = null;
	}

	@Override
	public void addAnnotation(String annotation, MIGroup group, Object miExtendable) {
		MIType mio = group.getMIO(miExtendable);
		StringListMIO stringListMIO = new StringListMIO();
	
		// check if the group really returns a valid object
		if(mio == null) { 
			// append the new GOTerm
			stringListMIO.getValue().add(annotation);
			// and add to group
			group.add(miExtendable, stringListMIO);
		} else {
			// cast 
			stringListMIO = (StringListMIO) mio;
			// append the new GOTerm
			stringListMIO.getValue().add(annotation);
		}
	}

	@Override
	public MIGroup createMIGroup(MIManager miManager) {
		return MIPathUtilities.createMIGroup(miManager, PATH, "PAS.MIO.StringList");
	}

	@Override
	public List<String> getAnnotation(String id) {
		ArrayList<String> koIds = new ArrayList<String>();
		
		try{
			String[] result = serv.get_ko_by_gene(id); 

			if(result.length == 0)
				return koIds;
			
			for(String entry : result){
				koIds.add(entry);
			}
		}
		catch(RemoteException e){
			e.printStackTrace();
		}
		
		return koIds;
	}

}
