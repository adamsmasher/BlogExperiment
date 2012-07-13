import java.sql.Connection;
import java.sql.ResultSet;

class IndexPage(db: Connection) {
  val findPostListSQL = db.prepareStatement(
    "SELECT * FROM post ORDER BY timestamp DESC LIMIT 10 OFFSET ?"
  );
  val postCountSQL = db.prepareStatement(
    "SELECT count(*) count FROM post");

  def index(req: FCGIRequest): HTTPResponse = {
    val pageNum = (for {
      pageNumStr <- req.fields.get("page")
      pageNum <- IntUtil.toInt(pageNumStr)
    } yield pageNum) match {
      case Some(pageNum) => pageNum
      case None => 0
    }

    val template = Templates.get("index");

    template.add("next_page", nextPage(pageNum).orNull);
    template.add("previous_page", previousPage(pageNum).orNull);

    val results = findPostList(pageNum);
    while(results.next()) {
      template.add("posts", Post.fromRow(results));
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
    if(currentPage * 10 + 10 > postCount)
      return None;
    else
      return Some(currentPage + 1);
  }

  def previousPage(currentPage: Int): Option[Int] = {
    if(currentPage > 0)
      return Some(currentPage - 1);
    else
      return None;
  }

  def findPostList(page:Int) : ResultSet = {
    findPostListSQL.setInt(1, page * 10);
    return findPostListSQL.executeQuery();
  }

  def getPostCount() : Int = {
    val row = postCountSQL.executeQuery();
    row.next();
    return row.getInt("count");
  }
}
