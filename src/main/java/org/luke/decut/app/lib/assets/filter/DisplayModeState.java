package org.luke.decut.app.lib.assets.filter;

public class DisplayModeState {
    private AssetDisplayMode mode;

    public DisplayModeState(AssetDisplayMode mode) {
        this.mode = mode;
    }

    public AssetDisplayMode getMode() {
        return mode;
    }

    public void setMode(AssetDisplayMode mode) {
        this.mode = mode;
    }
}
