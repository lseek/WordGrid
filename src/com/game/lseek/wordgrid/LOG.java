package com.game.lseek.wordgrid;

import android.util.Log;

/*
 * Simple (incomplete, but sufficient) wrappers for logging.
 */
class LOG {
    public static void d(String tag, String fmt, Object... args) {
        Log.d(tag, String.format(fmt, args));
    }


    public static void e(String tag, String fmt, Object... args) {
        Log.e(tag, String.format(fmt, args));
    }


    public static void i(String tag, String fmt, Object... args) {
        Log.i(tag, String.format(fmt, args));
    }
}
