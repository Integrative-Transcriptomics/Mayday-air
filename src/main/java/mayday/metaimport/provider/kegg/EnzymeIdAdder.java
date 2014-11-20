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
 * 'enzyme_id' is an enzyme identifier consisting of database name 'ec' and an 
 * enzyme code used in KEGG/LIGAND ENZYME database. (e.g. 'ec:1.1.1.1' means 
 * an alcohol dehydrogenase enzyme)
 * 
 */
public class EnzymeIdAdder extends AbstractAnnotationAdder<String>{
	
	public static final String PATH = "KEGG/LIGAND Enzyme Ids";
	
	/**
	 * the connection object to SOAP from KEGG
	 */
	private KEGGPortType serv;
	
	/**
	 * Constructor
	 * @param serv
	 * @param provider
	 */
	public EnzymeIdAdder(KEGGPortType serv, KEGGAPIAnnotationProvider provider) {
		super("Enzyme identifier",
			   "'enzyme_id' is an enzyme identifier consisting of database name 'ec' and an" +
			   " enzyme code used in KEGG/LIGAND ENZYME database. (e.g. 'ec:1.1.1.1' means" + 
			   " an alcohol dehydrogenase enzyme)", provider);
		this.serv = serv;
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
	public List<String> getAnnotation(String id){
		ArrayList<String> enzymeIds = new ArrayList<String>();
		try{
			String[] result = serv.get_enzymes_by_gene(id); 
			
			if(result.length == 0)
				return enzymeIds;
			
			for(String entry : result){
				enzymeIds.add(entry);
			}
			
			return enzymeIds;
		}
		catch(RemoteException e) {
			e.printStackTrace();
		}
		
		return enzymeIds;
	}
	
	/*
	 * don't need here anything
	 */
	@Override
	public void initGUI() { 
		gui = null;
	}

	@Override
	public MIGroup createMIGroup(MIManager miManager) {
		return MIPathUtilities.createMIGroup(miManager, PATH, "PAS.MIO.StringList");
	}
	
}