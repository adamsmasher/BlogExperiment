post(post, comments) ::= <<
<html>
  <head>
    <title>$post.title$</title>
  </head>
  <body>
    $post:post_body()$
    <hr />
    <a name="comments" />
    $comments:comment()$
  </body>
</html>
>>
