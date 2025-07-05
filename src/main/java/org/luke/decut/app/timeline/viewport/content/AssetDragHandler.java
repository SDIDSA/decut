package org.luke.decut.app.timeline.viewport.content;

import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import org.luke.decut.app.home.Home;
import org.luke.decut.app.lib.assets.data.AssetData;
import org.luke.decut.app.lib.assets.data.AudioAssetData;
import org.luke.decut.app.lib.assets.data.VideoAssetData;
import org.luke.decut.app.lib.assets.filter.AssetType;
import org.luke.decut.app.timeline.clips.LinkedClips;
import org.luke.decut.app.timeline.clips.TimelineClip;
import org.luke.decut.app.timeline.tracks.Track;
import org.luke.decut.dragdrop.AssetDrag;
import org.luke.decut.dragdrop.DragContentException;
import org.luke.decut.dragdrop.DropHandler;

import java.util.HashMap;
import java.util.List;

public class AssetDragHandler implements DropHandler {
    private final Home owner;
    private final Track track;
    private final TrackContent root;

    private final HashMap<AssetData, ClipPreDrop> dragged;
    private final HashMap<AssetData, Track> tempTracks;
    private final HashMap<AssetData, Track> tracks;

    public AssetDragHandler(Home owner, Track track, TrackContent root) {
        this.owner = owner;
        this.track = track;
        this.root = root;

        dragged = new HashMap<>();
        tempTracks = new HashMap<>();
        tracks = new HashMap<>();
    }

    @Override
    public void onDragEntered(DragEvent event) {
        Dragboard db = event.getDragboard();
        try {
            AssetDrag dc = new AssetDrag(owner, db);
            AssetData asset = dc.getBody();
            if(!track.getType().isValidType(asset.getType())) {
                return;
            }
            if(asset instanceof VideoAssetData video) {
                VideoAssetData vid = video.getVideo();
                dragged.put(vid, new ClipPreDrop(owner, track, vid));
                AudioAssetData aud = video.getAudio();
                if(aud != null) {
                    dragged.put(aud, new ClipPreDrop(owner, track, aud));
                }
            } else {
                dragged.put(asset, new ClipPreDrop(owner, track, asset));
            }
        } catch (DragContentException x) {
            //Wrong drag type
        }
    }

    @Override
    public void onDragExited(DragEvent event) {
        Dragboard db = event.getDragboard();
        try {
            AssetDrag dc = new AssetDrag(owner, db);
            if (!track.getType().isValidType(dc.getBody().getType())) {
                return;
            }
            for (TimelineClip clip : root.getClips()) {
                clip.setTimeshift(0);
            }
            for (ClipPreDrop cpd : dragged.values()) {
                if(cpd.getParent() instanceof TrackContent tc) {
                    tc.getChildren().remove(cpd);
                }
            }
            owner.getTracks().getTracks().removeAll(tempTracks.values());
        } catch (DragContentException x) {
            //Wrong drag type
        }
        dragged.clear();
        tempTracks.clear();
        tracks.clear();
    }

