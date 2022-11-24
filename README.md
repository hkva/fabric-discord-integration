# Fabric Discord Integration ![GitHub Workflow Status](https://img.shields.io/github/workflow/status/chunkaligned/fabric-discord-integration/build?style=flat-square)

![image](https://user-images.githubusercontent.com/91440203/172745735-47e3396e-e84c-42c5-a29e-d24a0d8db3e6.png)

## Notable features:
* Scoreboard integration
* Attachment support
* Remote server console access for admins
* Emoji support, including custom emoji

## Building

`./gradlew build`

## Setting up a Discord bot
1. Create a [Discord Bot Application](https://discord.com/developers/docs/getting-started) at the [Discord Developer Portal](https://discord.com/developers/applications)
2. Enable the "message content" intent ([screenshot](https://user-images.githubusercontent.com/91440203/203864167-519d7fd3-25b8-4490-b633-253a287f360e.png))


## Installing

[Download the latest version here](https://github.com/chunkaligned/fabric-discord-integration/releases)

1. Install the [Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api/files) on the dedicated server
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
