package io.github.chikyukido.lovelystats.pages.table;

import java.util.ArrayList;
import java.util.List;

public class TablePageConfig {
    private final String id;
    private int paddingLeft;
    private boolean withIcon;
    private int iconSize = 30;
    private final List<TablePageRow> rows = new ArrayList<>();
    private Object[][] values;

    public TablePageConfig(String id,int paddingLeft, boolean withIcon) {
        this.id = id;
        this.paddingLeft = paddingLeft;
        this.withIcon = withIcon;
    }

    public String getId() {
        return id;
    }

    public int getFullPaddingLeft() {
        return paddingLeft + (withIcon ? iconSize : 0);
    }
    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public boolean isWithIcon() {
        return withIcon;
    }

    public void setWithIcon(boolean withIcon) {
        this.withIcon = withIcon;
    }
    public List<TablePageRow> getRows() {
        return rows;
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
    }

    public Object[][] getValues() {
        return values;
    }
    public void setValues(Object[][] values) {
        this.values = values;
    }
}
