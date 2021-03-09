package com.example.mycloud.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mycloud.R
import com.example.mycloud.utils.ShowToast

class JumpTest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jump_test)

        ShowToast(this,"登录成功")
    }
}