package com.bscan.app.core.ui.components

import android.content.Context
import android.text.format.Formatter
import com.bscan.app.R
import java.text.DateFormat
import java.util.Date

object BscanFormatters {

    fun formatDate(epochMs: Long): String {
        val formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        return formatter.format(Date(epochMs))
    }

    fun formatByteCount(context: Context, bytes: Long): String {
        return Formatter.formatShortFileSize(context, bytes.coerceAtLeast(0L))
    }

    fun formatPageCount(context: Context, count: Int): String {
        return if (count == 1) {
            context.getString(R.string.project_page_count_one, count)
        } else {
            context.getString(R.string.project_page_count_other, count)
        }
    }
}
