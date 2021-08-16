package com.akshay.meetwm.ui.chatActivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akshay.meetwm.R
import com.akshay.meetwm.model.*
import com.akshay.meetwm.socket.SocketInstance
import com.akshay.meetwm.ui.callActivity.CallTestActivity
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import de.hdodenhof.circleimageview.CircleImageView
import io.socket.client.Socket
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import retrofit2.http.OPTIONS
import java.util.*
import kotlin.collections.ArrayList


class ChatActivity : AppCompatActivity() {

    private lateinit var mSocket : Socket
    private lateinit var username : String
    private lateinit var chatUID : String
    private lateinit var myUID : String
    private lateinit var chatNumber: String
    private var listOfMessage = ArrayList<MessageData>()

    lateinit var editText : TextView
    lateinit var recyclerView : RecyclerView
    lateinit var adapter : ChatAdapter
    lateinit var callBtn: ImageView
    lateinit var dateTextView : TextView
    lateinit var linearLayoutManager : LinearLayoutManager
    lateinit var dpImageView: CircleImageView
    lateinit var chatName : TextView

    lateinit var currentContact : Contact

    lateinit var viewModel: ChatViewModel
    var firebaseRef = Firebase.database.getReference("users")

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        username = intent.getStringExtra("name")!!
        chatUID = intent.getStringExtra("chatUID")!!
        myUID = intent.getStringExtra("myUID")!!
        chatNumber = intent.getStringExtra("chatNumber")!!

        // views
        editText = findViewById(R.id.msgEditText)
        recyclerView = findViewById(R.id.messageRecyclerView)
        dpImageView = findViewById(R.id.chatDp)
        chatName = findViewById(R.id.chatUserName)

        callBtn = findViewById(R.id.callBtn)
        dateTextView = findViewById(R.id.dateText)

        val itemClicked = object : ChatAdapter.ChatAdapterInterface{
            override fun onItemClicked(messageTime: String) {

                dateTextView.text = getTimeFormat(messageTime.toLong())
            }
        }
        adapter = ChatAdapter(this, itemClicked)


        //recyclerView
        linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        recyclerView.addOnScrollListener(listener)

        try {
            val socketInstance = application as SocketInstance
            mSocket = socketInstance.getSocketInstance()

            mSocket.emit("join", myUID)
        }catch (e : Exception){
            Log.d("SOCKET EXCEPTION", e.localizedMessage)
        }
        val time = System.currentTimeMillis().toString()
        val seenMessage = SeenMessage(chatUID, myUID, "random id", time);
        mSocket.emit("seenMessage", Gson().toJson(seenMessage))

        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,
            IntentFilter("my-event")
        )

        val chatViewModelFactory = ChatViewModelFactory(application, chatUID);
        viewModel = ViewModelProvider(this, chatViewModelFactory).get(ChatViewModel::class.java)

        viewModel.getContact(chatUID)
        viewModel.currentContact.observe(this, {
            currentContact = it
            Glide.with(this).load(currentContact.dp_url).into(dpImageView)
        })
        // observing only list of messages
        lifecycleScope.launch {
            viewModel.allMessages.collectLatest { list ->
                Log.d("LIST of Messages", list.toString())
                adapter.submitData(list)
            }
        }

        callBtn.setOnClickListener {

            val intent = Intent(this, CallTestActivity::class.java)
            intent.putExtra("myId", myUID)
            intent.putExtra("callerId", chatUID)
            intent.putExtra("friendUserName", currentContact.display_name)
            intent.putExtra("photoURL", currentContact.dp_url)
            intent.putExtra("callType", "outgoing")

            firebaseRef.child(chatUID)
                .child("isAvailable")
                .addListenerForSingleValueEvent(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.value.toString() == "true"){
                            startActivity(intent)
                            Toast.makeText(this@ChatActivity, "Calling", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@ChatActivity, "He/She is busy", Toast.LENGTH_SHORT).show()
                            return
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })

        }
        chatName.text = username

        // observing the whole chatAndMessages data
//        viewModel.allChatMessages.observe(this, { list ->
//            list?.let {
//                if(list.isNotEmpty()) {
//                    adapter.update(list)
//                    listOfMessage.addAll(list)
//                    Log.d("List of messages", list.size.toString())
//                    recyclerView.smoothScrollToPosition(list.first().messages.size-1)
//                }
//            }
//
//        })


        sendBtn.setOnClickListener {
            val time = System.currentTimeMillis().toString()
            val id = myUID + time
            if(editText.text.toString().trim().isEmpty()){
                return@setOnClickListener
            }
            val myMessage = MessageData(id,"sent", chatUID, myUID, chatUID,"text_msg",
                "", "","",
                editText.text.toString().trim(), "not_yet", time,"not_yet")
//            Log.d("My message", Gson().toJson(myMessage))

            mSocket.emit("sendMessage", Gson().toJson(myMessage))

            if(currentContact != null){
                viewModel.insertChat(ChatModel(
                    chatUID, chatNumber, username,currentContact.global_name,
                    currentContact.status,"offline",
                    currentContact.dp_url, "0", true))
            }

            viewModel.insertMessage(myMessage)
            editText.text = ""
        }


    }

    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Get data from intent and update
            if(intent != null){
                val time = System.currentTimeMillis().toString()
                val chat_id  = intent.getStringExtra("data")!!.split(",")[1]
                val id  = intent.getStringExtra("data")!!.split(",")[2]
                val seenMessage = SeenMessage(chat_id, myUID, id, time);
                mSocket.emit("seenMessage", Gson().toJson(seenMessage))
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        recyclerView.clearOnScrollListeners()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver)
//        mSocket.emit("disconnect", senderUID)
        Log.d("SOCKET", "Destroyed")
    }

    private val listener = object : RecyclerView.OnScrollListener(){
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (listOfMessage.isNotEmpty()){
                val position = linearLayoutManager.findFirstVisibleItemPosition()
                Log.d("time of message", position.toString())
                val time = listOfMessage[position].send_timestamp
                dateTextView.text = getTimeFormat(time.toLong())

            }
        }
    }

    fun getTimeFormat(timeStamp: Long) : String{
        val smsTime = Calendar.getInstance()
        smsTime.timeInMillis = timeStamp

        val curTime = Calendar.getInstance()

        val dateTimeFormatString = "EEEE, MMMM d"

        if(curTime.get(Calendar.DATE) == smsTime.get(Calendar.DATE)){
            return "Today"
        } else if (curTime.get(Calendar.DATE) - smsTime.get(Calendar.DATE) == 1  ){
            return "Yesterday"
        } else if (curTime.get(Calendar.YEAR) == smsTime.get(Calendar.YEAR)) {
            return DateFormat.format(dateTimeFormatString, smsTime).toString();
        } else {
            return DateFormat.format("MMMM dd yyyy, h:mm aa", smsTime).toString();
        }
    }
}