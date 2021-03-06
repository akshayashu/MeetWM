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
import android.util.Log
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.akshay.meetwm.model.MessageData
import java.lang.Integer.min
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max

class ChatAdapter(val context: Context, private val chatInterface: ChatAdapterInterface) :
    PagingDataAdapter<MessageData, ChatAdapter.ViewHolder>(DIFF_CALLBACK) {

    private var MSG_TYP_LEFT = 0
    private var MSG_TYP_Right = 1
    private var lastMessagePosition = 0

    companion object{
        var mClickListener: ChatAdapterInterface? = null
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<MessageData>(){
            override fun areItemsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
                return oldItem == newItem
            }

        }
    }

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
        val cur = getItem(position)
        if(cur == null){
            Log.d("RECYCLERVIEW NULL is at", position.toString())
        }else {
            // max position element
            Log.d("ITEMS BINDING ", position.toString()+ " - " + cur.data)
            lastMessagePosition = max(lastMessagePosition, position)

            holder.bindTo(cur)
            mClickListener?.lastMessagePositionNumber(lastMessagePosition)
        }
    }


    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val mainMessage: TextView = itemView.findViewById(R.id.messageTextView)
        private val messageTime: TextView = itemView.findViewById(R.id.messageTime)
        private val messageStatus: ImageView = itemView.findViewById(R.id.messageStatus)

        fun bindTo(cur : MessageData){
            mainMessage.text = cur.data
            val time = getTimeFormat(context, cur.send_timestamp.toLong())
            val timeOnly = time.takeLast(8)
            messageTime.text = timeOnly

            if(cur.status == "sent"){
                messageStatus.setImageResource(R.drawable.sent_message_icon)
            }else if(cur.status == "received") {
                messageStatus.setImageResource(R.drawable.received_message_icon)
            }else if(cur.status == "seen") {
                messageStatus.setImageResource(R.drawable.seen_message_icon)
            }
        }
    }
    interface ChatAdapterInterface{
        fun getTopTimeStampOfChat(messageTime : String)
        fun lastMessagePositionNumber(pos : Int)
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
        super.onViewAttachedToWindow(holder)

        mClickListener = chatInterface
        val cur = getItem(holder.absoluteAdapterPosition)
        if(cur == null){
            Log.d("RECYCLERVIEW NULL is at", "HERE")
        }else
            mClickListener!!.getTopTimeStampOfChat(cur.send_timestamp)

    }

    override fun getItemViewType(position: Int): Int {
        val pref = SharedPref(context)
        if(getItem(position)?.sender_uid == pref.getUserID()){
            return MSG_TYP_Right
        }else{
            return MSG_TYP_LEFT
        }
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