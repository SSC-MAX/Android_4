package com.example.mycloud.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.mycloud.R
import com.example.mycloud.utils.ShowToast
import com.example.mycloudtest.database.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.concurrent.thread


class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()

        val myDao = AppDatabase.getDatabase(this).userDao()

        button_Register.setOnClickListener {
            val input_account = accountEdit_R.text.toString()
            val input_password = passwordEdit_R.text.toString()
            val input_passwordEnsure = passwordEnsure.text.toString()
            if (input_account.isEmpty() || input_password.isEmpty() || input_passwordEnsure.isEmpty()){
                ShowToast(this,"账号或密码不能为空")
            }else{
                if (!input_password.equals(input_passwordEnsure)){
                    ShowToast(this,"两次输入的密码不同，请重新输入")
                }else{
                      thread {
                           var user = Users(input_account,input_password)
                           myDao.insertUser(user)
                           ShowToast(this,"注册成功")
                      }
                }
            }
        }

        button_See.setOnClickListener {
            thread {
                for (users in myDao.loadAllUsers()){
                    Log.d("Register",users.toString())
                }
            }
        }
    }

    private fun checkUnique(input:String, dao:UserDao) : Boolean{
        Log.d("Register","进行了检查")
        for (users in dao.loadAllUsers()){
            if (input.equals(users.account)){
                return false
            }
        }
        return true
    }
}