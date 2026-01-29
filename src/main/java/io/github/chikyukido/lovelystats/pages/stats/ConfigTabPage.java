package io.github.chikyukido.lovelystats.pages.stats;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.Main;
import io.github.chikyukido.lovelystats.config.PlayerConfig;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;

import javax.annotation.Nonnull;
import java.util.List;
public class ConfigTabPage extends TabPage {
    private final PlayerConfig.PlayerConfigData playerConfig;
    public ConfigTabPage(UpdateHandler parent, PlayerRef playerRef) {
        super(parent, playerRef);
        playerConfig = Main.PLAYER_CONFIG.get().getPlayerConfig(playerRef.getUuid());
    }

    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages", "stats/config/config_page.ui");
        List<PlayerConfig.PlayerConfigData.PlayerConfigDataOverviewEntry> entries = playerConfig.getOverview();
        for(int i = 0; i < entries.size(); i++) {
            addOverviewConfigRow(cb,event,entries.get(i),i);
        }
    }

    private void addOverviewConfigRow(UICommandBuilder cb, UIEventBuilder event,PlayerConfig.PlayerConfigData.PlayerConfigDataOverviewEntry entry,int index) {
        cb.append("#OverviewConfigGroup","stats/config/overview_config_row.ui");
        cb.set("#OverviewConfigGroup["+index+"] #OverviewConfigRowLabel.Text",entry.getName());
        cb.set("#OverviewConfigGroup["+index+"] #OverviewConfigRowToggle.Text",entry.isActive() ? "YES" : "NO");


        event.addEventBinding(CustomUIEventBindingType.Activating,"#OverviewConfigGroup["+index+"] #OverviewConfigRowUp", EventData.of("Button","up_"+index),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#OverviewConfigGroup["+index+"] #OverviewConfigRowDown", EventData.of("Button","down_"+index),false);
        event.addEventBinding(CustomUIEventBindingType.Activating,"#OverviewConfigGroup["+index+"] #OverviewConfigRowToggle", EventData.of("Button","toggle_"+index),false);
    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {
        var split = data.split("_");
        if(split.length != 2) return;
        var button = split[0];
        var index = Integer.parseInt(split[1]);
        if(button.equals("up")) {
            playerConfig.moveUpOverviewEntry(index);
            rebuildOverviewConfig();
        }else if(button.equals("down")) {
            playerConfig.moveDownOverviewEntry(index);
            rebuildOverviewConfig();
        }else if(button.equals("toggle")) {
            playerConfig.getOverview().get(index).setActive(!playerConfig.getOverview().get(index).isActive());
            rebuildOverviewConfig();
        }
        Main.PLAYER_CONFIG.save();
    }
    private void rebuildOverviewConfig() {
        UIEventBuilder event = new UIEventBuilder();
        UICommandBuilder cb = new UICommandBuilder();
        cb.clear("#OverviewConfigGroup");
        List<PlayerConfig.PlayerConfigData.PlayerConfigDataOverviewEntry> entries = playerConfig.getOverview();
        for(int i = 0; i < entries.size(); i++) {
            addOverviewConfigRow(cb,event,entries.get(i),i);
        }
        parent.sendUpdate(cb,event,false);
    }
}
