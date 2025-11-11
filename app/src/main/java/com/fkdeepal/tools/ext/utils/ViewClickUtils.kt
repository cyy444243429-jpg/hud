package com.fkdeepal.tools.ext.utils

import kotlin.math.abs

object ViewClickUtils {
    private var lastClickTimeMillis = 0L

    fun isOnClickSafe(safeTimeMillis: Int = 200): Boolean {
        val current = System.currentTimeMillis()
        if (abs(current - lastClickTimeMillis) >= safeTimeMillis) {
            lastClickTimeMillis = current
            return true
        }
        return false
    }
}