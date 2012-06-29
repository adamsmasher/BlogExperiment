object BlogApp extends FCGIHandler {
  def main(args: Array[String]) = {
    init();
    for(req <- get_request()) {
      val response = req dispatch Map(
	"/(index)?" -> index
      )
      System.out.println(response);
    }
  }

  def index(req: FCGIRequest): HTTPResponse = {
    val name = req.fields.getOrElse("name", "World");

    return new HTTPResponse(
      HTMLMIME,
      Array(),
      "<HTML>Hello "+name+"!</HTML>");
  }

  def init() = {}
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
