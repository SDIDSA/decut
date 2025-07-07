package org.luke.decut.app.timeline.clips;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;

public class LinkedClips {
    private final ObservableList<TimelineClip> clips;
    private boolean updating = false; // Prevent recursive updates

    public LinkedClips() {
        this.clips = FXCollections.observableArrayList();
    }

    public void addClip(TimelineClip clip) {
        clips.add(clip);
        clip.setLinkedGroup(this);
    }

    public void removeClip(TimelineClip clip) {
        clips.remove(clip);
        clip.setLinkedGroup(null);
    }

    public ObservableList<TimelineClip> getClips() {
        return clips;
    }

    public void updateStartTime(double newStartTime) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                clip.setStartTimeInternal(newStartTime);
            }
        } finally {
            updating = false;
        }
    }

    public void updateInPointAndStartTime(double newInPoint, double newStartTime) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                clip.setInPointAndStartTimeInternal(newInPoint, newStartTime);
            }
        } finally {
            updating = false;
        }
    }

    public void updateTimeshift(double newTimeshift) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                clip.setTimeshiftInternal(newTimeshift);
            }
        } finally {
            updating = false;
        }
    }

    public void setInPoint(double intPoint) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                clip.setInPointInternal(intPoint);
            }
        } finally {
            updating = false;
        }
    }

    public void setOutPoint(double outPoint) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                clip.setOutPointInternal(outPoint);
            }
        } finally {
            updating = false;
        }
    }

    public List<TimelineClip> getOtherClips(TimelineClip exclude) {
        List<TimelineClip> others = new ArrayList<>(clips);
        others.remove(exclude);
        return others;
    }

    public boolean isEmpty() {
        return clips.isEmpty();
    }

    public int size() {
        return clips.size();
    }
}