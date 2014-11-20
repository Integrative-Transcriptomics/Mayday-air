package mayday.metaimport.provider.kegg;

import java.util.HashMap;
import java.util.Map;

import keggapi.Definition;
import keggapi.KEGGLocator;
import keggapi.KEGGPortType;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.AbstractAnnotationProvider;
import mayday.metaimport.core.AnnotationManager;
import mayday.metaimport.core.Defaults;
import mayday.metaimport.provider.kegg.meta.types.MotifMIO;
import mayday.metaimport.provider.kegg.meta.types.MotifMapMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 *
 */
public class KEGGAPIAnnotationProvider extends AbstractAnnotationProvider {

	public final static String myType = "PAS.MIO.IMPORT.KEGG";
	
	/**
	 * 
	 */
	private KEGGLocator locator;
	
	/**
	 * 
	 */
	private KEGGPortType serv;

	/**
	 * Default Constructor.
	 */
	public KEGGAPIAnnotationProvider(){
		super("KEGG API", "Kyoto Encyclopedia of Genes and Genomes. KEGG is a database of biological systems, consisting of genetic building blocks of genes and proteins (KEGG GENES), chemical building blocks of both endogenous and exogenous substances (KEGG LIGAND), molecular wiring diagrams of interaction and reaction networks (KEGG PATHWAY), and hierarchies and relationships of various biological objects (KEGG BRITE). KEGG provides a reference knowledge base for linking genomes to biological systems and also to environments by the processes of PATHWAY mapping and BRITE mapping.");

		logoPath = Defaults.PLUGIN_DIR + "kegg/image/kegg2.gif";
	}

	@Override
	protected boolean initConnection(){
		locator 	= new KEGGLocator();
		try{
			serv = locator.getKEGGPort();
			if(serv.list_databases().length == 0)
				throw new Exception();
		}
		catch(Exception e){
			System.out.println("KEGG API: Exception:");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	@Override
	protected void initAdders() {
		// add the adders
		AbstractAnnotationAdder<String> koIdAdder = new KoIdAdder(serv, this); 
		adders.put(koIdAdder.getName(), koIdAdder);
		
		AbstractAnnotationAdder<String> enzymeIdAdder = new EnzymeIdAdder(serv, this); 
		adders.put(enzymeIdAdder.getName(), enzymeIdAdder);

		AbstractAnnotationAdder<Motif> motifAdder= new MotifAdder(serv, this); 
		adders.put(motifAdder.getName(), motifAdder);

		AbstractAnnotationAdder<String> pathwaysAdder = new PathwayAdder(serv, this); 
		adders.put(pathwaysAdder.getName(), pathwaysAdder);
	}
	
	@Override
	protected void initSupportedIds() {
		// for some reason KEGG API needs the organisms prefix: sce:gene_id 
		try {
			// get the organisms KEGG knows
			Definition[] definitions  = serv.list_organisms();
			
			// build a map organisms -> prefix (to be appended to the id)
			Map<String, String> organism2prefix = new HashMap<String, String>();
			for(Definition definition : definitions) {
				String orga = definition.getDefinition();
				// string to be append as a prefix
				String prefix = definition.getEntry_id() + ":";
				organism2prefix.put(orga, prefix);
			}
		
			KEGGAPI2SGDIdmodifier SGDmodifier = new KEGGAPI2SGDIdmodifier("SGD", organism2prefix);
			SGDmodifier.initComponent();
			supportedIds.put("SGD", SGDmodifier);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init() {}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(),
				myType,
				new String[] {"LIB.KEGG", AnnotationManager.myType, MotifMapMIO.myType, MotifMIO.myType},
				Defaults.MC_METAINFORMATION_PROVIDER,
				new HashMap<String, Object>(),
				"Michael Piechotta",
				"piechott@student.uni-tuebingen.de",
				"KEGG API",
				"KEGG API");
		return pli;
	}
	
}
