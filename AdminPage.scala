import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import scala.reflect.BeanProperty;

class AdminPage(db: Connection) {
  val findPostListSQL = db.prepareStatement(
    """SELECT id, title, timestamp FROM post
       ORDER BY timestamp DESC""");
     
  def admin(req: FCGIRequest): HTTPResponse = {
    val template = Templates.get("admin");

    val results = findPostListSQL.executeQuery();
    while(results.next()) {
      template.add("entries", AdminEntry.fromRow(results));
    }

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      template.render());
  }

  object AdminEntry {
    def fromRow(row:ResultSet) : AdminEntry = {
        return new AdminEntry(
            row.getInt("id"),
            row.getString("title"),
            row.getTimestamp("timestamp"));
    }
  }

  class AdminEntry(@BeanProperty val id:Int,
                   @BeanProperty val title:String,
                   @BeanProperty val timestamp: Timestamp)
  {
  }
}


