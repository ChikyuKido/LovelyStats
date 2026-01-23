package io.github.chikyukido.lovelystats.pages.table;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.chikyukido.lovelystats.pages.TabPage;
import io.github.chikyukido.lovelystats.pages.UpdateHandler;
import io.github.chikyukido.lovelystats.util.Format;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TablePage extends TabPage {
    protected final TablePageConfig config;
    private String currentSort = "name";
    private boolean ascending = true;
    public TablePage(UpdateHandler parent, UUID playerUUID,TablePageConfig config) {
        super(parent, playerUUID);
        this.config = config;
    }

    @Override
    public void build(UICommandBuilder cb, UIEventBuilder event) {
        cb.append("#TabPages","common/table/page.ui");
        cb.insertBeforeInline("#BodyRow","Group #HeaderRow { LayoutMode: Left; Padding: (Top: 5, Bottom: 5, Left: "+config.getFullPaddingLeft()+"); }");
        for (int i = 0; i < config.getRows().size(); i++) {
            TablePageRow row = config.getRows().get(i);
            cb.appendInline("#HeaderRow", "TextButton #Header"+i+" { Text: \""+row.name()+"\"; Anchor: (Width: "+row.width()+"); }");
            event.addEventBinding(CustomUIEventBindingType.Activating,"#Header"+i, EventData.of("Button",config.getId()+"_"+i),false);
        }
        buildRows(cb);

    }

    @Override
    public void handleEvent(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store, @Nonnull String data) {
        sortAndRefreshGrid(data,new UICommandBuilder());
    }
    private void sortAndRefreshGrid(String sortBy,UICommandBuilder cb) {
        sortBy = sortBy.split("_")[1];
        int sortByRowIndex = Integer.parseInt(sortBy);
        TablePageRow row = config.getRows().get(sortByRowIndex);
        TablePageRowType type = row.type();

        Arrays.sort(config.getValues(), (a, b) -> {
            Object v1 = a[sortByRowIndex];
            Object v2 = b[sortByRowIndex];

            if (v1 == null && v2 == null) return 0;
            if (v1 == null) return -1;
            if (v2 == null) return 1;

            return switch (type) {
                case STRING -> ((String) v1).compareTo((String) v2);
                case LONG -> Long.compare(
                        ((Number) v1).longValue(),
                        ((Number) v2).longValue()
                );
                case DOUBLE -> Double.compare(
                        ((Number) v1).doubleValue(),
                        ((Number) v2).doubleValue()
                );
            };
        });


        if (currentSort.equals(sortBy) && ascending) {
            Collections.reverse(Arrays.asList(config.getValues()));
            ascending = false;
        } else {
            ascending = true;
        }
        currentSort = sortBy;
        buildRows(cb);
        parent.sendUpdate(cb);
    }

    private void buildRows(UICommandBuilder cb) {
        cb.clear("#BodyRow");
        Object[][] values = config.getValues();
        List<TablePageRow> rows = config.getRows();

        for (int i = 0; i < values.length; i++) {
            StringBuilder sb = new StringBuilder();
            Object[] valueRow = values[i];

            sb.append("Group #StatContainer { ");
            sb.append("LayoutMode: Left; ");
            sb.append("Padding: (Top: 5, Bottom: 5, Left: ").append(config.getPaddingLeft()).append("); ");
            if (config.isWithIcon()) {
                sb.append("AssetImage #Image { ");
                sb.append("Anchor: (Width: ").append(config.getIconSize()).append(", Height: ").append(config.getIconSize()).append("); ");
                sb.append("Padding: (Right: ").append(config.getPaddingLeft()).append("); ");
                sb.append("AssetPath: \"").append(valueRow[0]).append("\"; ");
                sb.append("} ");
            }

            for (int j = 0; j < rows.size(); j++) {
                TablePageRow row = rows.get(j);
                Object value = valueRow[j+(config.isWithIcon()?1:0)];

                String text = "";
                switch (row.visualizeType()) {
                    case STRING -> {
                        if (value == null) {
                            text = "";
                        } else {
                            switch (row.type()) {
                                case STRING, LONG -> text = value.toString();
                                case DOUBLE -> text = String.format("%.2f", (Double) value);
                            }
                        }
                    }
                    case DATE -> text = value != null ? Format.formatDate((long) value) : "-";
                    case TIME -> text = value != null ? Format.formatTime((long) value)  : "-";
                    case DISTANCE -> text = value != null ? Format.formatDistance((double) value)  : "-";
                    default -> text = "-";
                }

                sb.append("Label")
                        .append(" { Text: \"")
                        .append(text)
                        .append("\"; Anchor: (Width: ")
                        .append(row.width())
                        .append("); } ");
            }

            sb.append("}");
            cb.appendInline("#BodyRow",sb.toString());
        }
    }
}
