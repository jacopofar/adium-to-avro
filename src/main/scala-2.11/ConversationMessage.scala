import org.joda.time.DateTime

case class ConversationMessage(body: String, sender: String, channel: String, timestamp: DateTime) {}
