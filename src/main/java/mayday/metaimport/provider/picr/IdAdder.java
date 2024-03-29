package mayday.metaimport.provider.picr;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import uk.ac.ebi.picr.client.AccessionMapperInterface;
import uk.ac.ebi.picr.client.CrossReference;
import uk.ac.ebi.picr.client.UPEntry;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.MIPathUtilities;
import mayday.metaimport.core.id.IdSettings;
import mayday.metaimport.provider.picr.meta.types.IdSetMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.2 $, $Date: 2014/11/19 13:46:37 $
 *
 */
public class IdAdder extends AbstractAnnotationAdder<String> {

	/**
	 * Reference to the object that connects to PICR. 
	 */
	private AccessionMapperInterface port;

	private static enum OPTION {LOGICAL, IDENTICAL};
	
	private static Map<OPTION, String> OPTIONS2NAME = new HashMap<OPTION, String>();
	static {
		OPTIONS2NAME.put(OPTION.LOGICAL, "logical");
		OPTIONS2NAME.put(OPTION.IDENTICAL, "identical");
	}
	
	/**
	 * Stores the settings for this idAdder
	 */
	private Set<OPTION> settings;
	
	/**
	 * 
	 * @param name
	 * @param desc
	 * @param port
	 * @param provider
	 */
	public IdAdder(String name, String desc, AccessionMapperInterface port, PICRAnnotationProvider provider) {
		super(name, desc, provider);
		this.port = port;
		
		// select all options by default
		settings = new HashSet<OPTION>(Arrays.asList(OPTION.values()));
	}

	@Override
	public MIGroup createMIGroup(MIManager miManager) {
		return MIPathUtilities.createMIGroup(miManager, IdSettings.IDPATH + "/" + getName(), IdSettings.MIO_TYPE);
	}
	
	@Override
	public void addAnnotation(String id , MIGroup group, Object miExtendable) {
		// get MIO from group - may be null
		MIType mio = group.getMIO(miExtendable);
		
		IdSetMIO idSetMIO = new IdSetMIO();
	
		// check if the group really returns a valid object
		if(mio == null) { 
			// append the new id
			idSetMIO.getValue().add(id);
			// and add to group
			group.add(miExtendable, idSetMIO);
		} else {
			// cast 
			idSetMIO = (IdSetMIO) mio;
			// append the new id
			idSetMIO.getValue().add(id);
		}
	}

	@Override
	public List<String> getAnnotation(String query) {
		// getUPIForAccession()expects an array of databases to use.
		ArrayList<String> databases = new ArrayList<String>();
		
		// result list
		ArrayList<String> ids = new ArrayList<String>();
		
		databases.add(getName());
		List<UPEntry> result = port.getUPIForAccession(query, 
										 null, 
										 databases, 
										 null, 
										 false);

		// return on no result
		if(result.size() == 0)
			return ids;		
		
		try {
			// iterate over the results
			Iterator<UPEntry> it = result.iterator();
			while(it.hasNext()){
				UPEntry upEntry = it.next();
				
				if(settings.contains(OPTION.LOGICAL)) {
					List<CrossReference> logicalCrossReferences = upEntry.getLogicalCrossReferences();
					Iterator<CrossReference> logicalCrossReferenceIterator = logicalCrossReferences.iterator();
					while(logicalCrossReferenceIterator.hasNext()){
						String accession = logicalCrossReferenceIterator.next().getAccession();
						ids.add( accession );
					}
				}
				
				if(settings.contains(OPTION.IDENTICAL)) {
					List<CrossReference> identicalCrossReferences = upEntry.getIdenticalCrossReferences();
					Iterator<CrossReference> identicalCrossReferenceIterator = identicalCrossReferences.iterator();
					while(identicalCrossReferenceIterator.hasNext()){
						String accession = identicalCrossReferenceIterator.next().getAccession();
						ids.add( accession );
					}
				}
			}
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return ids;
	}

	@Override
	public void initGUI() {
		gui = new JPanel();
		gui.setLayout(new GridBagLayout());
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		
		for(final OPTION option : OPTION.values()) {
			final JCheckBox checkBox = new JCheckBox(OPTIONS2NAME.get(option));
			// initialize checkbox with settings
			checkBox.setSelected(settings.contains(option));
			checkBox.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					if(checkBox.isSelected())
						settings.add(option);
					else
						settings.remove(option);
				}
			});
			gui.add(checkBox, gbc);
		}
	}
	
	@Override
	public Collection<String> getErrorLog(){
		Collection<String> errorLog = new ArrayList<String>();
	
		if(settings.size() == 0)
			errorLog.add("Nothing selected!");
			
		return errorLog;
	}
	
}
