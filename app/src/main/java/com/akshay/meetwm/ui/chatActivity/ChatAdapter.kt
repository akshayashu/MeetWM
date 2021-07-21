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
import android.text.format.DateFormat
import android.widget.ImageView
import java.util.*
import kotlin.collections.ArrayList

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
        val time = getTimeFormat(context, cur.send_timestamp.toLong())
        holder.messageTime.text = "$time"

        if(cur.status == "sent"){
            holder.messageStatus.setImageResource(R.drawable.sent_message_icon)
        }else if(cur.status == "received") {
            holder.messageStatus.setImageResource(R.drawable.received_message_icon)
        }else if(cur.status == "seen") {
            holder.messageStatus.setImageResource(R.drawable.seen_message_icon)
        }
    }

    override fun getItemCount(): Int {
        if(messageList.size == 0){
            return 0;
        }
        return messageList.first().messages.size
    }

    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val mainMessage = itemView.findViewById<TextView>(R.id.messageTextView)
        val messageTime = itemView.findViewById<TextView>(R.id.messageTime)
        val messageStatus = itemView.findViewById<ImageView>(R.id.messageStatus)
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

    fun getTimeFormat(context: Context, timeStamp: Long) : String{
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = timeStamp

        val curTime = Calendar.getInstance()

        val timeFormatString = "h:mm aa"
        val dateTimeFormatString = "EEEE, MMMM d, h:mm aa"
        val HOURS : Long = 60 * 60 * 60

        if(curTime.get(Calendar.DATE) == smsTime.get(Calendar.DATE)){
            return "Today " + DateFormat.format(timeFormatString, smsTime)
        } else if (curTime.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday " + DateFormat.format(timeFormatString, smsTime);
        } else if (curTime.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }

}