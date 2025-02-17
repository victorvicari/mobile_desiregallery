package com.example.desiregallery.logging

import android.util.Log
import com.crashlytics.android.Crashlytics
import io.sentry.Sentry
import io.sentry.event.Event
import io.sentry.event.EventBuilder

/**
 * @author babaetskv on 18.09.19
 */
object DGLogger : ILogger {

    override fun logDebug(tag: String, message: String) {
        Log.d(tag, message)
    }

    override fun logInfo(tag:String, message: String) {
        Log.i(tag, message)
    }

    override fun logError(tag: String, message: String) {
        Log.e(tag, message)
        Crashlytics.log(Log.ERROR, tag, message)
        Sentry.capture(EventBuilder().withLevel(Event.Level.ERROR).withTag("tag", tag).withMessage(message).build())
    }

    override fun logWarning(tag: String, message: String) {
        Log.w(tag, message)
        Crashlytics.log(Log.WARN, tag, message)
        Sentry.capture(EventBuilder().withLevel(Event.Level.WARNING).withTag("tag", tag).withMessage(message).build())
    }
}