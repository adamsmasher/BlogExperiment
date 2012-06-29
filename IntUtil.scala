object IntUtil {
  def toInt(s:String) : Option[Int] = {
    try {
      return Some(s toInt);
    } catch {
      case _:NumberFormatException => return None;
    }
  }
}
