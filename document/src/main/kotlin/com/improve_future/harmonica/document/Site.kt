package com.improve_future.harmonica.document

import com.improve_future.harmonica.document.view.HarmonicaView
import com.improve_future.harmonica.document.view.JarmonicaView
import com.improve_future.harmonica.document.view.MigrationMethod
import com.improve_future.harmonica.document.view.HomeView

object Site {
    val structure: Array<Any> = arrayOf(
        HomeView,
        JarmonicaView,
        HarmonicaView,
        MigrationMethod
    )
}