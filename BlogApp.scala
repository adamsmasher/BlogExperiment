import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.util.PSQLState;
import org.stringtemplate.v4._;

object BlogApp extends FCGIHandler {
  val log = System.err;
  val db = DB.connect();
  val findPostSQL = db.prepareStatement(
    "SELECT * FROM post WHERE id = ?");
  val findPostListSQL = db.prepareStatement(
    "SELECT * FROM post ORDER BY timestamp DESC LIMIT 10 OFFSET ?"
  );
  val postCountSQL = db.prepareStatement(
    "SELECT count(*) count FROM post");
  def main(args: Array[String]) = {
    for(req <- get_request()) {
      val response = req dispatch Map(
        "/(index)?" -> index,
        "/post" -> post
      )
      System.out.println(response);
    }
  }

  def index(req: FCGIRequest): HTTPResponse = {
    val pageNum = (for {
      pageNumStr <- req.fields.get("page")
      pageNum <- IntUtil.toInt(pageNumStr)
    } yield pageNum) match {
      case Some(pageNum) => pageNum
      case None => 0
    }

    val template = new STGroupDir("templates", '$', '$').getInstanceOf("index");

    template.add("next_page", nextPage(pageNum).orNull);

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

  def post(req: FCGIRequest): HTTPResponse = {
    val post = for {
      postIdStr <- req.fields.get("id")
      postId <- IntUtil.toInt(postIdStr)
      post <- findPost(postId)
    } yield post;
    return post match {
      case Some(post) => postResponse(post)
      case None       => postNotFound()
    };
  }

  def findPost(id:Int) : Option[Post] = {
    findPostSQL.setInt(1, id);
    try {
      val row = findPostSQL.executeQuery();
      row.next();
      return Some(Post.fromRow(row));
    } catch {
      case e:SQLException => {
	// The cursor will be in an invalid state after next()
	// if the result set is empty
	if(e.getSQLState() equals
	   PSQLState.INVALID_CURSOR_STATE.getState())
	{
	  return None;
	}
	else {
	  log.println(e.getSQLState());
	  log.println(e);
	  throw e;
	}
      }
    }
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

  def postNotFound() : HTTPResponse = {
    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Not found.</HTML>");
  }

  def postResponse(post:Post) : HTTPResponse = {
    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML><H1>"+post.title+"</H1><H2>"+post.timestamp+"</H2>" +
	post.contents+"</HTML>");
  }
}
