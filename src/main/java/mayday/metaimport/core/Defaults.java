package mayday.metaimport.core;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:56 $
 * 
 * TODO change to XML or some other format that can be edited without changing
 * source code.
 * 
 */
public class Defaults {

	/**
	 * Id that identifies AnnotationProvider in the name plugin system
	 */
	public static final String MC_METAINFORMATION_PROVIDER = "Annotation provider";

	/**
	 * mapping of PICR identification to a link
	 */
	static public final Map<String, String> IDS = new HashMap<String, String>();

	/**
	 * Some template string to be replaced by the id E.g.: your link is
	 * www.some-bio-db.com/something/index.html?id=yal023c or something else one
	 * would write a template as follow:
	 * www.some-bio-db.com/something/index.html?id=ID_TEMPLATE
	 */
	static public final String ID_TEMPLATE = new String("######");

	/**
	 * List manually generated from port.getMappedDatabaseNames() instead of
	 * invoking the method to establish independence of PICR "When PICR is
	 * temporally down, we will be able to use GO..."
	 * 
	 * needs maintenance TODO make it an XML document... and update it
	 * automatically, when PICR works. Update link templates.
	 */
	static {
		IDS.put("EMBL",
				"http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+[emblcds-PROBE:'"
						+ Defaults.ID_TEMPLATE + "']");
		IDS.put("EMBLWGS", new String());
		IDS.put("EMBL_ANNCON", new String());
		IDS.put("EMBL_TPA", new String());
		IDS.put("ENSEMBL", new String());
		IDS.put("ENSEMBL_ARMADILLO", new String());
		IDS.put("ENSEMBL_BUSHBABY", new String());
		IDS.put("ENSEMBL_CAT", new String());
		IDS.put("ENSEMBL_CBRIGGSAE", new String());
		IDS.put("ENSEMBL_CELEGANS", new String());
		IDS.put("ENSEMBL_CHICKEN", new String());
		IDS.put("ENSEMBL_CHIMP", new String());
		IDS.put("ENSEMBL_CIONA", new String());
		IDS.put("ENSEMBL_COW", new String());
		IDS.put("ENSEMBL_DOG", new String());
		IDS.put("ENSEMBL_ELEPHANT", new String());
		IDS.put("ENSEMBL_ERINACEUS", new String());
		IDS.put("ENSEMBL_FLY", new String());
		IDS.put("ENSEMBL_FUGU", new String());
		IDS.put("ENSEMBL_GUINEA_PIG", new String());
		IDS.put("ENSEMBL_HEDGEHOG", new String());
		IDS.put("ENSEMBL_HONEYBEE", new String());
		IDS.put("ENSEMBL_HUMAN", new String());
		IDS.put("ENSEMBL_MEDAKA", new String());
		IDS.put("ENSEMBL_MICROBAT", new String());
		IDS.put("ENSEMBL_MOSQUITO", new String());
		IDS.put("ENSEMBL_MOUSE", new String());
		IDS.put("ENSEMBL_OPOSSUM", new String());
		IDS.put("ENSEMBL_PLATYPUS", new String());
		IDS.put("ENSEMBL_RABBIT", new String());
		IDS.put("ENSEMBL_RAT", new String());
		IDS.put("ENSEMBL_RHESUS_MACAQUE", new String());
		IDS.put("ENSEMBL_SQUIRREL", new String());
		IDS.put("ENSEMBL_STICKLEBACK", new String());
		IDS.put("ENSEMBL_TETRAODON", new String());
		IDS.put("ENSEMBL_TREE_SHREW", new String());
		IDS.put("ENSEMBL_XENOPUS", new String());
		IDS.put("ENSEMBL_YF_MOSQUITO", new String());
		IDS.put("ENSEMBL_ZEBRAFISH", new String());
		IDS.put("EPO",
				"http://srs.ebi.ac.uk/srsbin/cgi-bin/wgetz?-e+[EPO_PRT:'"
						+ Defaults.ID_TEMPLATE + "']");
		IDS.put("FLYBASE", new String());
		IDS.put("H_INV", new String());
		IDS.put("IPI", new String());
		IDS.put("JPO", new String());
		IDS.put("PDB", new String());
		IDS.put("PIR",
				"http://pir.georgetown.edu/cgi-bin/textsearch.pl?field0=PIIDS%2CPIACCS&query0="
						+ Defaults.ID_TEMPLATE + "+&search=Search");
		IDS.put("PIRARC", new String());
		IDS.put("PRF", new String());
		IDS.put("REFSEQ",
				"http://www.ncbi.nlm.nih.gov/entrez/viewer.fcgi?db=protein&val="
						+ Defaults.ID_TEMPLATE);
		IDS.put("REFSEQ_HUMAN", new String());
		IDS.put("REFSEQ_MOUSE", new String());
		IDS.put("REFSEQ_RAT", new String());
		IDS.put("REFSEQ_ZEBRAFISH", new String());
		IDS.put("SGD", "http://db.yeastgenome.org/cgi-bin/locus.pl?locus="
				+ Defaults.ID_TEMPLATE);
		IDS.put("SWISSPROT", "http://www.ebi.uniprot.org/entry/"
				+ Defaults.ID_TEMPLATE);
		IDS.put("SWISSPROT_VARSPLIC", new String());
		IDS.put("TAIR_ARABIDOPSIS", new String());
		IDS.put("TREMBL", "http://www.ebi.uniprot.org/entry/"
				+ Defaults.ID_TEMPLATE);
		IDS.put("TREMBL_VARSPLIC", new String());
		IDS.put("TROME_CE", new String());
		IDS.put("TROME_DM", new String());
		IDS.put("TROME_HS", new String());
		IDS.put("TROME_MM", new String());
		IDS.put("UNIMES", new String());
		IDS.put("USPTO", new String());
		IDS.put("VEGA_DOG", new String());
		IDS.put("VEGA_HUMAN", new String());
		IDS.put("VEGA_MOUSE", new String());
		IDS.put("VEGA_ZEBRAFISH", new String());
		IDS.put("WORMBASE", new String());
	}

	/**
	 * Returns a link String that represents a link to the database represented
	 * by idType and an id where one can retrieve further information.
	 * Make sure that id is valid for chosen idType.
	 *
	 * @param id
	 *            to be used
	 * @param idType
	 *            to be used
	 * @return String link
	 */
	public static String getLink(String id, String idType) {
		String link = Defaults.IDS.get(idType);
		if (link.isEmpty())
			return new String();

		return link.replaceFirst(Defaults.ID_TEMPLATE, idType);
	}

	/**
	 * 
	 */
	public static String PLUGIN_DIR = "mayday/metaimport/provider/";
}
