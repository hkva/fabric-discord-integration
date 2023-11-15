# Fabric Discord Integration [![build](https://github.com/chunkaligned/fabric-discord-integration/actions/workflows/build.yml/badge.svg)](https://github.com/chunkaligned/fabric-discord-integration/actions/workflows/build.yml)

https://github.com/chunkaligned/fabric-discord-integration/assets/91440203/489ff3c2-df7d-4531-826b-7aa48ae57ef8

[This mod is also available on Modrinth](https://modrinth.com/mod/fabric-discord-integration)

## Notable features:
* Scoreboard integration
* Attachment support
* Remote server console access for admins
* Emoji support, including custom emoji

## Building

Install a Java 17 JDK and run `./gradlew build`

## Setting up a Discord bot
1. Create a [Discord Bot Application](https://discord.com/developers/docs/getting-started) at the [Discord Developer Portal](https://discord.com/developers/applications)
2. Enable the "server members" and "message content" gateway intents ([screenshot](https://github.com/chunkaligned/fabric-discord-integration/assets/91440203/8435a6c9-dc9c-4f62-a93d-6b8c7ce9982a))


## Installing

[Download the latest version here](https://github.com/chunkaligned/fabric-discord-integration/releases)

1. Install the on the dedicated server: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files), [Modrinth](https://modrinth.com/mod/fabric-api)
2. Install `fabric-discord-integration-x.x.x.jar`
3. Launch the server
4. Configure `config/discord.json`
5. Run `/discord loadConfig`, then `/discord reconnect`

## Discord Commands
* `mc!players`
* `mc!rcon`
* `mc!scoreboard`

## Server Commands
* `/discord loadConfig`
* `/discord reconnect`
* `/discord status`

## Contributing

All contributions are welcome. If you want to suggest a feature, [create an issue](https://github.com/chunkaligned/fabric-discord-integration/issues/new/choose) and it'll probably get added.

## License

[MIT](/LICENSE)
