import java.sql.DriverManager;
import java.sql.Connection;

object DB {
  def connect() : Connection = {
    Class.forName("org.postgresql.Driver");
    return DriverManager.getConnection(
      "jbdc:postgresql://localhost:PORT/blog",
      "USERNAME", "PASSWORD");
  }
}
