package com.akshay.meetwm.ui.chatListFragment

import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akshay.meetwm.R
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.model.MessageData
import com.akshay.meetwm.ui.SharedPref
import com.akshay.meetwm.ui.chatActivity.ChatActivity
import com.bumptech.glide.Glide
import java.util.*
import kotlin.collections.ArrayList

class ChatListAdapter(private val list : ArrayList<ChatAndMessages>) : RecyclerView.Adapter<ChatListAdapter.ViewHolder>(){

    lateinit var context : Context
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.chat_name)
        val dpImageView : ImageView = itemView.findViewById(R.id.chat_image_view)
        val lastMessage : TextView = itemView.findViewById(R.id.chat_last_message)
        val lastMessageTime : TextView = itemView.findViewById(R.id.chat_last_message_time)
        val chatLinearLayout : LinearLayout = itemView.findViewById(R.id.chatLinearLayout)
        
        fun bind(cur : ChatAndMessages) = with(itemView){

            name.text = cur.chat.name
            lastMessage.text = cur.messages.last().data
            lastMessageTime.text = getTimeFormat(context, cur.messages.last().send_timestamp.toLong())
            Glide.with(context).load(cur.chat.dp_url).into(dpImageView)

            chatLinearLayout.setOnClickListener {
                val myUID = SharedPref(context).getUserID()
                val intent = Intent(context.applicationContext, ChatActivity::class.java)
                intent.putExtra("name", cur.chat.name)
                intent.putExtra("chatUID", cur.chat.uid)
                intent.putExtra("myUID", myUID)
                intent.putExtra("chatNumber", cur.chat.number)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        val viewHolder = ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_chat,parent, false))
        return viewHolder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cur = list[position]
        holder.bind(cur)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun update(newList : List<ChatAndMessages>){
        list.clear()
        list.addAll(newList)

        notifyDataSetChanged()
    }

    fun getTimeFormat(context: Context, timeStamp: Long) : String{
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = timeStamp

        val curTime = Calendar.getInstance()

        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "d MMMM"
        val HOURS : Long = 60 * 60 * 60

        if(curTime.get(Calendar.DATE) == smsTime.get(Calendar.DATE)){
            return DateFormat.format(timeFormatString, smsTime).toString()
        } else if (curTime.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday"
        } else if (curTime.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("yyyy", smsTime).toString();
        }
    }

}