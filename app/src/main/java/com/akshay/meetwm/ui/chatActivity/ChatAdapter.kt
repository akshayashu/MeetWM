package com.akshay.meetwm.ui.chatActivity

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akshay.meetwm.R
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.ui.SharedPref

class ChatAdapter(val context: Context) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private var messageList = ArrayList<ChatAndMessages>()
    private var MSG_TYP_LEFT = 0
    private var MSG_TYP_Right = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        if(viewType == MSG_TYP_Right){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.my_message_item_layout, parent, false)
            return ViewHolder(view)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.other_message_item_layout, parent, false)
            return ViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: ChatAdapter.ViewHolder, position: Int) {
        val cur = messageList.first().messages[position]

        holder.mainMessage.text = cur.data
        holder.messageTime.text = "${cur.send_timestamp}, ${cur.status}"
    }

    override fun getItemCount(): Int {
        if(messageList.size == 0){
            return 0;
        }
        return messageList.first().messages.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val mainMessage = itemView.findViewById<TextView>(R.id.messageTextView)
        val messageTime = itemView.findViewById<TextView>(R.id.timeTextview1);
    }

    override fun getItemViewType(position: Int): Int {
        val pref = SharedPref(context)
        if(messageList.first().messages[position].sender_uid == pref.getUserID()){
            return MSG_TYP_Right
        }else{
            return MSG_TYP_LEFT
        }
    }

    fun update(newList : List<ChatAndMessages>){
        messageList.clear()
        messageList.addAll(newList)

        notifyDataSetChanged()
    }
}