import java.sql.Connection;
import java.sql.ResultSet;
import scala.reflect.BeanProperty;

class IndexPage(db: Connection) {
  val findPostListSQL = db.prepareStatement(
    """SELECT post.id, post.title, post.timestamp, post.contents,
              coalesce(comment_subquery.c, 0) as comment_count
       FROM post LEFT JOIN (
            SELECT comment.post_id id, count(*) c FROM comment, post
            WHERE post.id = comment.post_id
            GROUP BY comment.post_id)
       AS comment_subquery
       ON comment_subquery.id = post.id
       ORDER BY post.timestamp DESC
       LIMIT 10
       OFFSET ?""");
     
  val postCountSQL = db.prepareStatement(
    "SELECT count(*) count FROM post");

  def index(req: FCGIRequest): HTTPResponse = {
    val pageNum = (for {
      pageNumStr <- req.fields.get("page")
      pageNum <- IntUtil.toInt(pageNumStr)
    } yield pageNum) match {
      case Some(pageNum) => if(pageNum < 1) 1 else pageNum
      case None => 1
    }

    val template = Templates.get("index");

    template.add("next_page", nextPage(pageNum).orNull);
    template.add("previous_page", previousPage(pageNum).orNull);

    val results = findPostList(pageNum);
    while(results.next()) {
      template.add("entries", IndexEntry.fromRow(results));
    }

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      template.render());
  }

  def nextPage(currentPage: Int): Option[Int] = {
    val postCount = getPostCount();
    // return the next page number only if there are more pages
    // to display
    if(currentPage * 10 >= postCount)
      return None;
    else
      return Some(currentPage + 1);
  }

  def previousPage(currentPage: Int): Option[Int] = {
    if(currentPage > 1)
      return Some(currentPage - 1);
    else
      return None;
  }

  def findPostList(page:Int) : ResultSet = {
    findPostListSQL.setInt(1, (page - 1) * 10);
    return findPostListSQL.executeQuery();
  }

  def getPostCount() : Int = {
    val row = postCountSQL.executeQuery();
    row.next();
    return row.getInt("count");
  }
}

object IndexEntry {
    def fromRow(row:ResultSet) : IndexEntry = {
        return new IndexEntry(
            Post.fromRow(row),
            row.getInt("comment_count"));
    }
}

class IndexEntry(@BeanProperty val post:Post,
                 @BeanProperty val commentCount:Int)
{
}
