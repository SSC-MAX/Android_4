package com.example.mycloudtest.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    fun insertUser(user:Users)

    @Query("SELECT * FROM Users")
    fun loadAllUsers():List<Users>

    @Query("UPDATE Users SET encryption_password = :enPassword WHERE account = :account")
    fun update(enPassword:String, account:String)
}