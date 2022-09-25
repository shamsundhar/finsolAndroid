package com.finsol.tech.db

import androidx.room.*

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_table")
    fun getAll(): List<Notification>

    @Query("SELECT * FROM notification_table WHERE userID LIKE :userID LIMIT 1")
    suspend fun findByRoll(userID: Int): Notification

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)

    @Query("DELETE FROM notification_table")
    suspend fun deleteAll()
}