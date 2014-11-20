package mayday.metaimport.core.id;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 * A dummy IdModifier - just returns the id. Makes life easier dont't.
 */
public class DummyIdModifier extends AbstractIdModifier {

	/**
	 * Constructor.
	 * @param idType
	 */
	public DummyIdModifier(String idType) {
		super(idType);
	}
	
	/**
	 * It is the dummy modifier so it does not do anything with the id but returns it
	 */
	@Override
	protected String manufactureId(String id) {
		return id;
	}

}
