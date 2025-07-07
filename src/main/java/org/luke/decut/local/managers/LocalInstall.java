package org.luke.decut.local.managers;

import java.io.File;

public class LocalInstall {
    private final File binary;
    private final File root;
    private final String version;

    public LocalInstall(File root, File binary, String version) {
        this.root = root;
        this.version = version;
        this.binary = binary;
    }

    public File getRoot() {
        return root;
    }

    public String getVersion() {
        return version;
    }

    public File getBinary() {
        return binary;
    }
}
