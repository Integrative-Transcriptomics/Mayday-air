package mayday.metaimport.provider.go;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.MIPathUtilities;
import mayday.metaimport.provider.go.meta.types.GOTermMIO;
import mayday.metaimport.provider.go.meta.types.GOTermMapMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 * Retrieves GO-Terms using the connection given by GOTermProvider and adds them to MIOs
 */
public class GOTermAdder extends AbstractAnnotationAdder<GOTerm> {
		
	/**
	 * MIPathUtilities for storing GOTermMIOs.
	 */
	public static final String PATH = "GO Terms";
	
	/**
	 * 
	 * @author Michael Piechotta
	 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
	 *
	 */
	private static enum ONTOLOGY {CELLULAR_COMPONENT, BIOLOGICAL_PROCESS, MOLECULAR_FUNCTION};

	/**
	 * 
	 */
	private static final HashMap<ONTOLOGY, String> ONTOLOGY2TABLE = new HashMap<ONTOLOGY, String>();
	static {
		ONTOLOGY2TABLE.put(ONTOLOGY.CELLULAR_COMPONENT, "cellular_component");
		ONTOLOGY2TABLE.put(ONTOLOGY.BIOLOGICAL_PROCESS, "biological_process");
		ONTOLOGY2TABLE.put(ONTOLOGY.MOLECULAR_FUNCTION, "molecular_function");
	}
	
	/**
	 * 
	 */
	private static final HashMap<ONTOLOGY, String> ONTOLOGY2NAME = new HashMap<ONTOLOGY, String>();
	static {
		ONTOLOGY2NAME.put(ONTOLOGY.CELLULAR_COMPONENT, "Cellular component");
		ONTOLOGY2NAME.put(ONTOLOGY.BIOLOGICAL_PROCESS, "Biological process");
		ONTOLOGY2NAME.put(ONTOLOGY.MOLECULAR_FUNCTION, "Molecular function");
	}
	
	/**
	 * Provider source.
	 */
	private Connection connection;

	/**
	 *
	 */
	private Set<ONTOLOGY> choosenOntologies;
	
	/**
	 * Term restriction appended to SQL query.
	 */
	private String termTypeRestriction;
	
	/**
	 * Constructor.
	 * @param connection
	 * @param provider
	 */
	public GOTermAdder(Connection connection, GOAnnotationProvider provider){
		super("GO-Terms", "Each entry in GO has a unique numerical identifier of the form GO:nnnnnnn, and a term name, e.g. cell, fibroblast growth factor receptor binding or signal transduction. Each term is also assigned to one of the three ontologies, molecular function, cellular gui or biological process.",
		provider);
		this.connection = connection;
		choosenOntologies = new HashSet<ONTOLOGY>();
		
		// preselect all ontologies
		choosenOntologies.addAll(Arrays.asList(ONTOLOGY.values()));
	
		termTypeRestriction = new String();
	}

	/*
	 * requires MIGroup to be able to store GOTermMapMIO
	 */
	@Override
	public void addAnnotation(GOTerm term, MIGroup group, Object miExtendable) {
		/*
		 * this version puts each GO-Term in a separate MIGroup
		 * pro: filtering through MIGroups. Question: What Probes have GO:xxxxxx through corresponding MIGroup.
		 * neg: huge amount of MIGroups 
		MIManager miManager = group.getMIManager();
		Directory root = miManager.getTreeRoot();
		String path = root.getPathFor(group) + "/" + term.getAcc();
		Directory dir = root.getDirectory(path, false);

		MIGroup child = null;
		if(dir == null) {
			child = miManager.newGroup("PAS.MIO.GOTerm", term.getAcc(), group);
			child.add(miExtendable, new GOTermMIO(term));
		} else {
			child = dir.getGroup();
			if(!child.contains(miExtendable)) {
				child.add(miExtendable, new GOTermMIO(term));
			}
		}*/
		
		// the MIO container for GO-Terms
		MIType mio = group.getMIO(miExtendable);
		GOTermMapMIO goTermMapMIO = new GOTermMapMIO();

		// MIO container for the GO-Term to be saved
		GOTermMIO goTerm = new GOTermMIO(term);
		
		// check if the group really returns a valid MIO container for GO-Terms
		if(mio == null) { 
			// append the new GOTermMIO
			goTermMapMIO.getValue().put(term.getAcc(), goTerm);
			// and add to group
			group.add(miExtendable, goTermMapMIO);
		} else {
			// cast 
			goTermMapMIO = (GOTermMapMIO) mio;
			// append the new GOTerm to existing MIO container for GO-Terms
			goTermMapMIO.getValue().put(term.getAcc(), goTerm);
		}
	}

