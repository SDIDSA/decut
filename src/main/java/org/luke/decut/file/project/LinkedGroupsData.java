package org.luke.decut.file.project;

import org.json.JSONObject;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.timeline.clips.LinkedClips;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.tracks.Track;

import java.util.ArrayList;
import java.util.List;

public class LinkedGroupsData extends ArrayList<LinkedGroupData> implements ProjectPart {

    public JSONObject serialize() {
        JSONObject arr = new JSONObject();
        for (LinkedGroupData group : this) {
            arr.put(String.valueOf(group.getGroupId()), group.serialize());
        }
        return arr;
    }

    public void deserialize(JSONObject arr) {
        for (String s : arr.keySet()) {
            int groupId = Integer.parseInt(s);
            LinkedGroupData g = new LinkedGroupData();
            g.deserialize(groupId, arr.getJSONArray(s));
            add(g);
        }
    }

    @Override
    public void save(Home owner) {
        ArrayList<LinkedClips> groups = new ArrayList<>();
        ArrayList<TimelineClip> clips = new ArrayList<>();
        for (Track track : owner.getTracks().getTracks()) {
            for (TimelineClip clip : track.getContent().getSortedClips()) {
                clips.add(clip);
                if(clip.isLinked()) {
                    if(!groups.contains(clip.getLinkedGroup())) {
                        groups.add(clip.getLinkedGroup());
                    }
                }
            }
        }
        List<LinkedGroupData> groupsData = groups.stream()
                .map(g -> new LinkedGroupData(groups.indexOf(g)))
                .toList();
        for (TimelineClip clip : clips) {
            if(clip.isLinked()) {
                int gi = groups.indexOf(clip.getLinkedGroup());
                groupsData.get(gi).addClipId(clips.indexOf(clip));
            }
        }
        addAll(groupsData);
    }

    @Override
    public void load(Home owner) {
        ArrayList<TimelineClip> clips = new ArrayList<>();
        for (Track track : owner.getTracks().getTracks()) {
            clips.addAll(track.getContent().getSortedClips());
        }

        for (LinkedGroupData groupData : this) {
            LinkedClips linkedGroup = new LinkedClips();

            for (Integer clipIndex : groupData.getClipIds()) {
                if (clipIndex >= 0 && clipIndex < clips.size()) {
                    TimelineClip clip = clips.get(clipIndex);
                    linkedGroup.addClip(clip);
                }
            }
        }
    }
}