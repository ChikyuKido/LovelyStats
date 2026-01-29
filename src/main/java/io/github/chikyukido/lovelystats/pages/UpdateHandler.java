package io.github.chikyukido.lovelystats.pages;

import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;

public interface UpdateHandler {
    void sendUpdate(UICommandBuilder cb);
    void sendUpdate(UICommandBuilder cb, UIEventBuilder event, boolean forceUpdate);
}
