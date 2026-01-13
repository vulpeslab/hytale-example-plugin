# Hytale Example Plugin

An example plugin for Hytale servers demonstrating basic plugin functionality.

## Features

- `/example info` - Display plugin information
- `/example tools` - Give the player a set of crude tools (once per player)
- Door interaction event - Receive a door item when opening a door for the first time

## Building

```bash
./gradlew build
```

The compiled plugin JAR will be in `build/libs/`.

## Installation

1. Copy the JAR file to your server's `mods/` directory
2. Restart the server

## Requirements

- Hytale Server with plugin support
- Java 21+
- `HytaleServer.jar` in the project root for compilation

## Documentation

For a comprehensive guide on how to create Hytale plugins, see the official documentation:

👉 [Hytale Plugin Development Guide](https://hytale-docs.pages.dev/getting-started/introduction/)

## License

MIT
