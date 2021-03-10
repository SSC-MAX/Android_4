package com.example.mycloud.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.example.mycloud.R
import com.example.mycloud.userPages.MainPage
import com.example.mycloud.utils.ShowToast
import com.example.mycloudtest.database.AppDatabase
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlin.concurrent.thread

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        val loginDao = AppDatabase.getDatabase(this).userDao()

        //使用SharedPreferences实现记住密码功能
        val prefs = getPreferences(Context.MODE_PRIVATE)
        val isRemember = prefs.getBoolean("remember_password", false)
        if (isRemember){
            val account = prefs.getString("account", "")
            val password = prefs.getString("password","")
            accountEdit_L.setText(account)
            passwordEdit_L.setText(password)
            remeberpassword.isChecked = true
        }

        //登录点击事件
        button_Login.setOnClickListener {
            val account = accountEdit_L.text.toString()
            val password = passwordEdit_L.text.toString()
            var check = false
            thread {
                //检测账号密码是否匹配
                Log.d("Login","开始检测")
               for (users in loginDao.loadAllUsers()){
                   if (account.equals(users.account) && password.equals(users.password)){
                       Log.d("Login","检测到了")
                       check = true
                       break
                   }
               }
                Log.d("Login","check的值为:$check")
                if (check){
                    //账号与密码匹配成功
                    Log.d("Login","通过检测")
                    val editor = prefs.edit()
                    //若点击了记住密码，则将账号与密码保存至SharedPreference
                    if (remeberpassword.isChecked){
                        editor.putBoolean("remember_password", true)
                        editor.putString("account", account)
                        editor.putString("password", password)
                    }else{
                        editor.clear()
                    }
                    editor.apply()
                    editor.commit()
                    //启动用户主页面
                    val intent = Intent(this, MainPage::class.java)
                    startActivity(intent)
                }else{
                    //账号与密码不匹配，则开启主线程并在其中弹出AlertDialog
                    Log.d("Login","检测到了错误")
                    runOnUiThread {
                        AlertDialog.Builder(this).apply {
                            setTitle("提示信息")
                            setMessage("账号或密码错误，请检查后重新输入")
                            setCancelable(false)
                            setPositiveButton("好的"){ dialog, which ->
                            }
                            show()
                        }
                        accountEdit_L.setText("")
                        passwordEdit_L.setText("")
                    }
                }
            }


        }

    }
}