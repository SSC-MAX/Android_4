package com.example.mycloud.utils

import android.app.Activity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mycloud.adapters.PicturesAdapter
import com.example.mycloud.beans.Pictures


object RecyclerViewSettings{
    @JvmStatic
    fun ShowAllItems(list:ArrayList<Pictures>, activity: Activity, recyclerView: RecyclerView){
        val layoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = layoutManager
        val adapter = PicturesAdapter(list)
        recyclerView.adapter = adapter
    }

    @JvmStatic
    fun ShowSelectedItems(searchKey:String, nullItem:ArrayList<Pictures>,list:ArrayList<Pictures>, activity: Activity, recyclerView: RecyclerView){
        val selectedList = ArrayList<Pictures>()
        var ifMiss = true
        for (items in list){
            if (items.name.contains(searchKey)){
                selectedList.add(items)
                ifMiss = false
            }
        }
        if (ifMiss){
            ShowAllItems(nullItem,activity,recyclerView)
        }else{
            ShowAllItems(selectedList,activity, recyclerView)
        }
    }


}
