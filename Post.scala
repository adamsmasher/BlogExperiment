import java.sql.ResultSet;
import java.sql.Timestamp;
import scala.reflect.BeanProperty;

object Post {
  def fromRow(row:ResultSet) : Post = {
    return new Post(
      row.getInt("id"),
      row.getString("title"),
      row.getString("contents"),
      row.getTimestamp("timestamp"));
  }
}

class Post(@BeanProperty val id: Int,
           @BeanProperty val title: String,
           @BeanProperty val contents: String,
           @BeanProperty val timestamp: Timestamp)
{
}
