package io.github.chikyukido.lovelystats.types;

public class SaveStat {
    private volatile boolean dirty;

    public void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearDirty() {
        dirty = false;
    }
}
