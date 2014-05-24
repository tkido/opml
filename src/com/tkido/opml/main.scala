package com.tkido.opml

object main extends App {
  import com.tkido.tools.Logger
  import com.tkido.tools.Text
  import scala.xml._
  
  Logger.level = Logger.INFO
  
  val xml = XML.loadFile("data/export.xml")
  
  val groups = (xml \ "body" \ "outline" \ "outline")
  Logger.debug(groups.size)
  
  Text.write("data/god.txt", groups.map(convertGodAll(_)).mkString("\n"))
  Text.write("data/foma.txt", groups.map(convertFomaAll(_)).mkString("\n"))
  
  def convertGodAll(node:Node):String = {
    node.attribute("title").get.text match{
      case "‚Ü‚Æ‚ß" => ""
      case "‚»‚ê‚É‚Â‚¯‚Ä‚à‹à‚Ì‚Ù‚µ‚³‚æ" => ""
      case _ => convertGod(node)
    }
  }
  
  def convertFomaAll(node:Node):String = {
    node.attribute("title").get.text match{
      case "‚Ü‚Æ‚ß" => ""
      case "‚»‚ê‚É‚Â‚¯‚Ä‚à‹à‚Ì‚Ù‚µ‚³‚æ" => convertFoma(node)
      case _ => ""
    }
  }
  
  
  def convert(node:Node):String = {
    node.attribute("title").get.text match{
      case "‚Ü‚Æ‚ß" => ""
      case "‚»‚ê‚É‚Â‚¯‚Ä‚à‹à‚Ì‚Ù‚µ‚³‚æ" => convertFoma(node)
      case _ => convertGod(node)
    }
  }
  
  def convertFoma(node:Node):String = {
    def convert(node:Node):String = {
      """<div class="sidebody"><a href="%s" target="_blank">%s</a></div>""".format(
        node.attribute("htmlUrl").get.text,
        node.attribute("title").get.text)
    }
    (node \ "outline").map(convert(_)).mkString("\n")
  }
  def convertGod(node:Node):String = {
    def convert(node:Node):String = {
      """<li><a href="%s" target="_blank">%s</a></li>""".format(
        node.attribute("htmlUrl").get.text,
        node.attribute("title").get.text)
    }
    "<strong>%s</strong>\n".format(node.attribute("title").get.text) +
    (node \ "outline").map(convert(_)).mkString("<ul>\n", "\n", "\n</ul>")
  }
  
  Logger.close()
}