import java.sql.ResultSet;
import java.sql.Timestamp;

object Post {
  def fromRow(row:ResultSet) : Post = {
    return new Post(
      row.getString("title"),
      row.getString("contents"),
      row.getTimestamp("timestamp"));
  }
}

class Post(val title: String,
	   val contents: String,
	   val timestamp: Timestamp)
{
}
