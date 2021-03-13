package com.example.mycloud.adapters

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.mycloud.activities.Login
import com.example.mycloud.activities.Register

import com.example.mycloud.beans.Pictures
import com.example.mycloud.userPages.MainPage
import com.example.mycloud.userPages.SubFolder
import com.example.mycloud.utils.Checks
import com.example.mycloud.utils.ShowToast
import com.example.mycloudtest.database.AppDatabase
import kotlinx.android.synthetic.main.layout_encryption.view.*
import kotlinx.android.synthetic.main.layout_preview_picture.view.*
import kotlin.concurrent.thread


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
            if (picture.id.equals("root")){
                val encryption_Layout = LayoutInflater.from(mycontext).inflate(R.layout.layout_encryption, null)
                val encryption_enter_layout = LayoutInflater.from(mycontext).inflate(R.layout.layout_encryption_enter, null)
                val myDao = AppDatabase.getDatabase(mycontext).userDao()
                //未设置密码则要求设置密码

                if (!Login.encryptionPassword_L.equals("") || Register.encryptionPassword_R.equals("")){
                    ShowToast(mycontext as Activity, Login.LoginUserStored)
                    AlertDialog.Builder(mycontext).apply {
                        setView(encryption_Layout)
                        setPositiveButton("确认"){dialog, which->
                            val encryptionPassword = encryption_Layout.editText_encryption.text.toString()
                            thread {
                                if (Register.ifRegister){
                                    myDao.update(encryptionPassword, Login.LoginUserStored)
                                    Login.encryptionPassword_L = encryptionPassword
                                }else{
                                    myDao.update(encryptionPassword, Register.RegisterUserStored)
                                    Register.encryptionPassword_R = encryptionPassword
                                }

                            }
                        }
                        setNegativeButton("取消"){dialog, which ->}
                        show()
                    }
                }else{
                    //设置过密码直接弹出AlertDialog要求输入密码
                    AlertDialog.Builder(mycontext).apply {
                        setView(encryption_enter_layout)
                        setNegativeButton("取消"){dialog, which ->}
                        setPositiveButton("确认"){dialog,which ->
                            ShowToast(mycontext as Activity,"欢迎进入加密文件")
                            val inputPassword = encryption_enter_layout.editText_encryption.text.toString()
                            thread {
                                if (Checks.checkPassword(inputPassword, myDao)){
                                    mycontext.runOnUiThread {
                                        val intent = Intent(mycontext, SubFolder::class.java)
                                        //缓存上的处理
                                        intent.putExtra("pic_id",picture.id)
                                        mycontext.startActivity(intent)
                                    }

                                }else{
                                    mycontext.runOnUiThread {
                                        ShowToast(mycontext,"密码错误，请重试")
                                    }
                                }
                            }
                        }
                        show()
                    }
                }
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