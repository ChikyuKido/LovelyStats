package io.github.chikyukido.lovelystats.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.*;

public class PlayerConfig {
    private static final Gson GSON = new GsonBuilder()
            .create();

    public static final BuilderCodec<PlayerConfig> CODEC = BuilderCodec.builder(PlayerConfig.class, PlayerConfig::new)
            .append(new KeyedCodec<String>("Players", Codec.STRING),
                    (config, v) -> config.players = fromJson(v).players,
                    playerConfig -> toJson(playerConfig)).add()
            .build();
    private Map<String,PlayerConfigData> players;
    public PlayerConfig() {
        players = new HashMap<>();
    }

    public PlayerConfigData getPlayerConfig(UUID uuid) {
        if(!players.containsKey(uuid.toString())) {
            PlayerConfigData playerConfigData = new PlayerConfigData();
            playerConfigData.overview = new ArrayList<>(List.of(
                    new PlayerConfigData.PlayerConfigDataOverviewEntry("Show Entity",true),
                    new PlayerConfigData.PlayerConfigDataOverviewEntry("Show Player",true),
                    new PlayerConfigData.PlayerConfigDataOverviewEntry("Show Playtime",true),
                    new PlayerConfigData.PlayerConfigDataOverviewEntry("Show Items",true),
                    new PlayerConfigData.PlayerConfigDataOverviewEntry("Show Activity",true)
            ));
            players.put(uuid.toString(),playerConfigData);
        }
        return players.get(uuid.toString());

    }

    public static String toJson(PlayerConfig config) {
        return GSON.toJson(config);
    }
    public static PlayerConfig fromJson(String json) {
        return GSON.fromJson(json, PlayerConfig.class);
    }


    public static class PlayerConfigData {
        private List<PlayerConfigDataOverviewEntry> overview;

        public List<PlayerConfigDataOverviewEntry> getOverview() {
            return overview;
        }
        public void moveUpOverviewEntry(int index) {
            if(index == 0) return;
            Collections.swap(overview,index-1,index);
        }
        public void moveDownOverviewEntry(int index) {
            if(index == overview.size()-1) return;
            Collections.swap(overview,index,index+1);
        }

        public static class PlayerConfigDataOverviewEntry {
            private String name;
            private boolean active;

            public PlayerConfigDataOverviewEntry() {}

            public PlayerConfigDataOverviewEntry(String name, boolean active) {
                this.name = name;
                this.active = active;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public boolean isActive() {
                return active;
            }

            public void setActive(boolean active) {
                this.active = active;
            }
        }
    }
}
