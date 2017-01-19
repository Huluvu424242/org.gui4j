package org.gui4j.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Represents a day.<br>
 * Allows to conveniently work with days instead of dates that also include time
 * info.
 */
public final class Day implements Comparable, Nameable
{
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private Calendar calendar;

    /**
     * Private constructor for Day. Clients should use factory method
     * {@link #getInstance(Date)}.
     * 
     * @param date
     */
    private Day(Calendar cal)
    {
        calendar = Calendar.getInstance(UTC);
        if (cal.get(Calendar.YEAR) < 2000)
        {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 2000);
        }
        calendar.set(Calendar.YEAR,cal.get(Calendar.YEAR));
        calendar.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.MONTH,cal.get(Calendar.MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public Date getDate()
    {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR,calendar.get(Calendar.YEAR));
        cal.set(Calendar.DAY_OF_MONTH,calendar.get(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.MONTH,calendar.get(Calendar.MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * Decides if this Day is considered to be a working day. Working days are
     * Monday to Friday, i.e. Saturday and Sunday are not working days.
     * 
     * @return <code>true</code>, if this Day represents a working day,
     *         <code>false</code> otherwise.
     */
    public boolean isWorkday()
    {
        return isWerktag();
    }

    /**
     * @return (see {@link #isWorkday()})
     * @deprecated replaced by english named method {@link #isWorkday()}.
     */
    public boolean isWerktag()
    {
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return day != Calendar.SATURDAY && day != Calendar.SUNDAY;
    }

    /**
     * Returns the day of the week represented by this <code>Day</code>.
     * 
     * @return The day of week, represented as one of the constants defined in
     *         {@link Calendar}, i.e. {@link Calendar#SUNDAY} through
     *         {@link Calendar#SATURDAY}.
     */
    public int getDayOfWeek()
    {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * Returns the month this <code>Day</code> belongs to.
     * 
     * @return the month as defined by the <code>Calendar</code> constants
     *         {@link Calendar#JANUARY} through {@link Calendar#DECEMBER}.
     */
    public int getMonth()
    {
        return getMonat();
    }

    /**
     * @return (see {@link #getMonth()})
     * @deprecated replaced by the english named method {@link #getMonth()}.
     */
    public int getMonat()
    {
        return calendar.get(Calendar.MONTH);
    }

    /**
     * Returns the week of year ("calendar week") this <code>Day</code> belongs to.<br>
     * Note: Currently this class implicitly uses the default locale to create
     * its Calendar object so the criteria which define the first week of a year
     * are taken from that default locale.
     * 
     * @return the week of year
     */
    public int getWeek()
    {
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * Returns the year this <code>Day</code> belongs to.
     * 
     * @return the year
     */
    public int getYear()
    {
        return getJahr();
    }

    /**
     * @deprecated use the english named method {@link #getYear()} instead.
     */
    public int getJahr()
    {
        return calendar.get(Calendar.YEAR);
    }

    /**
     * Returns the year the week containing this <code>Day</code> belongs to.
     * This is not necessarily the year this <code>Day</code> belongs to: if
     * this <code>Day</code> is part of the first week of a year, the
     * <code>Day</code> itself could still be in the previous year while the
     * week belongs to the next year.
     * 
     * @return the year of this <code>Day</code>'s week.
     */
    public int getYearForWeek()
    {
        return getJahrForWeek();
    }

    /**
     * @return (see {@link #getYearForWeek()}).
     * @deprecated use the english named method {@link #getYearForWeek()}
     *             instead.
     */
    public int getJahrForWeek()
    {
        int week = getWeek();
        if (week == 1)
        {
            if (getMonat() == 0)
            {
                return calendar.get(Calendar.YEAR);
            }
            else
            {
                return calendar.get(Calendar.YEAR) + 1;
            }
        }
        else
        {
            return calendar.get(Calendar.YEAR);
        }
    }

    /**
     * Public factory method to get an instance of <code>Day</code> from.
     * 
     * @param date
     *            The date for which to create a <code>Day</code>.
     * @return a Day instance for the given date.
     */
    public static Day getInstance(Date date)
    {
        if (date == null)
        {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) < 2000)
        {
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + 2000);
        }
        return new Day(cal);
    }

    /**
     * Get a <code>Day</code> instance representing today.
     * 
     * @return a <code>Day</code> instance representing today.
     */
    public static Day getToday()
    {
        return getInstance(new Date());
    }

    /**
     * Returns the first day of the specified month in the specified year.
     * 
     * @param year
     *            year (fully qualified, i.e. four digits required)
     * @param month
     *            month according to usage in {@link Calendar} (i.e. 0 is first
     *            month).
     * @return Day
     */
    public static Day getFirstDayOf(int year, int month)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.MONTH, month);
        return new Day(calendar);
    }

    /**
     * Returns the first day of the specified calendar week in the specified year.
     * Note: the returned <code>Day</code> might not be in the specified year if
     * calendar week 1 was specified.
     * @param year year (fully qualified, i.e. four digits required)
     * @param week calendar week
     * @return the first day of the specified calendar week in the specified year.
     */
    public static Day getFirstDayOfWeek(int year, int week) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.WEEK_OF_YEAR, week);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        return new Day(calendar);
    }
    
    /**
     * Returns the last day of the specified month in the specified year.
     * 
     * @param year
     *            year (fully qualified, i.e. four digits required)
     * @param month
     *            month according to usage in {@link Calendar} (i.e. 0 is first
     *            month).
     * @return Day
     */
    public static Day getLastDayOf(int year, int month)
    {
        if (month == Calendar.DECEMBER)
        {
            return getLastDayOfYear(year);
        }
        Day d = getFirstDayOf(year, month + 1);
        return d.prevDay();
    }

    /**
     * Returns the last day of the specified year.
     * 
     * @param year
     *            year (fully qualified, i.e. four digits required)
     * @return the last day of the specified year.
     */
    public static Day getLastDayOfYear(int year)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, Calendar.DECEMBER);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        return new Day(calendar);
    }

    /**
     * Returns the day following this day.
     * 
     * @return Day
     */
    public Day nextDay()
    {
        Calendar cal = Calendar.getInstance(UTC);
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, 1);
        return new Day(cal);
    }

    /**
     * Returns the number of days between this <code>Day</code> and the
     * specified <code>Day</code>. the result includes both this and the
     * specified <code>Day</code>.
     * 
     * @param other
     *            <code>Day for which to return the number of days between it and this <code>Day</code>.
     *            May not be <code>null</code>.
     * @return int the number of days between this <code>Day</code> and the specified <code>Day</code>.
     */
    public int getDiff(Day other)
    {
        assert other != null;
        if (compareDay(other) > 0)
        {
            // Aktueller Tag liegt nach dem uebergebenen
            return 0;
        }
        long ms1 = getDate().getTime();
        long ms2 = other.getDate().getTime();
        long diff = ms2 - ms1;
        diff /= 1000; // Sekunden
        diff /= 60; // Minuten
        diff /= 60; // Stunden
        diff /= 24; // Tage
        return (int) diff + 1; // +1, da beide Tage inklusiv gerechnet werden
    }

    /**
     * Returns the day <code>diff</code> days after this <code>Day</code>.<br>
     * <code>nextDay(1)</code> is equivalent to <code>nextDay()</code>,
     * <code>nextDay(2)</code> returns the day after <code>nextDay()</code> etc.
     * 
     * @param diff defines the day to return based on this <code>Day</code>
     * @return Day
     */
    public Day nextDay(int diff)
    {
        Calendar cal = Calendar.getInstance(UTC);
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, diff);
        return new Day(cal);
    }

    /**
     * Returns the previous <code>Day</code>. 
     * 
     * @return Day
     */
    public Day prevDay()
    {
        Calendar cal = Calendar.getInstance(UTC);
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, -1);
        return new Day(cal);
    }

    /**
     * Returns the first day of the month this <code>Day</code> belongs to.
     * @return the first day of the month this <code>Day</code> belongs to.
     */
    public Day getFirstDayOfMonth()
    {
        Calendar cal = Calendar.getInstance(UTC);
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, -cal.get(Calendar.DAY_OF_MONTH) + 1);
        return new Day(cal);
    }

    /**
     * Returns the first day of the year this <code>Day</code> belongs to.
     * @return the first day of the year this <code>Day</code> belongs to.
     */
    public Day getFirstDayOfYear()
    {
        Calendar cal = Calendar.getInstance(UTC);
        cal.setTime(calendar.getTime());
        cal.add(Calendar.DATE, -cal.get(Calendar.DAY_OF_YEAR) + 1);
        return new Day(cal);
    }

    public int sub(Day otherDay)
    {
        long thisTime = calendar.getTimeInMillis();
        long otherTime = otherDay.calendar.getTimeInMillis();
        long diff = thisTime - otherTime;
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    /**
     * Compares this <code>Day</code> to parameter <code>day</code>.<br>
     * Returns <code>-1</code> if this <code>Day</code> is earlier than
     * the specified one. Returns <code>0</code> if the <code>Day</code>s
     * denote the same day. Returns 1 otherwise.
     * 
     * @param day <code>Day</code> to compare this day to.
     * @return int see above
     */
    public int compareDay(Day day)
    {
        long myDays = calendar.get(Calendar.YEAR) * 12l * 32l + calendar.get(Calendar.MONTH) * 32l
                + calendar.get(Calendar.DATE);
        long otherDays = 0;
        if (day != null) {
            otherDays = day.calendar.get(Calendar.YEAR) * 12l * 32l + day.calendar.get(Calendar.MONTH) * 32l
            + day.calendar.get(Calendar.DATE);
        }
        if (myDays == otherDays)
        {
            return 0;
        }
        return myDays > otherDays ? 1 : -1;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof Day)
        {
            return compareDay((Day) obj) == 0;
        }
        else
        {
            return false;
        }
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode()
    {
        return calendar.get(Calendar.YEAR) * 12 * 32 + calendar.get(Calendar.MONTH) * 32 + calendar.get(Calendar.DATE);
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        return "" + day + "." + month + "." + year;
    }

    /**
     * @see java.lang.Comparable#compareTo(Object)
     */
    public int compareTo(Object o)
    {
        Day other = (Day) o;
        return compareDay(other);
    }

    /**
     * @see org.gui4j.util.Nameable#getNameTag()
     */
    public String getNameTag()
    {
        return getClass().getName() + "_" + getShortTag();
    }

    /**
     * @see org.gui4j.util.Nameable#getShortTag()
     */
    public String getShortTag()
    {
        return String.valueOf(calendar.getTimeInMillis());
    }

}
