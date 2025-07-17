package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.home.Home;

import java.io.File;

public class DecutProject implements ProjectPart {
    public static final String ASSETS = "assets";
    public static final String TIMELINE = "timeline";
    public static final String PROPERTIES = "properties";

    private final ProjectAssets assets;
    private final TimelineData timeline;
    private final ProjectProperties properties;

    public DecutProject() {
        assets = new ProjectAssets();
        timeline = new TimelineData();
        properties = new ProjectProperties();
    }

    public ProjectAssets getAssets() {
        return assets;
    }

    public TimelineData getTimeline() {
        return timeline;
    }

    public ProjectProperties getProperties() {
        return properties;
    }

    @Override
    public void save(Home owner) {
        assets.save(owner);
        timeline.save(owner);
        properties.save(owner);
    }

    @Override
    public void load(Home owner) {
        assets.load(owner, () -> {
            timeline.load(owner);
            properties.load(owner);
        });
    }

    public JSONObject serialize(boolean zipped) {
        return new JSONObject()
                .put(ASSETS, assets.serialize(zipped))
                .put(TIMELINE, timeline.serialize())
                .put(PROPERTIES, properties.serialize());
    }

    public void deserialize(File projectFile, JSONObject object, boolean zipped) {
        if (object.has(ASSETS))
            assets.deserialize(projectFile, object.getJSONArray(ASSETS), zipped);
        if (object.has(TIMELINE))
            timeline.deserialize(object.getJSONObject(TIMELINE));
        if (object.has(PROPERTIES))
            properties.deserialize(object.getJSONObject(PROPERTIES));
    }
}
