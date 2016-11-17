# adium-to-avro
Produce an Avro-formatted corpus of chat messages from Adium logs

The resulting avro file is suitable for [another tool](https://github.com/jacopofar/markov-avro-tools) for generating Markov models.

How to use
========
Just run `sbt run`, there are no options. It will produce a TSV and an Avro file containing all of the messages in Adium logs, from each kind of protocol (IRC, XMPP, Telegram, etc.)

