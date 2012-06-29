import java.sql.DriverManager;
import java.sql.Connection;

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
}
