post(post) ::= <<
<html>
  <head>
    <title>$post.title$</title>
  </head>
  <body>
    $post:post_body()$
  </body>
</html>
>>
