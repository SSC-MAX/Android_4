package com.example.mycloud.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.mycloud.R
import kotlinx.android.synthetic.main.activity_profile.*


class Profile : AppCompatActivity()  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        textView_Profile.setText(Login.LoginUserStored)

        exit_app.setOnClickListener {
            val intent_login = Intent()
            intent_login.setClass(this, Start::class.java)
            //将新的activity置为栈顶
            intent_login.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent_login)
            finish()

        }
    }
}