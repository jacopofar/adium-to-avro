import java.io.File

import javax.xml.parsers.SAXParserFactory

import org.joda.time.DateTime
import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler

import scala.collection.mutable
object AdiumIngester {

  var count = 0
  val userCount = mutable.HashMap[String, Integer]()
  val channelCount = mutable.HashMap[String, Integer]()

  def ingest(cb: (ConversationMessage) => Unit) = {
    if (System.getProperty("os.name") != "Mac OS X"){
      println("sorry, this code currently works only on Mac OS X")
      System.exit(1)
    }

    val originalPath = "/Users/" + System.getProperty("user.name")+ "/Library/Application Support/Adium 2.0/Users/Default/Logs"
    val logDir = new File(originalPath)
    for(sf <- logDir.listFiles()){
      scanAccountFolder(sf, 0, cb)
    }
  }


  def scanAccountFolder(f: File, indent: Integer, cb: (ConversationMessage) => Unit): Unit = {
    if (!f.isDirectory || f.getName() == ".DS_Store")
      return
    println(" " * indent + "examining log folder for account " + f.getName)
    for(cf <- f.listFiles()){
      scanChannelFolder(cf, indent + 2, cb)
    }
    println("total messages: " + count)
    userCount.foreach[Unit](t => {
      if(t._2 > 10)
      println(t._1 + " -> " + t._2)
    })

  }

  def scanChannelFolder(cf: File, indent: Integer, cb: (ConversationMessage) => Unit): Unit = {
    if (!cf.isDirectory || cf.getName() == ".DS_Store")
      return
    println(" " * indent + "channel " + cf.getName)
    for(slf <- cf.listFiles()){
      scanLogFolder(slf, indent + 2, cf.getName, cb)
    }
  }
  def scanLogFolder(slf: File, indent: Integer, channelName: String, cb: (ConversationMessage) => Unit): Unit = {
    if (!slf.isDirectory || slf.getName() == ".DS_Store")
      return
    println(" " * indent + " log folder " + slf.getName)
    for(oneLog <- slf.listFiles()){
      scanSingleLog(oneLog, indent + 2, channelName, cb)
    }
  }
  def scanSingleLog(slf: File, indent: Integer, channelName: String, cb: (ConversationMessage) => Unit): Unit = {
    if (!slf.isFile || !slf.getName.endsWith(".xml"))
      return
    println(" " * indent + " single log " + slf.getName)
    val saxParser = SAXParserFactory.newInstance().newSAXParser()
    var currentSender:String = null
    var currentTime:String = null
    var currentText:String = ""


    try {
      saxParser.parse(slf, new DefaultHandler() {

        override def startElement(uri: String, localName: String, qName: String, attributes: Attributes) = {
          if (qName == "message") {
            currentSender = attributes.getValue(attributes.getIndex("sender"))
            currentTime = attributes.getValue(attributes.getIndex("time"))
          }
        }

        override def endElement(uri: String, localName: String, qName: String) = {
          if (qName == "message") {
            userCount.put(currentSender, userCount.getOrElse[Integer](currentSender, 0) + 1)
            channelCount.put(channelName, channelCount.getOrElse[Integer](channelName, 0) + 1)

            cb(ConversationMessage(currentText.replace("\t", " ").replace("\n", " "),
              currentSender,
              channelName.replace("\t", " ").replace("\n", " "),
              new DateTime(currentTime)))

            currentSender = null
            currentText = ""
            count = count + 1
          }
        }

        override def characters(ch: Array[Char], start: Int, length: Int) = {
          if (currentSender != null) {
            currentText += String.valueOf(ch, start, length)
          }
        }
      })
    }
    catch{
      case e:Exception => println(e)
    }
  }
}
