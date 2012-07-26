import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.util.PSQLState;

object DB {
  def connect() : Connection = {
    Class.forName("org.postgresql.Driver");
    val port = System.getProperty("BLOGDB_PORT");
    val username = System.getProperty("BLOGDB_USERNAME");
    val password = System.getProperty("BLOGDB_PASSWORD");
    return DriverManager.getConnection(
      "jbdc:postgresql://localhost:"+port+"/blog",
      username, password);
  }

  def executeQueryNonEmpty(stmt: PreparedStatement) : Option[ResultSet] = {
    try {
      val row = stmt.executeQuery();
      row.next();
      return Some(row);
    } catch {
      case e:SQLException => {
        // The cursor will be in an invalid state after next() if the result set
        // is empty
        if(e.getSQLState() equals PSQLState.INVALID_CURSOR_STATE.getState())
        {
          return None;
        }
        else {
          throw e;
        }
      }
    }
  }
}
