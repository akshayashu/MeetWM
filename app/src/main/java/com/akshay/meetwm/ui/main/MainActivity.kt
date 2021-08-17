package com.akshay.meetwm.ui.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.akshay.meetwm.R
import com.akshay.meetwm.model.*
import com.akshay.meetwm.socket.SocketInstance
import com.akshay.meetwm.ui.SharedPref
import com.akshay.meetwm.ui.callActivity.CallTestActivity
import com.akshay.meetwm.ui.chatListFragment.MainChatSharedViewModel
import com.akshay.meetwm.ui.contact.ContactActivity
import com.akshay.meetwm.ui.signInActivity.SignInActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.gson.Gson
import io.socket.client.Socket
import io.socket.emitter.Emitter
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var permissions = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.WRITE_CONTACTS)
    private val requestCode = 1
    lateinit var pref : SharedPref

    private lateinit var mSocket : Socket

    private val mainViewModel: MainChatSharedViewModel by viewModels()

    private lateinit var viewModel: MainViewModel
    private var list = ArrayList<Contact>()
    private var contactMap = HashMap<String, Contact>()

    var firebaseRef = Firebase.database.getReference("users")

    override fun onStart() {
        super.onStart()
        if(!isPermissionGranted()){
            askPermissions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        //set up view
        viewModel = ViewModelProvider(this,
               ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(MainViewModel::class.java)

        val appTitle = findViewById<TextView>(R.id.app_title)
        val searchButton = findViewById<ImageView>(R.id.search_button)
        val searchText = findViewById<EditText>(R.id.searchEditText)

        searchButton.setOnClickListener {
            searchButton.visibility = View.GONE
            appTitle.visibility = View.GONE
            searchText.visibility = View.VISIBLE
        }

        searchText.setOnFocusChangeListener{view, hasFocus ->
            if(!hasFocus) {
                searchButton.visibility = View.VISIBLE
                appTitle.visibility = View.VISIBLE
                searchText.visibility = View.GONE
            }
        }

        searchText.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                Log.d("TEXT WATCHER 2", p0.toString())
                if(p0.toString() != ""){
                    mainViewModel.changeQuery(p0.toString().toLowerCase())
                }else{
                    mainViewModel.changeQuery(p0.toString())
                }

            }

        })

        viewModel.getContact()
        viewModel.list.observe(this, {
            list = it
            updateList()
        })

        val intent = Intent("my-event")
        configureTabLayout()

        showPref()

        Firebase.initialize(this)

        fab.setOnClickListener {
            val intent = Intent(this, ContactActivity::class.java)
            startActivity(intent)
        }

        try {
            val socketInstance = application as SocketInstance
            mSocket = socketInstance.getSocketInstance()

            mSocket.connect()

            mSocket.on(Socket.EVENT_CONNECT, onConnect)
            val myUID = SharedPref(applicationContext).getUserID()
            mSocket.emit("join", myUID)
        }catch (e : Exception){
            Log.d("SOCKET EXCEPTION", e.localizedMessage)
        }

        // someone has received my message
        mSocket.on("received"){
            // update msg receive time
            if(it != null) {
                val data = it[0]

                val receivedData = Gson().fromJson(data.toString(), ReceivedMessage::class.java)
                viewModel.updateReceivedMessageTime(receivedData.receivedTime, receivedData.message_id)
                Log.d("MESSAGE", "Received $receivedData")
            }
        }

        // someone has seen my message
        mSocket.on("seen"){
            // update msg receive time
            if(it != null) {
                val data = it[0]
                val seenData = Gson().fromJson(data.toString(), SeenMessage::class.java)
                Log.d("MESSAGE", "Seen $seenData")

                val list = viewModel.getUnseenMessageId(seenData.chat_uid)
                Log.d("SIZE OF UNSEEN MSGS", list.size.toString());
                for(ele in list){
                    viewModel.updateSeenMessageTime(seenData.seenTime, ele.id)
                }
            }
        }

        //I've received a new message
        mSocket.on("message"){
            if (it != null) {
                val data = it[0]
                val time = System.currentTimeMillis().toString()

                Log.d("SOCKET MESSAGE", data.toString())
                val messageData = Gson().fromJson(data.toString(), MessageData::class.java)
                viewModel.insertMessage(messageData)

                // updating unseen msg count
                val prevUnCountMsg = viewModel.getUnseenMessageCount(messageData.sender_uid).toInt()
                viewModel.updateUnseenMessageCount(messageData.sender_uid, (prevUnCountMsg + 1).toString())

                // emitting the other person that I've received his/her message
                Log.d("MY IDDDDD", pref.getUserID()!!)
                val receivedMessage = ReceivedMessage(messageData.chat_uid, pref.getUserID()!!, messageData.id,time)
                mSocket.emit("receivedMessage", Gson().toJson(receivedMessage))

                intent.putExtra("data", "newMessage,${messageData.chat_uid},${messageData.id}");
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            }
        }

        //section for incoming calls
        firebaseRef.child(pref.getUserID().toString())
            .child("incoming").addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.value.toString().trim() == ""){
                        return
                    }else {
                        val contact = contactMap[snapshot.value.toString()]!!
                        val intent = Intent(this@MainActivity, CallTestActivity::class.java)
                        intent.putExtra("myId", pref.getUserID().toString())
                        intent.putExtra("callerId", contact.uid)
                        // one case has to be handled, when unknown person will call and his contact is not saved
                        intent.putExtra("friendUserName", contact.display_name)
                        intent.putExtra("photoURL", contact.dp_url)
                        intent.putExtra("callType", "incoming")
                        Toast.makeText(this@MainActivity, "Incoming call", Toast.LENGTH_SHORT).show()
                        startActivity(intent)
//                        viewModel.getCallingContact(snapshot.value.toString())

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })

    }

    private fun updateList() {
        for(item in list) {
            contactMap[item.uid] = item
            viewModel.insertContact(item)
            Log.d("Contacts", "${item.display_name} , ${item.number}, ${item.id}")
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_chat_menu, menu)
        Log.d("MENU is", "Created")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.logout_btn -> {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this, SignInActivity::class.java))
                finish()
                return true
            }
            R.id.settings_btn ->{
                Toast.makeText(this, "Settings item", Toast.LENGTH_SHORT).show()
                Log.d("settings", "pressed")
                return true
            }
            R.id.newGroup_btn ->{
                Toast.makeText(this, "New Group item", Toast.LENGTH_SHORT).show()
                Log.d("new group", "pressed")
                return true
            }
            else ->{
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun showPref() {
        pref = SharedPref(this)

        //setting default values for calling
        firebaseRef.child(pref.getUserID().toString()).child("incoming").setValue("")
        firebaseRef.child(pref.getUserID().toString()).child("callStatus").setValue("")
        firebaseRef.child(pref.getUserID().toString()).child("isAvailable").setValue(true)
        firebaseRef.child(pref.getUserID().toString()).child("connId").setValue("")
    }

    private fun configureTabLayout() {

        val adapter = PageAdapter(supportFragmentManager, lifecycle)
        pager.adapter = adapter


        TabLayoutMediator(tabLayout, pager){tab, position ->
            when(position){
                0 -> {
                    tab.text = "Chat"
                }
                1 -> {
                    tab.text = "Status"
                }
                2 -> {
                    tab.text = "History"
                }
            }
        }.attach()
    }

    private fun isPermissionGranted(): Boolean {

        permissions.forEach {
            if(ActivityCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED){
                return false
            }
        }
        return true
    }

    private fun askPermissions() {
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private val onConnect = Emitter.Listener { Log.d("Tag-Socket", "Socket Connected!") }
    private val onConnectError = Emitter.Listener { Log.d("Tag-Socket", "Socket ERROR !")}
    private val onConnectTimeout = Emitter.Listener { runOnUiThread { Log.d("Tag-Socket", "Socket Timeout !") } }
    private val onDisconnect = Emitter.Listener { runOnUiThread {Log.d("Tag-Socket", "Socket Disconnected!") } }

    //handle editText focus
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val v: View? = currentFocus
            if (v is EditText) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    Log.d("focus", "touchevent")
                    v.clearFocus()
                    val imm: InputMethodManager =
                        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0)
                }
            }
        }
        return super.dispatchTouchEvent(event)
    }

    override fun onDestroy() {
        super.onDestroy()
        val pref = SharedPref(this)
//        mSocket.emit("disconnect", pref.getUserID())
        Log.d("SOCKET", "Destroyed")
    }

}