package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class KeggWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.KEGG", new String[0], "Libraries", (HashMap)null, "The Kanehisa Laboratory, see http://kanehisa.kuicr.kyoto-u.ac.jp/people.html", "http://www.genome.jp/kegg/soap/", "SOAP Interface to the Kyoto Encyclopedia of Genes and Genomes - free for academic use.<br><br>Relevant articles:<br><ul><li>Kanehisa, M., Araki, M., Goto, S., Hattori, M., Hirakawa, M., Itoh, M., Katayama, T., Kawashima, S., Okuda, S., Tokimatsu, T., and Yamanishi, Y.; KEGG for linking genomes to life and the environment. Nucleic Acids Res. 36, D480-D484 (2008).</li><li>Kanehisa, M., Goto, S., Hattori, M., Aoki-Kinoshita, K.F., Itoh, M., Kawashima, S., Katayama, T., Araki, M., and Hirakawa, M.; From genomics to chemical genomics: new developments in KEGG. Nucleic Acids Res. 34, D354-357 (2006).</li><li>Kanehisa, M. and Goto, S.; KEGG: Kyoto Encyclopedia of Genes and Genomes. Nucleic Acids Res. 28, 27-30 (2000).</li></ul>", "KEGG API");
   }
}
