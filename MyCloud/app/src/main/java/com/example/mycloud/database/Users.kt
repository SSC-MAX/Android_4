package com.example.mycloudtest.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Users(
    @PrimaryKey var account:String,
    @ColumnInfo var password:String,
    @ColumnInfo var encryption_password:String)
