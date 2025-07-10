package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.home.Home;

public class ProjectProperties implements ProjectPart {
    public static final String DURATION = "duration";

    private double duration;

    public double getDuration() {
        return duration;
    }

    public JSONObject serialize() {
        return new JSONObject().put(DURATION, duration);
    }

    public void deserialize(JSONObject object) {
        duration = object.getDouble(DURATION);
    }

    @Override
    public void save(Home owner) {
        duration = owner.durationProperty().get();
    }

    @Override
    public void load(Home owner) {
        owner.durationProperty().set(duration);
    }
}
