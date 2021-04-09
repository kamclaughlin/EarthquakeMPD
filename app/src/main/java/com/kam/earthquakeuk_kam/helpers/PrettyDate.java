/*
  Created by Kerry-Anne McLaughlin
  kmclau208@caledonian.ac.uk, s1802675
 */
package com.kam.earthquakeuk_kam.helpers;

import java.util.Date;

public class PrettyDate {
    private static final int SECS = 1000;
    private static final int MINS = 60 * SECS;
    private static final int HRS = 60 * MINS;
    private static final int DAYS = 24 * HRS;

    public static String getTimeSince(Date date) {

        long now = new Date().getTime();
        long then = date.getTime();

        if (then > now || then <= 0) {
            return "in the future";
        }

        final long diff = now - then;

        if (diff < MINS) {
            return "Moments ago.";
        } else if (diff < 2 * MINS) {
            return "One minute ago.";
        } else if (diff < 50 * MINS) {
            return diff / MINS + " minutes ago.";
        } else if (diff < 90 * MINS) {
            return "About an hour ago.";
        } else if (diff < 24 * HRS) {
            return diff / HRS + " hours ago.";
        } else if (diff < 48 * HRS) {
            return "Yesterday";
        } else if (diff < 14 * DAYS) {
            return diff / DAYS + " days ago.";
        } else {
            return diff / DAYS + " days ago.";
        }
    }

}
