package org.luke.decut.app.timeline.viewport.timeRuler;

import org.luke.gui.controls.Font;
import org.luke.gui.controls.text.unkeyed.Text;

public class TimeLabel extends Text {

    public TimeLabel() {
        super("");
        opacityProperty().unbind();
        setMouseTransparent(true);
        setOpacity(0.7);
        setFont(new Font(10));
    }

    public void updatePosition(double pre, double timePosition, double pixelsPerSecond, double xScroll,
                               double vpw, double framerate) {
        setText(formatTime(pixelsPerSecond, timePosition, framerate));
        double x = pre + (timePosition * pixelsPerSecond);
        double w = getBoundsInLocal().getWidth();
        double bx = x - (w / 2);
        double ex = x + (w / 2);

        if(bx < xScroll + 5) {
            bx = xScroll + 5;
        }

        if(ex > xScroll + vpw - 5) {
            bx = xScroll + vpw - 5 - w;
        }

        setLayoutX((int) bx);
        setLayoutY(20);
    }

    private String formatTime(double pps, double seconds, double framerate) {
        if (seconds < 0) {
            return "-" + formatTime(pps, -seconds, framerate);
        }

        if (pps >= 400) {
            return formatAsTimecode(seconds, framerate);
        }
        else if (pps >= 100) {
            return formatWithMilliseconds(seconds);
        }
        else {
            return formatStandard(seconds);
        }
    }

    private String formatAsTimecode(double seconds, double framerate) {
        int totalFrames = (int) Math.round(seconds * framerate);

        int totalSeconds = totalFrames / (int) framerate;
        int frameInSecond = totalFrames % (int) framerate;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if(frameInSecond == 0) {
            if (hours > 0) {
                return String.format("%02d:%02d:%02d", hours, minutes, secs);
            } else if (minutes > 0) {
                return String.format("%02d:%02d", minutes, secs);
            } else {
                return String.format("%ds", secs);
            }
        } else {
            if (hours > 0) {
                return String.format("%02d:%02d:%02d.%02df", hours, minutes, secs, frameInSecond);
            } else if (minutes > 0) {
                return String.format("%02d:%02d.%02df", minutes, secs, frameInSecond);
            } else {
                return String.format("%02d.%02df", secs, frameInSecond);
            }
        }
    }

    private String formatWithMilliseconds(double seconds) {
        int totalMillis = (int) Math.round(seconds * 1000);
        int totalSeconds = totalMillis / 1000;
        int millis = (totalMillis % 1000) / 10;

        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if(millis == 0) {
            if (hours > 0) {
                return String.format("%02d:%02d:%02d", hours, minutes, secs);
            } else if (minutes > 0) {
                return String.format("%02d:%02d", minutes, secs);
            } else {
                return String.format("%ds", secs);
            }
        } else {
            if (hours > 0) {
                return String.format("%02d:%02d:%02d.%02d", hours, minutes, secs, millis);
            } else if (minutes > 0) {
                return String.format("%02d:%02d.%02d", minutes, secs, millis);
            } else {
                return String.format("%02d.%02d", secs, millis);
            }
        }
    }

    private String formatStandard(double seconds) {
        int totalSeconds = (int) Math.round(seconds);
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int secs = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else if (minutes > 0) {
            return String.format("%02d:%02d", minutes, secs);
        } else {
            return String.format("%ds", secs);
        }
    }
}