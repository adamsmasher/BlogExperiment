admin_entry(entry) ::= <<
<tr>
  <td>$entry.title$</td>
  <td>$entry.timestamp$</td>
  <td><a href="/admin/edit_post?id=$entry.id$">Edit</a></td>
  <td><a href="/admin/delete_post?id=$entry.id$">Delete</a></td>
  <td><a href="/admin/moderate_post?id=$entry.id$">Moderate</a></td>
</tr>
>>
