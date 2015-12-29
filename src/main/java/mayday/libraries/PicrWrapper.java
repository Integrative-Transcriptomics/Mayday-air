package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class PicrWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.PICR", new String[0], "Libraries", (HashMap)null, "European Bioinformatics Institute", "http://www.ebi.ac.uk/Tools/picr", "A SOAP interface to PICR - Protein Identifier Cross-Reference Service", "PICR API");
   }
}
