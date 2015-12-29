package mayday.libraries;

import java.util.HashMap;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;

public class JdbcWrapper extends AbstractPlugin {
   public void init() {
   }

   public PluginInfo register() throws PluginManagerException {
      return new PluginInfo(this.getClass(), "LIB.JDBC", new String[0], "Libraries", (HashMap)null, "The Derby, Postgres, and MySQL development teams.", "-", "A collection of JDBC libraries for different database systems.", "JDBC");
   }
}
