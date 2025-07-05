package org.luke.gui.controls.text;

import org.luke.gui.controls.text.keyed.KeyedText;
import org.luke.gui.locale.Locale;
import org.luke.gui.window.Window;

import java.time.LocalDateTime;
import java.util.Date;

public class DateLabel extends KeyedText {
    private LocalDateTime date;
    private DateFormat format = DateFormat.FULL_LONG;
    private boolean ignoreTime = false;

    public DateLabel(Window owner) {
        super(owner, "");
    }

    public void setFormat(DateFormat format) {
        this.format = format;
        applyLocale(getWindow().getLocale().get());
    }

    public void setDate(Date date) {
        setDate(DateFormat.convertToLocalDateTime(date));
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
        applyLocale(getWindow().getLocale().get());
    }

    public void setIgnoreTime(boolean ignoreTime) {
        this.ignoreTime = ignoreTime;
        applyLocale(getWindow().getLocale().get());
    }

    @Override
    public void applyLocale(Locale locale) {
        if(date != null && format != null) {
            setText(format.format(locale, date, ignoreTime));
        }
    }
}
