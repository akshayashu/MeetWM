package com.akshay.meetwm.ui.chatListFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akshay.meetwm.R
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.MessageData

class ChatListAdapter(private val list : ArrayList<ChatAndMessages>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>(){

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.name)
        val number: TextView = itemView.findViewById(R.id.contactNumber)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)
        val status : TextView = itemView.findViewById(R.id.status)
        val dpImageView : ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact,parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cur = list[position]

        holder.name.text = cur.chat.name
        holder.status.text = cur.chat.staus
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList : List<ChatAndMessages>){
        list.clear()
        list.addAll(newList)

        notifyDataSetChanged()
    }
}