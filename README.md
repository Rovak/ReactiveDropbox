ReactiveDropbox
===============

## Polling

```scala
client.poll(5 minutes) {
  case EntryAdded(entry) => println(s"Added ${entry.path}")
  case EntryRemoved(entry) => println(s"Removed ${entry.path}")
}
```
