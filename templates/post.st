post(post, comments) ::= <<
<html>
  <head>
    <title>$post.title$</title>
  </head>
  <body>
    $post:post_body()$
    <hr />
    <a name="comments" />
    <form action="post_comment?post_id=$post.id$" method="post">
      <p>
        Title: <input name="comment_title" type="text" />
      </p>
      <p>
        <textarea name="comment_body"></textarea>
      </p>
      <p>
        <input type="submit" />
      </p>
    </form>
    $comments:comment()$
  </body>
</html>
>>
