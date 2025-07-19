package org.luke.decut.crossplatform;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;

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

    private static Os fromSystem = null;

    private final String name;
    private final String[] commandPrefix;
    private File decutRoot;
    private File decutCache;
    private File decutTemp;

    private static final String TEMP_FILE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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

    private static Os calcFromSystem() {
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

    public static Os fromSystem() {
        if(fromSystem == null) fromSystem = calcFromSystem();
        return fromSystem;
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

    private File calcDecutRoot() {
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

    private File calcDecutCache() {
        return getDecutPath("cache");
    }

    private File calcDecutTemp() {
        return getDecutPath("temp");
    }

    public File getDecutRoot() {
        if(decutRoot == null) decutRoot = calcDecutRoot();
        return decutRoot;
    }

    public File getDecutCache() {
        if(decutCache == null) decutCache = calcDecutCache();
        return decutCache;
    }

    public File getDecutTemp() {
        if(decutTemp == null) decutTemp = calcDecutTemp();
        return decutTemp;
    }

    public File createTempFile(String prefix, String suffix) throws IOException {
        return createTempFile(prefix, suffix, true);
    }

    public File createTempFile(String prefix, String suffix, boolean createFile) throws IOException {
        File tempDir = getDecutTemp();
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
            }
        }

        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }

        File tempFile;
        int attempts = 0;
        do {
            String randomPart = generateRandomString(8);
            String fileName = prefix + randomPart + suffix;
            tempFile = new File(tempDir, fileName);
            attempts++;

            if (attempts > 1000) {
                throw new IOException("Could not generate unique temp file name after 1000 attempts");
            }
        } while (tempFile.exists());

        if (createFile) {
            try {
                if (!tempFile.createNewFile()) {
                    throw new IOException("Could not create temp file: " + tempFile.getAbsolutePath());
                }
            } catch (IOException e) {
                throw new IOException("Could not create temp file: " + tempFile.getAbsolutePath(), e);
            }
        }

        return tempFile;
    }

    public File createTempDirectory(String prefix) throws IOException {
        File tempDir = getDecutTemp();
        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                throw new IOException("Could not create temp directory: " + tempDir.getAbsolutePath());
            }
        }

        if (prefix == null) {
            prefix = "";
        }

        File tempDirectory;
        int attempts = 0;
        do {
            String randomPart = generateRandomString(8);
            String dirName = prefix + randomPart;
            tempDirectory = new File(tempDir, dirName);
            attempts++;

            if (attempts > 1000) {
                throw new IOException("Could not generate unique temp directory name after 1000 attempts");
            }
        } while (tempDirectory.exists());

        if (!tempDirectory.mkdirs()) {
            throw new IOException("Could not create temp directory: " + tempDirectory.getAbsolutePath());
        }

        return tempDirectory;
    }

    public File[] listTempFiles() {
        File tempDir = getDecutTemp();
        if (!tempDir.exists()) {
            return new File[0];
        }

        File[] files = tempDir.listFiles();
        return files != null ? files : new File[0];
    }

    public int cleanTempFiles() {
        return cleanTempFiles(false);
    }

    public int cleanTempFiles(boolean includeDirectories) {
        File tempDir = getDecutTemp();
        if (!tempDir.exists()) {
            return 0;
        }

        File[] files = tempDir.listFiles();
        if (files == null) {
            return 0;
        }

        int deletedCount = 0;
        for (File file : files) {
            if (file.isFile()) {
                if (file.delete()) {
                    deletedCount++;
                } else {
                    System.out.println("failed to delete " + file);
                }
            } else if (file.isDirectory() && includeDirectories) {
                if (deleteDirectory(file)) {
                    deletedCount++;
                } else {
                    System.out.println("failed to delete " + file);
                }
            }
        }

        return deletedCount;
    }

    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = SECURE_RANDOM.nextInt(TEMP_FILE_CHARS.length());
            sb.append(TEMP_FILE_CHARS.charAt(index));
        }
        return sb.toString();
    }

    private boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectory(file);
                    } else {
                        file.delete();
                    }
                }
            }
        }
        return directory.delete();
    }

    public File getDecutPath(String dirname) {
        return new File(getDecutRoot(), dirname);
    }
}
