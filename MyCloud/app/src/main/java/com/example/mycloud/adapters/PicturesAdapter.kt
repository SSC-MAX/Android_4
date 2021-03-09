package com.example.mycloud.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mycloud.R
import com.example.mycloud.activities.JumpTest
import com.example.mycloud.beans.Pictures
import com.example.mycloud.userPages.MainPage
import com.example.mycloud.userPages.SubFolder
import kotlinx.android.synthetic.main.layout_preview_picture.view.*


class PicturesAdapter(val pictureslist:ArrayList<Pictures>)  : RecyclerView.Adapter<PicturesAdapter.InnerViewHolder>(){

    

    inner class InnerViewHolder(view:View) : RecyclerView.ViewHolder(view){
        val picturesImage: ImageView = view.findViewById(R.id.imageView_picturesImage)
        val picturesName : TextView  = view.findViewById(R.id.textView_picturesName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InnerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pictures, parent, false)
        return InnerViewHolder(view)
    }

    override fun onBindViewHolder(holder: InnerViewHolder, position: Int) {
        val picture = pictureslist[position]
        holder.picturesImage.setImageBitmap(picture.icon)
        holder.picturesName.text = picture.name
        holder.itemView.setOnClickListener{
            val mycontext = holder.itemView.context
            if (picture.name.equals("我的图片")){
                val intent = Intent(mycontext, SubFolder::class.java)
                mycontext.startActivity(intent)
            }else{
                val previewPictures = LayoutInflater.from(mycontext).inflate(R.layout.layout_preview_picture, null)
                Glide.with(mycontext).load(picture.icon).into(previewPictures.imageView_PreviewImage)
                AlertDialog.Builder(mycontext).apply {
                    setTitle("查看图片")
                    setView(previewPictures)
                    setNegativeButton("关闭"){dialog, which ->}
                    show()
                }
            }
        }
    }

    override fun getItemCount() = pictureslist.size
}