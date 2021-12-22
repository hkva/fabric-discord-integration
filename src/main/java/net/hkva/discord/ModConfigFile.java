package net.hkva.discord;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

//
// Represents a mod config file on disk
//
public class ModConfigFile {

    public static final ModConfig DEFAULT_CONFIG = new ModConfig();

    private static final Path FABRIC_CONFIG_DIR = FabricLoader.getInstance().getConfigDir();
    private static final Gson CONFIG_SERIALIZER = new GsonBuilder().setPrettyPrinting().create();

    private final File file;

    public ModConfigFile(String fileName) {
        file = new File(FABRIC_CONFIG_DIR.toFile(), fileName);
    }

    //
    // Returns true if the config file exists
    //
    public boolean exists() {
        return file.exists();
    }

    //
    // Read the config from disk. Returns None if not found
    //
    public ModConfig read() throws IOException {
        FileReader reader = new FileReader(file);
        return CONFIG_SERIALIZER.fromJson(reader, ModConfig.class);
    }

    //
    // Write the config to disk
    //
    public void write(ModConfig config) throws IOException {
        FileWriter writer = new FileWriter(file);
        writer.write(CONFIG_SERIALIZER.toJson(config));
        writer.close();
    }
}