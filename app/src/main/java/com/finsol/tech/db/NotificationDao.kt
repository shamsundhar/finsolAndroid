package com.finsol.tech.db

import androidx.room.*

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_table ORDER BY id DESC")
    fun getAll(): List<Notification>

    @Query("SELECT * FROM notification_table WHERE userID LIKE :userID LIMIT 1")
    suspend fun findByRoll(userID: Int): Notification

    @Query("SELECT * FROM notification_table WHERE read = 0")
    suspend fun getALlUnreadMessages():  List<Notification>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(notification: Notification)

    @Delete
    suspend fun delete(notification: Notification)

    @Query("DELETE FROM notification_table")
    suspend fun deleteAll()

    @Query("UPDATE notification_table SET read=:read WHERE id = :id")
    fun update(read: Boolean?, id: Int)
}