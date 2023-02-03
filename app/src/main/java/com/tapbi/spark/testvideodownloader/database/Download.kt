package com.tapbi.spark.testvideodownloader.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "downloads_table")
data class Download(
    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "total_size")
    var totalSize: Long,

    var thumbImageLink: String = ""

) {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "downloaded_percent")
    var downloadedPercent: Double = 0.0

    @ColumnInfo(name = "downloaded_size")
    var downloadedSize: Long = 0L

    @ColumnInfo(name = "downloaded_path")
    lateinit var downloadedPath: String

    @ColumnInfo(name = "media_type")
    lateinit var mediaType: String

    @Ignore
    var isSelected: Boolean = false
}