    @Override
    public void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        for (TimelineClip clip : root.getClips()) {
            clip.setTimeshift(0);
        }
        try {
            AssetDrag dc = new AssetDrag(owner, db);
            if(!track.getType().isValidType(dc.getBody().getType())) {
                return;
            }
            event.acceptTransferModes(TransferMode.MOVE);

            AssetData asset = dc.getBody();
            double x = event.getX();
            x = Math.max(0, x - 30);
            double timePosition = x / owner.ppsProperty().get();
            double dur = asset.getDurationSeconds();
            TimelineClip collision = getCollision(timePosition);
            if(collision != null) {
                timePosition = owner.snapToNextFrame(collision.getEndTime());
            }

            disturbedClips(timePosition, dur, false);

            double frameRate = owner.framerateProperty().get();
            double time = Math.floor(timePosition * frameRate) / frameRate;

            AssetData main;
            if(asset instanceof VideoAssetData video) {
                main = video.getVideo();
            } else {
                main = asset;
            }

            ClipPreDrop mainPd = dragged.get(main);
            positionInTrack(mainPd, main, track, time);

            for (AssetData assetData : dragged.keySet()) {
                if(assetData != main) {
                    positionInTrack(dragged.get(assetData), assetData, findTrackFor(assetData, time), time);
                }
            }

        } catch (DragContentException e) {
            //Wrong drag type
        }
    }

    private Track findTrackFor(AssetData asset, double position) {
        List<Track> tracks = owner.getTracks().getTracks();

        if(tempTracks.containsKey(asset)) {
            Track tempTrack = tempTracks.get(asset);
            int tempIndex = tracks.indexOf(tempTrack);
            if(tracks.size() > tempIndex + 1) {
                Track pot = tracks.get(tempIndex + 1);
                if(pot.getType().isValidType(asset.getType()) &&
                        !collidesWithAnything(pot.getContent(), position, asset.getDurationSeconds())) {
                    owner.getTracks().getTracks().remove(tempTracks.get(asset));
                    tempTracks.remove(asset);
                    return track;
                } else {
                    return tempTrack;
                }
            }
        }

        int thisIndex = tracks.indexOf(track);
        Track nextTrack = null;
        if(tracks.size() > thisIndex + 1) {
            nextTrack = tracks.get(thisIndex + 1);
            if(!nextTrack.getType().isValidType(asset.getType()) || collidesWithAnything(nextTrack.getContent(), position, asset.getDurationSeconds())) {
                nextTrack = null;
            }
        }
        if(nextTrack == null) {
            if(asset.getType() == AssetType.AUDIO) {
                nextTrack = owner.getTracks().getTrackList().addAudioTrackAt(thisIndex + 1);
                tempTracks.put(asset, nextTrack);
            }
        }
        return nextTrack;
    }

    private boolean collidesWithAnything(TrackContent track, double startTime, double duration) {
        for (TimelineClip clip : track.getSortedClips()) {
            int cs = owner.timeToFrame(clip.getStartTime() + clip.getTimeshift());
            int ce = owner.timeToFrame(clip.getEndTime() + clip.getTimeshift());

            int start = owner.timeToFrame(startTime);
            int end = owner.timeToFrame(startTime + duration);

            if (start < ce && end > cs) {
                return true;
            }
        }
        return false;
    }

    private void positionInTrack(ClipPreDrop pd, AssetData asset, Track track, double time) {
        if(pd.getParent() != track.getContent()) {
           if(pd.getParent() != null && pd.getParent() instanceof TrackContent oldC) {
               oldC.getChildren().remove(pd);
           }
           track.getContent().getChildren().add(pd);
        }
        pd.setLayoutX(time * owner.ppsProperty().get());
        pd.setTrack(track);
        tracks.put(asset, track);
    }

    @Override
    public void onDragDropped(DragEvent event) {
        root.getChildren().removeAll(dragged.values());

        Dragboard db = event.getDragboard();
        boolean success = false;
        try {
            AssetDrag dc = new AssetDrag(owner, db);
            if(!track.getType().isValidType(dc.getBody().getType())) {
                return;
            }

            LinkedClips group = new LinkedClips();
            HashMap<Track, Integer> tracksAdded = new HashMap<>();
            HashMap<TimelineClip, Track> clipsAdded = new HashMap<>();
            HashMap<TimelineClip, Double> disturbed = new HashMap<>();

            for (TimelineClip clip : root.getClips()) {
                double dt = clip.getTimeshift();
                if(dt != 0) {
                    double frameRate = owner.framerateProperty().get();
                    double df = Math.round(dt * frameRate) / frameRate;
                    disturbed.put(clip, df);
                    clip.setTimeshift(0);
                }
            }

            for (AssetData assetData : dragged.keySet()) {
                ClipPreDrop pd = dragged.get(assetData);
                double timePosition = pd.getLayoutX() / owner.ppsProperty().get();
                double frameRate = owner.framerateProperty().get();
                double framePosition = Math.round(timePosition * frameRate) / frameRate;
                Track track = tracks.get(assetData);
                TimelineClip clip = track.getContent().createClip(assetData, framePosition);
                clipsAdded.put(clip, track);
                if(dragged.size() > 1) {
                    group.addClip(clip);
                }
                if(tempTracks.containsValue(track)) {
                    int index = owner.getTracks().getTracks().indexOf(track);
                    tracksAdded.put(track, index);
                }
            }

            owner.getTracks().getTracks().removeAll(tempTracks.values());
            tempTracks.clear();

            Runnable perform = () -> {
                disturbed.forEach((clip, df) -> {
                    clip.setStartTime(clip.getStartTime() + df);
                });

                tracksAdded.forEach((track, index) -> {
                    owner.getTracks().getTrackList().addTrackAt(track, index);
                });

                clipsAdded.forEach((clip, track) -> {
                    track.getContent().addClip(clip);
                });
            };

            Runnable undo = () -> {
                clipsAdded.forEach((clip, track) -> {
                    track.getContent().getClips().remove(clip);
                });
                tracksAdded.forEach((track, _) -> {
                    owner.getTracks().getTracks().remove(track);
                });
                disturbed.forEach((clip, df) -> {
                    clip.setStartTime(clip.getStartTime() - df);
                });
            };

            owner.perform("Add clip", perform, undo);

            success = true;
        } catch (DragContentException e) {
            //Wrong drag type
        }
        dragged.clear();
        tempTracks.clear();
        tracks.clear();
        event.setDropCompleted(success);
        event.consume();
    }

    public TimelineClip getCollision(double time) {
        TimelineClip last = null;
        for (TimelineClip clip : root.getSortedClips()) {
            double start = clip.getStartTime();
            double end = clip.getEndTime();
            double mid = (start + end) / 2;
            if(time >= mid && time < end) {
                return clip;
            } else if(last != null && last.getEndTime() == start &&
                    time > start && time < mid) {
                return last;
            }
            last = clip;
        }
        return null;
    }

    public void disturbedClips(double time, double duration, boolean byClip) {
        for (TimelineClip clip : root.getSortedClips()) {
            double start = clip.getStartTime();
            double end = clip.getEndTime();
            double mid = (start + end) / 2;
            double myEnd = time + duration;
            if ((time < mid || time < end) && myEnd > start && clip.getTimeshift() == 0) {
                double newStart = time + duration;
                double newEnd = newStart + clip.getDuration();
                double dt = newStart - clip.getStartTime();
                clip.setTimeshift(dt);
                disturbedClips(time, newEnd - time, true);
                break;
            }
        }
    }
}
