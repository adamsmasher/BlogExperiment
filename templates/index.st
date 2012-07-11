index(posts, next_page) ::= <<
<html>
  <head>
    <title>blog</title>
  </head>
  <body>
    $posts:post()$
    <p>
      <a href="index?page=$next_page$">Next</a>
    </p>
  </body>
</html>
>>
