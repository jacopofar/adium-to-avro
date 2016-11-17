import org.joda.time.DateTime
/**
* A message in a conversation. Field names are pretty much what the names suggest
  * */
case class ConversationMessage(body: String, sender: String, channel: String, timestamp: DateTime) {}
