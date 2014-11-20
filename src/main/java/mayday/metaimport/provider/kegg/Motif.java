package mayday.metaimport.provider.kegg;

import keggapi.MotifResult;

/**
 * 
 *  @author Michael Piechotta
 *  @version $Revision: 1.1 $, $Date: 2008/12/03 13:29:57 $
 *  taken from {@link MotifResult} part of KEGG API
 */

public class Motif {

	private String motif_id;
    private String definition;
    private int start_position;
    private int end_position;
    private double score;
    private double evalue; 
    
    public Motif() {}

    public Motif(String motif_id,
    			  String definition,
    			  int start_position,
    			  int end_position,
    			  double score,
    			  double evalue) {
    	this.motif_id 		= motif_id;
    	this.definition 	= definition;
    	this.start_position = start_position;
    	this.end_position 	= end_position;
    	this.score			= score;
    	this.evalue			= evalue;
    }
    
    public Motif(MotifResult motifResult) {
    	motif_id 		= motifResult.getMotif_id();
        definition 		= motifResult.getDefinition();
        start_position 	= motifResult.getStart_position();
        end_position 	= motifResult.getEnd_position();
        score 			= motifResult.getScore();
        evalue 			= motifResult.getEvalue();
    }
    
    public String getMotif_id() {
    	return motif_id;
    }
    
    public String getDefinition() {
    	return definition;
    }
    
    public int getStart_position() {
    	return start_position;
    }
    
    public int getEnd_position() {
    	return end_position;
    }

    public double getScore() {
    	return score;
    }
    
    public double getEvalue() {
    	return evalue;
    }
    
}
