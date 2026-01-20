package io.github.chikyukido.lovelystats.save;

import java.io.File;
import java.io.IOException;
import java.util.*;

public interface StatsStorage<T> {

    File getDataFolder();

    T load(UUID uuid) throws IOException;
    void store(T player) throws IOException;

    default List<T> loadAll() throws IOException {
        File[] files = getDataFolder().listFiles((dir, name) ->
                name.endsWith(".bin") && !name.endsWith(".bin.tmp")
        );

        if (files == null) return List.of();

        List<T> result = new ArrayList<>(files.length);

        for (File file : files) {
            String name = file.getName();
            String uuidPart = name.substring(0, name.length() - 4);

            try {
                UUID uuid = UUID.fromString(uuidPart);
                result.add(load(uuid));
            } catch (IllegalArgumentException ignored) {
            }
        }

        return result;
    }
}
