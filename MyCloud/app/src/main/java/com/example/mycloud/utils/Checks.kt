
package com.example.mycloud.utils

import android.util.Log

import com.example.mycloudtest.database.UserDao
import com.example.mycloudtest.database.Users
import kotlin.concurrent.thread

object Checks {



    @JvmStatic
    fun checkAccountUnique(input: String, dao: UserDao): Boolean {
        var ifUnique = true
        for (users in dao.loadAllUsers()) {
            if (input.equals(users.account)) {
                ifUnique = false
            }
        }

        return ifUnique
    }

    @JvmStatic
    fun checkPassword(user:String,input: String, dao: UserDao): Boolean {
        var ifRight = false
        for (users in dao.loadAllUsers()) {
            if (users.account.equals(user) && input.equals(users.encryption_password)) {
                ifRight = true
            }
        }
        Log.d("Register", "ifUnique的值为: $ifRight")
        return ifRight
    }


}