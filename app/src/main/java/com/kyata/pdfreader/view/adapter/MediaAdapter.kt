package com.kyata.pdfreader.view.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kyata.pdfreader.R
import com.kyata.pdfreader.model.Media
import com.kyata.pdfreader.utils.FileUtils


class MediaAdapter(
    val context: Context,
    val list: MutableList<Media>,
    private val mediaListener: MediaListener
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var itemChecked = ArrayList<Media>()
    private var itemPos = ArrayList<TextView>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_media, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val viewHolder = holder as MyViewHolder
        val item = list[position]
        val btnUnchecked = viewHolder.itemView.findViewById<ImageView>(R.id.btnUnchecked)
        val btnChecked = viewHolder.itemView.findViewById<TextView>(R.id.btnChecked)
        try {
            Glide.with(context).load(item.image)
                .into(viewHolder.itemView.findViewById(R.id.ivPreview))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        viewHolder.itemView.findViewById<TextView>(R.id.tv_size).text =
            FileUtils.formatFileSize(item.size)
        if (item.isChecked) {
            btnChecked.text = (itemChecked.indexOf(item) + 1).toString()
            btnChecked.visibility = View.VISIBLE
            btnUnchecked.visibility = View.GONE
        } else {
            btnChecked.visibility = View.GONE
            btnUnchecked.visibility = View.VISIBLE
        }
        viewHolder.itemView.setOnClickListener {
            if (item.isChecked) {
                itemChecked.remove(item)
                itemPos.remove(btnChecked)
            } else {
                itemPos.add(btnChecked)
                itemChecked.add(item)
            }
            item.isChecked = !item.isChecked
            mediaListener.onSelected(itemChecked)
            notifyDataSetChanged()
        }

    }

    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface MediaListener {
        fun onSelected(itemChecked: ArrayList<Media>)
    }
}