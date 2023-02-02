package com.tapbi.spark.testvideodownloader.utils

import timber.log.Timber

class MyDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "(%s:%s)#%s",
            element.fileName,
            element.lineNumber,
            element.methodName
        )
    }
}