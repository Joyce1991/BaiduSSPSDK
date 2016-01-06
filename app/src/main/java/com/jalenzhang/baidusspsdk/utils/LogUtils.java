package com.jalenzhang.baidusspsdk.utils;

import android.text.TextUtils;
import android.util.Log;

import com.jalenzhang.baidusspsdk.BuildConfig;

/**
 * Created by chenzhiyong on 12/24/2015.
 */
public class LogUtils {

    public final static boolean DEBUG = BuildConfig.DEBUG;
    private final static String tagHead = "kyx_";

    /**
     * Message
     *
     * @param type
     * @param message
     */
    private static void log(int type, String message) {
        StackTraceElement stackTrace = Thread.currentThread().getStackTrace()[4];
        String className = stackTrace.getClassName();
        String tag = tagHead+className.substring(className.lastIndexOf('.') + 1);
        StringBuilder sb = new StringBuilder();

        sb.append("(")
                .append(stackTrace.getFileName())
                .append(":")
                .append(stackTrace.getLineNumber())
                .append(")")
                .append("#")
                .append(stackTrace.getMethodName())
                .append(":[")
                .append(message)
                .append("]");


        //message = stackTrace.getMethodName() + "#" + stackTrace.getLineNumber() + " [" + message + "]";
        switch (type) {
            case Log.DEBUG:
                Log.d(tag, sb.toString());
                break;
            case Log.INFO:
                Log.i(tag, sb.toString());
                break;
            case Log.WARN:
                Log.w(tag, sb.toString());
                break;
            case Log.ERROR:
                Log.e(tag, sb.toString());
                break;
            case Log.VERBOSE:
                Log.v(tag, sb.toString());
                break;
        }
    }


    private static String formatMessage(String message, Object... args) {
        if (TextUtils.isEmpty(message)) {
            return "";
        }
        if (args != null && args.length > 0) {
            try {
                return String.format(message, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return message;
    }
    //
    //public static void d(String tag, String message, Object... args) {
    //	if (DEBUG)
    //		Log.d(tag, formatMessage(message, args));
    //}
    //
    //public static void i(String tag, String message, Object... args) {
    //	if (DEBUG)
    //		Log.i(tag, formatMessage(message, args));
    //}
    //
    //public static void w(String tag, String message, Object... args) {
    //	if (DEBUG)
    //		Log.w(tag, formatMessage(message, args));
    //}
    //
    //public static void e(String tag, String message, Object... args) {
    //	if (DEBUG)
    //		Log.e(tag, formatMessage(message, args));
    //}
    //
    //public static void v(String tag, String message, Object... args) {
    //	if (DEBUG)
    //		Log.v(tag, formatMessage(message, args));
    //}

    public static void d(String message, Object... args) {
        if (DEBUG)
            log(Log.DEBUG, formatMessage(message, args));
    }

    public static void i(String message, Object... args) {
        if (DEBUG)
            log(Log.INFO, formatMessage(message, args));
    }

    public static void w(String message, Object... args) {
        if (DEBUG)
            log(Log.WARN, formatMessage(message, args));
    }

    public static void e(String message, Object... args) {
        if (DEBUG)
            log(Log.ERROR, formatMessage(message, args));
    }

    public static void v(String message, Object... args) {
        if (DEBUG)
            log(Log.VERBOSE, formatMessage(message, args));
    }

    public static void e(Throwable tr) {
        if (DEBUG) {
            tr.printStackTrace();
        }
    }
}
