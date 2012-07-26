import java.sql.ResultSet;
import java.sql.Timestamp;
import scala.reflect.BeanProperty;

object Comment {
  def fromRow(row:ResultSet) : Comment = {
    return new Comment(
      row.getInt("id"),
      row.getString("title"),
      row.getString("contents"),
      row.getTimestamp("timestamp"));
  }
}

class Comment(@BeanProperty val id: Int,
              @BeanProperty val title: String,
              @BeanProperty val contents: String,
              @BeanProperty val timestamp: Timestamp)
{
}