	@Override
	public List<GOTerm> getAnnotation(String id) {
		ArrayList<GOTerm> terms = new ArrayList<GOTerm>();

		try{
			Statement stmt = connection.createStatement();
			
			boolean result = stmt.execute("SELECT term_definition.term_definition, term.term_type, term.acc, term.name, evidence.code "+
			"FROM gene_product "+
			"INNER JOIN gene_product_synonym ON (gene_product.id=gene_product_synonym.gene_product_id) "+
			"INNER JOIN association ON (gene_product.id=association.gene_product_id) "+
			"INNER JOIN evidence ON (association.id=evidence.association_id) "+
			"INNER JOIN term ON (association.term_id=term.id) "+
			"INNER JOIN term_definition ON (term_definition.term_id=term.id) "+
			"WHERE gene_product_synonym.product_synonym='"+ id + "' " + getTermTypeRestriction());
			
			if(!result)
				return terms; 
			
			// collect results
			ResultSet rs = stmt.getResultSet();
			while(rs.next()){
				GOTerm term = new GOTerm();
				term.setAcc(rs.getString("acc"));
				term.setName(rs.getString("name"));
				term.setTerm_type(rs.getString("term_type"));
				term.setTerm_definition(rs.getString("term_definition"));
				term.setCode(rs.getString("code"));
				
				terms.add(term);
			}
			
			return terms;
		}
		catch (SQLException e) {
			e.printStackTrace(); 
		}
		
		return terms;
	}
	
	/*
	 * Choose which Ontologies to retrieve.
	 */
	@Override
	public void initGUI() {
		gui = new JPanel();
		gui.setLayout(new GridBagLayout());

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.LAST_LINE_START;
		
		gui.add(new JLabel("Ontologies:"), gbc);
		// creates options to choose ontology and aligns them
		for(final ONTOLOGY ontology : ONTOLOGY.values()) {
			final JCheckBox checkBox = new JCheckBox(ONTOLOGY2NAME.get(ontology));
			checkBox.setSelected(choosenOntologies.contains(ontology));
			checkBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(checkBox.isSelected())
						addOntology(ontology);
					else
						removeOntology(ontology);
				}
			});
			// add to gui
			gui.add(checkBox, gbc);
		}
	}

	/**
	 * 
	 * @param ontology
	 */
	public void addOntology(ONTOLOGY ontology) {
		choosenOntologies.add(ontology);
	}

	/**
	 * 
	 * @param ontology
	 */
	public void removeOntology(ONTOLOGY ontology) {
		choosenOntologies.remove(ontology);
	}
	
	// TODO inefficient repetitve calling
	// is processed each time getAnnotation is called
	// generated this restriction parallel to configuration of adder
	private String getTermTypeRestriction() {
		if(termTypeRestriction.length() == 0) {
			// all are selected, no restriction needed
			if(choosenOntologies.size() == ONTOLOGY.values().length)
				return new String();
			
		    Iterator<ONTOLOGY> i = choosenOntologies.iterator();
		
			StringBuilder sb = new StringBuilder();
			sb.append(" AND term.term_type IN (");
			for (;;) {
			    ONTOLOGY e = i.next();
			    sb.append("'" + ONTOLOGY2TABLE.get(e) + "'");
			    if (! i.hasNext()) {
			    	termTypeRestriction = sb.append(')').toString(); 
			    	return termTypeRestriction;
			    }
			    sb.append(",");
			}
		} else 
			return termTypeRestriction;
	}
	
	@Override
	public MIGroup createMIGroup(MIManager miManager) {
		return MIPathUtilities.createMIGroup(miManager, PATH, "PAS.MIO.GOTermMap");
	}	
	
	@Override
	public Collection<String> getErrorLog(){
		ArrayList<String> errorLog = new ArrayList<String>();
		
		if(choosenOntologies.size() == 0)
			errorLog.add("No ontology selected!");
		
		return errorLog;
	}
	
}
