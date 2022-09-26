package com.finsol.tech.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_table")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "receivedTimeStamp") var receivedTimeStamp: Long?,
    @ColumnInfo(name = "userID") val userID: Int?,
    @ColumnInfo(name = "responseMessageType") val responseMessageType: String?,
    @ColumnInfo(name = "responseMessage") val responseMessage: String?,
    @ColumnInfo(name = "responseCode") val responseCode: Int?,
    @ColumnInfo(name = "read") var read: Boolean = false
)