package mayday.metaimport.provider.go;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;

import com.mysql.jdbc.exceptions.MySQLNonTransientConnectionException;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.AbstractAnnotationProvider;
import mayday.metaimport.core.AnnotationManager;
import mayday.metaimport.core.Defaults;
import mayday.metaimport.core.id.DummyIdModifier;
import mayday.metaimport.provider.go.meta.types.GOTermMIO;
import mayday.metaimport.provider.go.meta.types.GOTermMapMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 * GO database. MYSQL connection - pretty fast.
 */
public class GOAnnotationProvider extends AbstractAnnotationProvider {

	/**
	 * Reference to the connection to be used by AnnotationAdders that this providers offers.
	 */
	private Connection connection;

	/**
	 * Link of the MySQL database to be used.
	 */
	private String url;
	
	/**
	 * Name of the database to connect to.
	 */
	private String database;
	
	/**
	 * Username used to connect to the database.
	 */
	private String user;
	
	/**
	 * Password used to connect to the database.
	 */
	private String pass;
	
	/**
	 * Port used to connect to the database.
	 */
	private int port;
	
	/**
	 * Default constructor
	 */
	public GOAnnotationProvider(){
		super("Gene Ontology Database", 
				"The Gene Ontology project provides a controlled vocabulary to describe gene and gene product attributes in any organism.");
		
		// standard initialization for a connection to GO database
		// TODO make this configurable through pluma
		url = "mysql.ebi.ac.uk";
		database = "go_latest";
		user = "go_select";
		pass = "amigo";
		port = 4085;
		
		// FIXME remove
		/*url = "localhost";
		database = "mygo";
		user = "root";
		pass = "root";
		port = 3306;
		*/
		logoPath = Defaults.PLUGIN_DIR + "go/image/logo-obo.gif";
	}

	@Override
	protected boolean initConnection() {
		// choose driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://"+ url + ":" + port + "/" + database,
				  	  user,
				  	  pass);
		}
		catch(ClassNotFoundException e){
			description = "JDBC.MYSQL driver not found!";
			System.out.println("GO ClassNotFound Exception:");
			e.printStackTrace();
			return false;
		}
		catch(MySQLNonTransientConnectionException e) {
			System.out.println("GO MySQLTransientConnection Exception:");
			e.printStackTrace();
			return false;
		}
		catch(Exception e) {
			description = e.getMessage();
			System.out.println("GO Exception:");
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	protected void initAdders() {
		// add adders that can be used
		AbstractAnnotationAdder<GOTerm> annotation = new GOTermAdder(connection, this); 
		adders.put(annotation.getName(), annotation);
	}
	
	@Override
	protected void initSupportedIds() {
		// see {Defaults}
		supportedIds.put("REFSEQ", new DummyIdModifier("REFSEQ"));
		supportedIds.put("SGD", new DummyIdModifier("SGD"));
	}

	@Override
	public void init() {/* nothing to be done here */}

	@Override
	public PluginInfo register() throws PluginManagerException {
		PluginInfo pli = new PluginInfo(
				getClass(), 
				"PAS.MIO.IMPORT.GO", 
				new String[] {"LIB.JDBC", AnnotationManager.myType, GOTermMIO.myType, GOTermMapMIO.myType}, 
				Defaults.MC_METAINFORMATION_PROVIDER,
				new HashMap<String, Object>(), 
				"Michael Piechotta",
				"piechott@student.uni-tuebingen.de", 
				"Connects to Gene Ontology database", 
				"GO Database");
		return pli;
	}

}
