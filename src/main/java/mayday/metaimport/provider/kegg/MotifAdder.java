package mayday.metaimport.provider.kegg;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import keggapi.KEGGPortType;
import keggapi.MotifResult;
import mayday.core.meta.MIGroup;
import mayday.core.meta.MIManager;
import mayday.core.meta.MIType;
import mayday.metaimport.core.AbstractAnnotationAdder;
import mayday.metaimport.core.MIPathUtilities;
import mayday.metaimport.provider.kegg.meta.types.MotifMIO;
import mayday.metaimport.provider.kegg.meta.types.MotifMapMIO;

/**
 * 
 * @author Michael Piechotta
 * @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 * 
 * 'motif_id' is a motif identifier consisting of motif database names 
 * ('ps' for prosite, 'bl' for blocks, 'pr' for prints, 'pd' for prodom, 
 * and 'pf' for pfam) and a motif entry name. (e.g. 'pf:DnaJ' means a 
 * Pfam database entry 'DnaJ').
 * 
 */
public class MotifAdder extends AbstractAnnotationAdder<Motif> {

	public static final String PATH = "KEGG/Motifs";
	
	/**
	 * 
	 */
	private KEGGPortType serv;

	/**
	 * 
	 */
	private String database;

	/**
	 * 
	 * @param serv
	 * @param mi3Manager
	 */
	public MotifAdder(KEGGPortType serv, KEGGAPIAnnotationProvider provider) {
		super(
				"Motif",
				"'motif_id' is a motif identifier consisting of motif database names"
						+ " ('ps' for prosite, 'bl' for blocks, 'pr' for prints, 'pd' for prodom,"
						+ " and 'pf' for pfam) and a motif entry name. (e.g. 'pf:DnaJ' means a"
						+ " Pfam database entry 'DnaJ').", provider);
		this.serv = serv;
		database = new String();
	}

	@Override
	public void initGUI() {
		ArrayList<String> options = new ArrayList<String>();
		// see http://www.genome.jp/kegg/soap/doc/keggapi_manual.html#label:67
		// for details
		options.add(new String("all"));
		options.add(new String("pfam"));
		options.add(new String("pspt"));
		options.add(new String("pspf"));
		
		gui = new JPanel();
		gui.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.anchor = GridBagConstraints.FIRST_LINE_START;
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		
		JLabel label = new JLabel("Database");
		gui.add(label, gbc);
		
		JComboBox select = new JComboBox(options.toArray());
		select
				.setToolTipText("The value of \'Database\' can be \'pfam\' for Pfam, \'pspt\' for PROSITE pattern, \'pspf\' for PROSITE profile or \'all\'.");
		gui.add(select, gbc);
		select.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JComboBox selectedChoice = (JComboBox) e.getSource();
				database = (String) selectedChoice.getSelectedItem();
			}
		});
		// init the gui
		database = (String) select.getSelectedItem();
	}

	@Override
	public void addAnnotation(Motif motif, MIGroup group, Object miExtendable) {
		/*
		 * See GOTermAdder for a discussion
		 * 
		MIManager miManager = group.getMIManager();
		Directory root = miManager.getTreeRoot();
		String path = root.getPathFor(group) + "/" + motif.getMotif_id();
		Directory dir = root.getDirectory(path, false);

		MIGroup child = null;
		if(dir == null) {
			child = miManager.newGroup("PAS.MIO.Motif", motif.getMotif_id(), group);
			child.add(miExtendable, new MotifMIO(motif));
		} else {
			child = dir.getGroup();
			if(!child.contains(miExtendable)) {
				child.add(miExtendable, new MotifMIO(motif));
			}
		}*/
		
		// the MIO container for Motifs
		MIType mio = group.getMIO(miExtendable);
		MotifMapMIO motifMapMIO = new MotifMapMIO();

		// MIO container for the GO-Term to be saved
		MotifMIO motifMIO = new MotifMIO(motif);
		
		// check if the group really returns a valid MIO container for Motifs
		if(mio == null) { 
			// append the new GOTermMIO
			motifMapMIO.getValue().put(motif.getMotif_id(), motifMIO);
			// and add to group
			group.add(miExtendable, motifMapMIO);
		} else {
			// cast 
			motifMapMIO = (MotifMapMIO) mio;
			// append the new GOTerm to existing MIO container for GO-Terms
			motifMapMIO.getValue().put(motif.getMotif_id(), motifMIO);
		}

		
	}

	@Override
	public MIGroup createMIGroup(MIManager miManager) {
		return MIPathUtilities.createMIGroup(miManager, PATH, "PAS.MIO.MotifMap");
	}

	@Override
	public List<Motif> getAnnotation(String id) {
		ArrayList<Motif> motifs = new ArrayList<Motif>();
		try {
			// we just take all - in doubt consider KEGG API
			MotifResult[] result = serv.get_motifs_by_gene(id, database);

			if (result.length == 0)
				return motifs;

			for (MotifResult entry : result) {
				motifs.add(new Motif(entry));
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return motifs;
	}

}
