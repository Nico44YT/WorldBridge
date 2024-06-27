package net.letscode.worldbridge.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileHandler {
    public static final File BASE_FOLDER_PATH = new File(FabricLoader.getInstance().getGameDir().toFile(), "worldbridge");

    public static void createBaseDirectory() {
        if(BASE_FOLDER_PATH.exists()) return;
        BASE_FOLDER_PATH.mkdir();
    }

    public static File getOrCreateWorldFolder(UUID levelUUID) {
        File worldDir = new File(BASE_FOLDER_PATH, levelUUID.toString());

        if(worldDir.exists()) return worldDir;
        worldDir.mkdir();
        return worldDir;
    }

    public static void writeFile(EntityDataHolder dataHolder) {
        File worldDir = getOrCreateWorldFolder(dataHolder.levelUUID);

        try {
            File entityDataFile = new File(worldDir, dataHolder.entityUUID.toString());

            Gson gsonBuilder = new GsonBuilder().setPrettyPrinting().create();
            String jsonData = gsonBuilder.toJson(dataHolder);

            FileWriter fileWriter = new FileWriter(entityDataFile);
            fileWriter.write(jsonData);
            fileWriter.close();

        } catch (Exception ignore) {};
    }

    public static EntityDataHolder readFile(File file) {
        EntityDataHolder dataHolder = null;

        try {
            if(!file.exists()) throw new Exception();

            Scanner fileScanner = new Scanner(file);
            String jsonData = "";

            while(fileScanner.hasNextLine()) {
                jsonData += fileScanner.nextLine();
            }
            fileScanner.close();

            return new Gson().fromJson(jsonData, EntityDataHolder.class);

        } catch (Exception ignore) {};

        return dataHolder;
    }

    public static EntityDataHolder readFile(UUID levelUUID, UUID entityUUID) {
        return readFile(new File(getOrCreateWorldFolder(levelUUID), entityUUID.toString()+".json"));
    }

    public static List<EntityDataHolder> getWorldFiles(UUID levelUUID) {
        List<EntityDataHolder> dataHolderList = new ArrayList<>();
        File worldDir = getOrCreateWorldFolder(levelUUID);

        if(worldDir.listFiles() == null) return new ArrayList<EntityDataHolder>();

        Arrays.stream(worldDir.listFiles()).forEach(file -> {
            dataHolderList.add(readFile(file));
        });

        return dataHolderList;
    }

    public static void clearFiles() {
        try{
            Arrays.stream(BASE_FOLDER_PATH.listFiles()).forEach(folder -> {
                if(folder.listFiles() == null) return;
                Arrays.stream(folder.listFiles()).forEach(file -> {
                    if(readFile(file).shouldDelete()) {
                        try {
                            Files.delete(Path.of(file.getAbsolutePath()));
                        } catch (IOException ignore) {}
                    }
                });
            });
        } catch (Exception ignore) {};
    }
}
