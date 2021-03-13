package com.example.mycloud.utils

import android.app.Activity
import android.widget.Toast

fun ShowToast(context:Activity, texts:String){
    Toast.makeText(context,texts,Toast.LENGTH_SHORT).show()
}