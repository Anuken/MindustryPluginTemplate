### Setup

Clone this repository first.
To edit the plugin display name and other data, take a look at `src/main.resources/plugin.json`.
Edit the name of the project itself by going into `settings.gradle`.

### Basic Usage

See `src/main/java/example/ExamplePlugin.java` for some basic commands and event handlers.  
Every main plugin class must extend `Plugin`. Make sure that `plugin.json` points to the correct main plugin class.

Please note that the plugin system is in beta, and as such is subject to changes.

### Building a Jar

`gradlew jar` / `./gradlew jar`

Output jar should be in `build/libs`.


### Installing

Simply place the output jar from the step above in your server's `config/mods` directory and restart the server.
List your currently installed plugins/mods by running the `mods` command.
