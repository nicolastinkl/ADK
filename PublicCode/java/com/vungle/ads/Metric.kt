package com.vungle.ads

import com.vungle.ads.internal.protos.Sdk

internal abstract class Metric(val metricType: Sdk.SDKMetric.SDKMetricType) {
    var meta: String? = null

    abstract fun getValue() : Long
}

internal class SingleValueMetric(metricType: Sdk.SDKMetric.SDKMetricType) : Metric(metricType) {
    var value: Long? = null

    fun markTime() {
        value = System.currentTimeMillis()
    }

    fun addValue(add: Long) {
        value = (value ?: 0) + add
    }

    override fun getValue() = value ?: 0
}

internal abstract class DualValueMetric(metricType: Sdk.SDKMetric.SDKMetricType) : Metric(metricType) {
    var valueFirst: Long? = null
    var valueSecond: Long? = null
}

internal open class TimeIntervalMetric(metricType: Sdk.SDKMetric.SDKMetricType) :
    DualValueMetric(metricType) {
    open fun markStart() {
        valueFirst = getCurrentTime()
    }

    open fun markEnd() {
        valueSecond = getCurrentTime()
    }

    fun calculateIntervalDuration() =
        (valueSecond ?: System.currentTimeMillis()) - (valueFirst ?: System.currentTimeMillis())

    private fun getCurrentTime() = System.currentTimeMillis()
    override fun getValue() = calculateIntervalDuration()
}

internal class OneShotTimeIntervalMetric(metricType: Sdk.SDKMetric.SDKMetricType) :
    TimeIntervalMetric(metricType) {
    internal fun alreadyMetered(): Boolean = valueFirst != null && valueSecond != null
    private var alreadyLogged = false

    override fun markStart() {
        if (valueFirst == null) {
            super.markStart()
        }
    }

    override fun markEnd() {
        if (valueSecond == null) {
            super.markEnd()
        }
    }

    fun isLogged(): Boolean {
        return alreadyLogged
    }

    fun markLogged() {
        alreadyLogged = true
    }
}