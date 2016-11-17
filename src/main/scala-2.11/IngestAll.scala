import java.io.{File, FileWriter}

import org.apache.avro.Schema
import org.apache.avro.file.DataFileWriter

import org.apache.avro.generic.{GenericData, GenericDatumWriter, GenericRecord}
import org.joda.time.format.ISODateTimeFormat

import scala.collection.JavaConversions.asJavaCollection

object IngestAll {
  val dtf = ISODateTimeFormat.dateTime()
  val fw = new FileWriter("adium_chat_messages.tsv")

  def main(args: Array[String]): Unit = {
    val schema = new Schema.Parser().parse(new File("corpus_utterance.avsc"))
    val file = new File("adium_chat_messages.avro")
    val datumWriter = new GenericDatumWriter[GenericRecord](schema)
    val dataFileWriter = new DataFileWriter[GenericRecord](datumWriter)
    dataFileWriter.create(schema, file)

    AdiumIngester.ingest((msg: ConversationMessage) => {
      val utterance = new GenericData.Record(schema)
      val text = msg.body.replace("\t", " ").replace("\n", " ")
      val ts = dtf.print(msg.timestamp)
      utterance.put("text", text)
      utterance.put("timestamp", ts)
      utterance.put("source", msg.channel)
      //Avro serialization of arrays is a bit tricky
      val tagList = asJavaCollection(List[String](msg.sender, msg.channel))
      utterance.put("tags",   tagList)
      dataFileWriter.append(utterance)
      fw.write(msg.channel + "\t" + ts + "\t" + msg.sender + "\t" + text + "\n")
    })

    dataFileWriter.close()
    fw.close()
  }
}

