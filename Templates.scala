import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupDir;

object Templates {
  val templates = new STGroupDir("templates", '$', '$')
  def get(name: String) : ST = {
    return templates.getInstanceOf(name);
  }
}
