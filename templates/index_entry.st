index_entry(entry) ::= <<
    $entry.post:post_body()$
    <p>
        (<a href="/post?id=$entry.post.id$#comments">$entry.commentCount$ comments</a>)
    </p>
>>
