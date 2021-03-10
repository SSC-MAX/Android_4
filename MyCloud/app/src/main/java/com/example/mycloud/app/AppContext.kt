package com.example.mycloud.app

import android.app.Application
import com.example.mycloud.beans.Pictures

class AppContext : Application() {

    companion object {
        val cacheData: HashMap<String, List<Pictures>> = HashMap()
    }
}