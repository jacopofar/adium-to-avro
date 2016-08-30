import java.io.FileWriter

import org.joda.time.format.ISODateTimeFormat

/**
  * Created by utente on 2016-08-30.
  */
object IngestAll {
  val dtf = ISODateTimeFormat.dateTime();
  val fw = new FileWriter("all_messages.tsv")

  def main(args: Array[String]): Unit = {
    AdiumIngester.ingest((msg: ConversationMessage) => {
      fw.write(msg.channel + "\t" + dtf.print(msg.timestamp) + "\t" + msg.sender + "\t" + msg.body.replace("\t", " ").replace("\n", " ") + "\n")
    })
    fw.close()
  }
}

