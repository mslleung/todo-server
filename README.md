
## todo-server

This is a submission for the Sleekflow interview challenge for the position of Full Stack Software Engineer.

# Major technology/libraries used
- Ktor
- Websocket
- Protobuf
- Koin
- Exposed
- junit
- h2

I chose WebSocket because we might need real-time collaboration as one of the features. Unfortunately I did not have time to actually implement the shared todo list.
An in-memory H2 database is used for the purpose of this interview due to ease of setup. It can be swapped via dependency injection to use other databases supported by Exposed.

# Setup
Ubuntu:
1. Install Java.
2. Set JAVA_HOME to point to your jdk.
3. Run ```.gradlew run``` from the project root directory.

Or you can install IntelliJ Idea and run directly from the IDE.
