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
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 * 
 * TODO Problem: hier wird eigentlich eine Set/Menge von Genen erwartet...
 * 
 */
public class PathwayAdder extends AbstractAnnotationAdder<String> {
	
	public final String PATH = "KEGG/Pathways";
	
	/**
	 * 
	 */
	private KEGGPortType serv;
	
	/**
	 * 
	 * @param serv
	 */
	public PathwayAdder(KEGGPortType serv, KEGGAPIAnnotationProvider provider){
		super("Pathway", "Pathway", provider);
		this.serv = serv;
	}

	/*
	 * nothing to be done here 
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
			// append the new Pathway
			stringListMIO.getValue().add(annotation);
			// and add to group
			group.add(miExtendable, stringListMIO);
		} else {
			// cast 
			stringListMIO = (StringListMIO) mio;
			// append the new Pathway
			stringListMIO.getValue().add(annotation);
		}
	}

	@Override
	public MIGroup createMIGroup(MIManager miManager) {
		return MIPathUtilities.createMIGroup(miManager, PATH, "PAS.MIO.StringList");
	}

	@Override
	public List<String> getAnnotation(String id) {
		ArrayList<String> pathways = new ArrayList<String>();
		try{
			String[] wrapper = {
					  id
			};
			
			// expects an array of genes - we wrap it
			String[] result = serv.get_pathways_by_genes(wrapper); 

			if(result.length == 0)
				return pathways;
	
			for(String entry : result){
				pathways.add(entry);
			}
		}
		catch(RemoteException e){
			e.printStackTrace();
		}
		
		return pathways;
	}

}
