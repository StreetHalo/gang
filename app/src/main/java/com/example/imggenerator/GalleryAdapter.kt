package com.example.imggenerator

import android.app.Activity
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.card_view.view.*

class GalleryAdapter(var gallery:List<Group>, val viewInterface: ViewInterface): RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {



    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.card_view,p0,false)
        val holder = ViewHolder(view)
        view.setOnClickListener {
            viewInterface.setGroup(holder.adapterPosition)
             }
        return holder
    }

    override fun getItemCount(): Int {
        return gallery.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        Glide.with(p0.itemView.context)
            .load(gallery[p1].groupImg)
            .into(p0.img)
    }

    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img = itemView.image

    }


}

