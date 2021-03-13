
package com.example.mycloud.utils

import android.util.Log

import com.example.mycloudtest.database.UserDao
import kotlin.concurrent.thread

object Checks {

    @JvmStatic
    fun checkAccountUnique(input: String, dao: UserDao): Boolean {
        var ifUnique = true
        Log.d("Register", "进行了检查")

        for (users in dao.loadAllUsers()) {
            if (input.equals(users.account)) {
                Log.d("Register", "正在检查 ${users.account} 与 $input")
                ifUnique = false
                Log.d("Register", "检查完毕，ifUnique的值为: $ifUnique")
            }
        }
        Log.d("Register", "ifUnique的值为: $ifUnique")
        return ifUnique
    }

    @JvmStatic
    fun checkPassword(input: String, dao: UserDao): Boolean {
        var ifUnique = true
        Log.d("Register", "进行了检查")

        for (users in dao.loadAllUsers()) {
            if (input.equals(users.encryption_password)) {
                Log.d("Register", "正在检查 ${users.account} 与 $input")
                ifUnique = false
                Log.d("Register", "检查完毕，ifUnique的值为: $ifUnique")
            }
        }
        Log.d("Register", "ifUnique的值为: $ifUnique")
        return ifUnique
    }

    @JvmStatic
    fun checkIfUnEncryption(dao:UserDao) : Boolean{
        thread {

        }
        var ifUnEncryption = true

        for (users in dao.loadAllUsers()){
            if (users.encryption_password.equals("")){
                ifUnEncryption = false
            }
        }
        return ifUnEncryption
    }
}