package org.luke.gui.controls.recycle.table;

import javafx.geometry.Dimension2D;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.function.Function;


public class TableColumns {

    public static <S, T> ColumnRenderer<S> createTextColumn(String header,
                                                            Function<S, T> extractor,
                                                            Function<T, String> renderer,
                                                            double initW,
                                                            Comparator<S> comparator) {
        return new TypedColumnRenderer<>(header, (tv) -> new TextCell<>(tv, extractor, renderer), initW, comparator);
    }

    public static <S, T> ColumnRenderer<S> createCustomColumn(String header,
                                                              Function<TableView<S>, TypedTableCell<S, T>> creator,
                                                              double initW,
                                                              Comparator<S> comparator) {
        return new TypedColumnRenderer<>(header, creator, initW, comparator);
    }

    // --- String Columns ---

    public static <S> ColumnRenderer<S> stringColumn(String header, Function<S, String> extractor,
                                                     double initW, Comparator<S> comparator) {
        return createTextColumn(header, extractor, text -> text, initW, comparator);
    }

    public static <S> ColumnRenderer<S> stringColumn(String header, Function<S, String> extractor, double initW) {
        return stringColumn(header, extractor, initW,
                Comparator.comparing(extractor, Comparator.nullsFirst(String.CASE_INSENSITIVE_ORDER)));
    }

    public static <S> ColumnRenderer<S> stringColumn(String header, Function<S, String> extractor) {
        return stringColumn(header, extractor, 0);
    }


    // --- File Size Columns ---

    /**
     * Creates a column for displaying file sizes. Sorts by the actual byte value.
     *
     * @param header    The column header text.
     * @param extractor A function that extracts a Number (e.g., Long, Integer) representing the file size in bytes.
     * @param initW     The initial relative width.
     */
    public static <S> ColumnRenderer<S> byteSizeColumn(String header, Function<S, ? extends Number> extractor, double initW) {
        return createTextColumn(header, extractor,
                bytes -> formatByteSize(bytes.longValue()),
                initW,
                Comparator.comparing(s -> extractor.apply(s).longValue()));
    }

    public static <S> ColumnRenderer<S> byteSizeColumn(String header, Function<S, ? extends Number> extractor) {
        return byteSizeColumn(header, extractor, 0);
    }


    // --- Date/Time Columns ---

    /**
     * Creates a column for displaying date/time from a {@code long} (milliseconds since epoch).
     *
     * @param header    The column header text.
     * @param extractor A function that extracts the date as a long.
     * @param initW     The initial relative width.
     */
    public static <S> ColumnRenderer<S> epochColumn(String header, Function<S, Long> extractor, double initW) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return createTextColumn(header, extractor,
                millis -> millis == null ? "" : df.format(new Date(millis)),
                initW,
                Comparator.comparing(extractor, Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    public static <S> ColumnRenderer<S> epochColumn(String header, Function<S, Long> extractor) {
        return epochColumn(header, extractor, 0);
    }

    /**
     * Creates a column for displaying date/time from a {@code LocalDateTime} object.
     *
     * @param header    The column header text.
     * @param extractor A function that extracts the LocalDateTime object.
     * @param initW     The initial relative width.
     */
    public static <S> ColumnRenderer<S> dateTimeColumn(String header, Function<S, LocalDateTime> extractor, double initW) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return createTextColumn(header, extractor,
                ldt -> ldt == null ? "" : dtf.format(ldt),
                initW,
                Comparator.comparing(extractor, Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    public static <S> ColumnRenderer<S> dateTimeColumn(String header, Function<S, LocalDateTime> extractor) {
        return dateTimeColumn(header, extractor, 0);
    }


    // --- Integer Columns ---

    /**
     * Creates a column for displaying integer values.
     *
     * @param header    The column header text.
     * @param extractor A function that extracts the Integer.
     * @param initW     The initial relative width.
     */
    public static <S> ColumnRenderer<S> intColumn(String header, Function<S, Integer> extractor, double initW) {
        return createTextColumn(header, extractor,
                Object::toString,
                initW,
                Comparator.comparing(extractor, Comparator.nullsFirst(Comparator.naturalOrder())));
    }

    public static <S> ColumnRenderer<S> intColumn(String header, Function<S, Integer> extractor) {
        return intColumn(header, extractor, 0);
    }

    // --- Duration Columns ---

    /**
     * Creates a column for displaying media duration from seconds.
     * Formats as HH:MM:SS or MM:SS depending on duration length.
     *
     * @param header    The column header text.
     * @param extractor A function that extracts the duration in seconds (Integer, Long, or Double).
     * @param initW     The initial relative width.
     */
    public static <S> ColumnRenderer<S> durationColumn(String header, Function<S, ? extends Number> extractor, double initW) {
        return createTextColumn(header, extractor,
                duration -> formatDuration(duration.longValue()),
                initW,
                Comparator.comparing(s -> extractor.apply(s).longValue()));
    }

    public static <S> ColumnRenderer<S> durationColumn(String header, Function<S, ? extends Number> extractor) {
        return durationColumn(header, extractor, 0);
    }

    /**
     * Creates a column for displaying media duration from milliseconds.
     * Formats as HH:MM:SS or MM:SS depending on duration length.
     *
     * @param header    The column header text.
     * @param extractor A function that extracts the duration in milliseconds.
     * @param initW     The initial relative width.
     */
    public static <S> ColumnRenderer<S> durationMillisColumn(String header, Function<S, ? extends Number> extractor, double initW) {
        return createTextColumn(header, extractor,
                duration -> formatDuration(duration.longValue()),
                initW,
                Comparator.comparing(s -> extractor.apply(s).longValue()));
    }

    public static <S> ColumnRenderer<S> durationMillisColumn(String header, Function<S, ? extends Number> extractor) {
        return durationMillisColumn(header, extractor, 0);
    }

    public static <S> ColumnRenderer<S> resolutionColumn(String header, Function<S, Dimension2D> extractor, double initW) {
        return createTextColumn(header, extractor,
                res -> res == null ? "" : (int) res.getWidth() + "x" + (int) res.getHeight(),
                initW,
                Comparator.comparing(s -> {
                    Dimension2D dim = extractor.apply(s);
                    if(dim == null) return 0.0;
                    return dim.getWidth() * dim.getHeight();
                }));
    }

// --- Helper Methods (add to existing helper section) ---

    /**
     * Formats a duration in seconds into a human-readable string.
     * Examples: "1:23", "12:34", "1:23:45"
     */
    private static String formatDuration(long totalMillis) {
        if (totalMillis < 0) return "";

        long totalSeconds = totalMillis / 1000;
        long hours = totalSeconds / 3600;
        long minutes = (totalSeconds % 3600) / 60;
        long seconds = totalSeconds % 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    // --- Helper Methods ---

    /**
     * Formats a byte count into a human-readable string (e.g., "1.2 KB", "5.0 MB").
     */
    private static String formatByteSize(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}