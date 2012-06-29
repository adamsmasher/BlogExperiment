import java.sql.ResultSet;
import java.sql.SQLException;
import org.postgresql.util.PSQLState;

object BlogApp extends FCGIHandler {
  val log = System.err;
  val db = DB.connect();
  val findPostSQL = db.prepareStatement(
    "SELECT * FROM post WHERE id = ?");
  val findPostListSQL = db.prepareStatement(
    "SELECT * FROM post ORDER BY timestamp DESC LIMIT 10 OFFSET ?"
  );
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

    val results = findPostList(pageNum);
    val builder = new StringBuilder();
    while(results.next()) {
      builder.append("<LI>"+results.getString("title")+"</LI>");
    }

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Hello World!<UL>"+builder+"</UL></HTML>");
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

/*

structure:

fastcgi, dispatch based on URL

four pages

$BLOG_BASE/index
$BLOG_BASE/post?id=<id>
$BLOG_BASE/admin/index
$BLOG_BASE/admin/edit_post?id=<id>

other stuff

$BLOG_BASE/post_comment?id=<post_id>
--POST body contains comment

$BLOG_BASE/admin/new_post
--HTTP response body provides new post ID

$BLOG_BASE/admin/update_post?id=<post_id>
--POST body contains new title, new text (JSON?)

we'll store everything in a DB for now - why not?

text is markdown

StringTemplate based templates

get request -> parse -> dispatch

blog post =
  id
  version
  first_post_time

blog post version =
  id
  post id
  title
  text
  post_time

comment =
  id
  post_id (corresponds to blog_post.id)
  poster
  post_time
  title
  text

poster = 
  id
  ip
  name
  
Get posts:

SELECT blog_post_version.{title, text, post_time},
       blog_post.first_post_time
       COUNT(comment)
FROM blog_post, blog_post_version, comment
WHERE blog_post.version = blog_post_version.id
      comment.post_id = blog_post.id
SORT BY blog_post.first_post_time


*/
