package org.luke.decut.file.project;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class LinkedGroupData {
    public static final String GROUP_ID = "groupId";
    public static final String CLIP_IDS = "clipIds";

    private Integer groupId;
    private List<Integer> clipIds;

    public LinkedGroupData() {
        this.clipIds = new ArrayList<>();
    }

    public LinkedGroupData(int groupId) {
        this();
        this.groupId = groupId;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public List<Integer> getClipIds() {
        return clipIds;
    }

    public void addClipId(Integer clipId) {
        clipIds.add(clipId);
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public JSONArray serialize() {
        JSONArray clipArray = new JSONArray();
        for (Integer clipId : clipIds) {
            clipArray.put(clipId);
        }
        return clipArray;
    }

    public void deserialize(int groupId, JSONArray data) {
        this.groupId = groupId;
        clipIds = new ArrayList<>();

        for (int i = 0; i < data.length(); i++) {
            clipIds.add(data.getInt(i));
        }
    }
}