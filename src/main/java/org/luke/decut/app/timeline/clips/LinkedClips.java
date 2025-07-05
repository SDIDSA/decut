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

    public void updateStartTime(TimelineClip source, double newStartTime) {
        if (updating) return;
        updating = true;

        try {
            double offset = newStartTime - source.getStartTime();

            for (TimelineClip clip : clips) {
                if (clip != source) {
                    clip.setStartTimeInternal(clip.getStartTime() + offset);
                }
            }
        } finally {
            updating = false;
        }
    }

    public void updateInPointAndStartTime(TimelineClip sourceClip, double newInPoint, double newStartTime) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                if (clip != sourceClip) {
                    clip.setInPointAndStartTimeInternal(newInPoint, newStartTime);
                }
            }
        } finally {
            updating = false;
        }
    }

    public void updateTimeshift(TimelineClip source, double newTimeshift) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                if (clip != source) {
                    clip.setTimeshiftInternal(newTimeshift);
                }
            }
        } finally {
            updating = false;
        }
    }

    public void updateInOutPoints(TimelineClip source, double deltaIn, double deltaOut) {
        if (updating) return;
        updating = true;

        try {
            for (TimelineClip clip : clips) {
                if (clip != source) {
                    clip.setInPointInternal(clip.getInPoint() - deltaIn);
                    clip.setOutPointInternal(clip.getOutPoint() - deltaOut);
                }
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