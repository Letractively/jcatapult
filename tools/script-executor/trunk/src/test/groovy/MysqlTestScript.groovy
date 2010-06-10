import org.jcatapult.migration.scripts.Script
import org.jcatapult.migration.scripts.ScriptContext
import java.sql.ResultSet
import java.sql.Statement
import java.sql.Connection

/**
 * @author jhumphrey
 */
public class MysqlTestScript implements Script {

  void execute(ScriptContext scriptContext) {
    Connection conn = scriptContext.getConnection();
    Statement st = conn.createStatement();
    ResultSet rs = st.executeQuery("SELECT * FROM foo where bar = 'baz'");
    while (rs.next()) {
      System.out.print("1 column returned [" + rs.getString(1) + "]");
    }
    rs.close();
    st.close();
  }
}