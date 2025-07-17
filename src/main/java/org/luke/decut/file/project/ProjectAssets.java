package org.luke.decut.file.project;

import org.json.JSONArray;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.LibraryContent;
import org.luke.decut.app.lib.assets.Assets;
import org.luke.decut.app.lib.assets.data.AssetData;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ProjectAssets extends ArrayList<File> implements ProjectPart {
    public JSONArray serialize(boolean zipped) {
        JSONArray arr = new JSONArray();
        for (File asset : this) {
            arr.put(zipped ? asset.getName() : asset.getAbsolutePath());
        }
        return arr;
    }

    public void deserialize(File projectFile, JSONArray arr, boolean zipped) {
        for(int i = 0; i < arr.length(); i++) {
            String assetPath = arr.getString(i);
            add(zipped ?
                    new File(projectFile.getParent(), assetPath) :
                    new File(assetPath));
        }
    }

    @Override
    public void save(Home owner) {
        for (AssetData asset : LibraryContent.getInstance(owner, Assets.class).getGrid().getData()) {
            if(owner.isAssetUsed(asset)) {
                add(asset.getFile());
            }
        }
    }

    public void load(Home owner, Runnable post) {
        HashMap<File, File> replace = new HashMap<>();
        forEach(file -> {
            if(!file.exists()) {
                int lastIndexFor = file.getAbsolutePath().lastIndexOf("/");
                int lastIndexBack = file.getAbsolutePath().lastIndexOf("\\");
                String name = file.getAbsolutePath().substring(Math.max(lastIndexBack, lastIndexFor) + 1);
                File rep = new File(owner.getOpenProject().getParent(), name);
                if(rep.exists()) {
                    replace.put(file, rep);
                }
            }
        });
        replace.forEach((key, value) -> {
            remove(key);
            add(value);
        });
        LibraryContent.getInstance(owner, Assets.class).getGrid().importFiles(this, post);
    }

    @Override
    public void load(Home owner) {
        load(owner, null);
    }
}
