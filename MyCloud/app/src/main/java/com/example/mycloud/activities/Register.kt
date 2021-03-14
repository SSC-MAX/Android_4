package com.example.mycloud.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.mycloud.R
import com.example.mycloud.userPages.MainPage
import com.example.mycloud.utils.Checks
import com.example.mycloud.utils.*
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
                GetToast.ShowToast(this,"账号或密码不能为空")
            }else{
                if (!input_password.equals(input_passwordEnsure)){
                    GetToast.ShowToast(this,"两次输入的密码不同，请重新输入")
                }else{
                    thread {
                        if (Checks.checkAccountUnique(input_account, myDao)) {
                            var user = Users(input_account, input_password,"")
                            myDao.insertUser(user)
                            runOnUiThread {
                                GetToast.ShowToast(this, "注册成功")
                                Login.LoginUserStored = input_account
                                val intent_login = Intent()
                                intent_login.setClass(this, MainPage::class.java)
                                //将新的activity置为栈顶
                                intent_login.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                startActivity(intent_login)
                                finish()
                            }
                        } else {
                            runOnUiThread {
                                AlertDialog.Builder(this).apply {
                                    setTitle("提示信息")
                                    setMessage("该账号已被注册，请重新输入")
                                    setCancelable(false)
                                    setNegativeButton("确认") { dialog, which -> }
                                    show()
                                }
                                //清空账号
                                accountEdit_R.setText("")
                            }
                        }
                    }
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