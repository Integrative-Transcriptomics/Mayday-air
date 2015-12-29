package mayday.metaimport.provider.go;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 * This class represents a GO-Term
 * feel free to add more
 * 
 * TODO
 * make use of OLS (ontology lookup service)
 */
public class GOTerm {

	/**
	 * 
	 */
	private String acc;
	
	/**
	 * 
	 */
	private String name;
	
	/**
	 * 
	 */
	private String term_type;
	
	/**
	 * 
	 */
	private String term_definition;
	
	/**
	 * 
	 */
	private String code;

	/**
	 * Default constructor.
	 */
	public GOTerm(){
		name = new String();
		term_type = new String();
		acc = new String();
		term_definition = new String();
		code = new String();
	}

	/**
	 * 
	 * @param acc
	 * @param name
	 * @param term_type
	 * @param term_definition
	 * @param reference
	 * @param code
	 */
	public GOTerm(
			String acc,
			String name,
			String term_type,
			String term_definition,
			String code) {
		this.name = name;
		this.term_type = term_type;
		this.acc = acc;
		this.term_definition = term_definition;
		this.code = code;
	}

	/**
	 * 
	 * @return
	 */
	public String getAcc() {
		return acc;
	}

	/**
	 * 
	 * @param acc
	 */
	public void setAcc(String acc) {
		this.acc = acc;
	}

	/**
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @return
	 */
	public String getCode() {
		return code;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getTerm_definition() {
		return term_definition;
	}

	/**
	 * 
	 * @param term_definition
	 */
	public void setTerm_definition(String term_definition) {
		this.term_definition = term_definition;
	}

	/**
	 * 
	 * @return
	 */
	public String getTerm_type() {
		return term_type;
	}

	/**
	 * 
	 * @param term_type
	 */
	public void setTerm_type(String term_type) {
		this.term_type = term_type;
	}
	
	/**
	 * 
	 * @param code
	 */
	public void setCode(String code) {
		this.code = code;
	}
	
}
