package org.luke.decut.crossplatform;

import java.io.File;

public enum Os {
    WINDOWS_64("windows-64", new String[]{"cmd", "/c"}),
    WINDOWS_32("windows-32", new String[]{"cmd", "/c"}),
    LINUX_64("linux-64", new String[]{"sh", "-c"}),
    LINUX_32("linux-32", new String[]{"sh", "-c"}),
    LINUX_ARM64("linux-arm64", new String[]{"sh", "-c"}),
    LINUX_ARMHF("linux-armhf", new String[]{"sh", "-c"}),
    LINUX_ARMEL("linux-armel", new String[]{"sh", "-c"}),
    OSX_64("osx-64", new String[]{"sh", "-c"}),
    UNKNOWN("unknown", new String[]{"sh", "-c"});

    private final String name;
    private final String[] commandPrefix;

    Os(String name, String[] commandPrefix) {
        this.name = name;
        this.commandPrefix = commandPrefix;
    }

    public String getName() {
        return name;
    }

    public String[] getCommandPrefix() {
        return commandPrefix;
    }

    public String[] addCommandPrefix(String[] command) {
        String[] res = new String[command.length + commandPrefix.length];

        System.arraycopy(commandPrefix, 0, res, 0, commandPrefix.length);

        System.arraycopy(command, 0, res, commandPrefix.length, command.length);

        return res;
    }

    public static Os fromSystem() {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        boolean isWindows = osName.contains("win");
        boolean isLinux = osName.contains("linux");
        boolean isMac = osName.contains("mac") || osName.contains("darwin");

        boolean is64Bit = osArch.contains("64") || osArch.equals("amd64") || osArch.equals("x86_64");
        boolean isArm = osArch.contains("arm") || osArch.contains("aarch");
        boolean isArmHf = osArch.contains("armhf");
        boolean isArm64 = osArch.contains("aarch64") || osArch.contains("arm64");

        if (isWindows) {
            return is64Bit ? WINDOWS_64 : WINDOWS_32;
        } else if (isLinux) {
            if (isArm64) {
                return LINUX_ARM64;
            } else if (isArmHf) {
                return LINUX_ARMHF;
            } else if (isArm) {
                return LINUX_ARMEL;
            } else {
                return is64Bit ? LINUX_64 : LINUX_32;
            }
        } else if (isMac) {
            return is64Bit && !isArm64 ? OSX_64 : UNKNOWN;
        }

        return UNKNOWN;
    }

    public boolean isWindows() {
        return this == WINDOWS_64 || this == WINDOWS_32;
    }

    public boolean isLinux() {
        return this == LINUX_64 || this == LINUX_32 ||
                this == LINUX_ARM64 || this == LINUX_ARMHF || this == LINUX_ARMEL;
    }

    public boolean isOsx() {
        return this == OSX_64;
    }

    public File getDecutRoot() {
        if(isWindows()) {
            return new File(System.getenv("appData") + "\\decut");
        } else if(isLinux()) {
            return new File(System.getProperty("user.home") + "/.config/decut");
        } else if(isOsx()) {
            return new File(System.getProperty("user.home") + "/Library/Application Support/decut");
        } else {
            return new File(System.getProperty("user.home") + "/.decut");
        }
    }

    public File getDecutPath(String dirname) {
        return new File(getDecutRoot(), dirname);
    }
}
