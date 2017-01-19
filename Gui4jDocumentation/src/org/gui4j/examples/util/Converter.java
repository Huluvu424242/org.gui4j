package org.gui4j.examples.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.gui4j.util.Day;

/**
 * Converts all necessary data types to Strings (for display)
 * and vice versa (for input).
 */
public class Converter {
    
    private final DateFormat dateFormat;

    public Converter() {
        Locale locale = Locale.GERMAN;
        dateFormat = DateFormat.getDateInstance(DateFormat.SHORT, locale);
    }

    public String day2str(Day day) {
        if (day == null) {
            return "";
        } else {
            return dateFormat.format(day.getDate());
        }
    }

    public Day str2Day(String value) {
        value = value.trim();
        if (value.equals("")) {
            return null;
        }
        if ("today".equalsIgnoreCase(value) || "t".equalsIgnoreCase(value) || "heute".equalsIgnoreCase(value)
            || "h".equalsIgnoreCase(value)) {
            return Day.getToday();
        }
        try {
            ParsePosition pos = new ParsePosition(0);
            Date date = dateFormat.parse(value, pos);
            if (pos.getErrorIndex() != -1 || pos.getIndex() != value.length()) {
                throw new ParseException(value, pos.getIndex());
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            if (cal.get(Calendar.YEAR) < 2000) {
                throw new RuntimeException("Invalid date: year = " + cal.get(Calendar.YEAR));
            }
            if (cal.get(Calendar.YEAR) > 2100) {
                throw new RuntimeException("Invalid date: year = " + cal.get(Calendar.YEAR));
            }
            return Day.getInstance(date);
        } catch (Throwable e) {
            Object[] args = { value };
            throw new UserInformation("error_parse_str2Day", args);
        }
    }

    public String int2str(int i) {
        return String.valueOf(i);
    }

    public int str2int(String val) {
        return Integer.parseInt(val);
    }

}
