package com.mojaye.basooofthevoid.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class SaveManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve("basoo_config.json").toFile();

    // The data to save
    public boolean enabled = false;
    public double minDelay = 0.13;
    public double maxDelay = 0.19;

    /**
     * Saves the current configuration to the .json file.
     */
    public void save() {
        try (Writer writer = new FileWriter(FILE)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("[BOTV] Failed to save config!");
            e.printStackTrace();
        }
    }

    /**
     * Loads the configuration from the .json file.
     * If the file doesn't exist, it returns a new config with default values.
     */
    public static SaveManager load() {
        if (!FILE.exists()) {
            SaveManager defaults = new SaveManager();
            defaults.save(); // Create the file immediately so it's visible
            return defaults;
        }

        try (Reader reader = new FileReader(FILE)) {
            SaveManager loaded = GSON.fromJson(reader, SaveManager.class);
            return loaded != null ? loaded : new SaveManager();
        } catch (Exception e) {
            System.err.println("[BOTV] Failed to load config, using defaults.");
            return new SaveManager();
        }
    }
}