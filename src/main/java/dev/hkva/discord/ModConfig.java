package dev.hkva.discord;

import java.util.ArrayList;

public class ModConfig {
    // Discord token
    public String token = "YOUR-TOKEN-HERE";

    // Command prefix
    public String commandPrefix = "mc!";

    // Relay channel IDs
    public ArrayList<Long> relayChannelIDs = new ArrayList<>();

    // Rcon user IDs
    public ArrayList<Long> rconUserIDs = new ArrayList<>();

    // Disable mentions from Minecraft chat
    public boolean disableMentions = true;
    
    // Discord message format
    public String discordMessageFormat = "[$USERNAME] $MESSAGE";
    
    // Game message format
    public String chatMessageFormat = "**<$NAME>** $MESSAGE";
    
    // System message format
    public String systemMessageFormat = "**$MESSAGE**";
}
