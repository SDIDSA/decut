
package org.luke.decut.dragdrop;

import javafx.scene.input.Dragboard;
import javafx.scene.input.DataFormat;
import org.json.JSONObject;
import org.luke.decut.app.home.Home;

import java.util.Map;

public abstract class DragContent<T> {
    public static final String TYPE = "type";
    public static final String BODY = "body";
    public static final DataFormat CUSTOM_FORMAT = new DataFormat("application/decut-drag");

    private final String type;
    private final T body;
    protected final Home owner;

    public DragContent(Home owner, Dragboard db) throws DragContentException {
        this.owner = owner;
        try {
            if (!db.hasContent(CUSTOM_FORMAT)) {
                throw new DragContentException("Dragboard does not contain expected format");
            }

            String content = (String) db.getContent(CUSTOM_FORMAT);
            JSONObject data = new JSONObject(content);

            if (!data.has(TYPE) || !data.has(BODY)) {
                throw new DragContentException("Invalid drag content structure");
            }

            this.type = data.getString(TYPE);
            if (!isValidType(this.type)) {
                throw new DragContentException("Unsupported drag content type: " + this.type);
            }

            this.body = decode(data.getJSONObject(BODY));
        } catch (Exception e) {
            throw new DragContentException("Failed to parse drag content", e);
        }
    }

    public DragContent(Home owner, String type, T body) {
        this.owner = owner;
        this.type = type;
        this.body = body;
    }

    public void putContent(Dragboard db) {
        db.setContent(Map.of(CUSTOM_FORMAT, encode().toString()));
    }

    public JSONObject encode() {
        JSONObject data = new JSONObject();
        data.put(TYPE, type);
        data.put(BODY, encode(body));
        return data;
    }

    public abstract T decode(JSONObject body) throws DragContentException;
    public abstract JSONObject encode(T body);
    public abstract boolean isValidType(String type);

    public String getType() { return type; }
    public T getBody() { return body; }
    public Home getOwner() { return owner; }
}