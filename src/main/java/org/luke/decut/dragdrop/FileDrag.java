package org.luke.decut.dragdrop;

import javafx.scene.input.Dragboard;
import org.json.JSONArray;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileDrag extends DragContent<List<File>> {
    public static final String TYPE_FILES = "files";
    public static final String ARRAY = "array";

    public FileDrag(Home owner, List<File> files) {
        super(owner, TYPE_FILES, files);
    }

    public FileDrag(Home owner, Dragboard db) throws DragContentException {
        super(owner, db);
    }

    @Override
    public List<File> decode(JSONObject body) throws DragContentException {
        try {
            JSONArray fileArray = body.getJSONArray(ARRAY);
            List<File> files = new ArrayList<>();
            for (int i = 0; i < fileArray.length(); i++) {
                files.add(new File(fileArray.getString(i)));
            }
            return files;
        } catch (Exception e) {
            throw new DragContentException("Failed to decode file list", e);
        }
    }

    @Override
    public JSONObject encode(List<File> files) {
        JSONObject body = new JSONObject();
        JSONArray fileArray = new JSONArray();
        for (File file : files) {
            fileArray.put(file.getAbsolutePath());
        }
        body.put(ARRAY, fileArray);
        return body;
    }

    @Override
    public boolean isValidType(String type) {
        return TYPE_FILES.equals(type);
    }
}
