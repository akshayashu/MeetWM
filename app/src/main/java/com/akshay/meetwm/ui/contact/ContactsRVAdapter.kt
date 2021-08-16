package com.akshay.meetwm

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.akshay.meetwm.model.Contact
import com.akshay.meetwm.ui.chatActivity.ChatActivity
import com.akshay.meetwm.ui.SharedPref
import com.bumptech.glide.Glide

class ContactsRVAdapter(private val listener: IContactsRVAdapter) : RecyclerView.Adapter<ContactsRVAdapter.NoteViewModel>() {

    private val allContacts = ArrayList<Contact>()
    private lateinit var context : Context

    inner class NoteViewModel(itemView : View) : RecyclerView.ViewHolder(itemView){
        val name: TextView = itemView.findViewById(R.id.name)
        val number: TextView = itemView.findViewById(R.id.contactNumber)
        val deleteBtn: ImageView = itemView.findViewById(R.id.deleteBtn)
        val status : TextView = itemView.findViewById(R.id.status)
        val dpImageView : ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewModel {
        context = parent.context
        val viewHolder = NoteViewModel(LayoutInflater.from(parent.context).inflate(R.layout.item_contact,parent, false))
        viewHolder.deleteBtn.setOnClickListener {
            listener.onItemClick(allContacts[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: NoteViewModel, position: Int) {
        val cur = allContacts[position]

        holder.name.text = cur.display_name
        holder.number.text = cur.number
        holder.status.text = cur.status
        Glide.with(context).load(cur.dp_url).into(holder.dpImageView)

        holder.name.setOnClickListener {
            val myUID = SharedPref(context).getUserID()
            val intent = Intent(context.applicationContext, ChatActivity::class.java)
            intent.putExtra("name", cur.display_name)
            intent.putExtra("chatUID", cur.uid)
            intent.putExtra("myUID", myUID)
            intent.putExtra("chatNumber", cur.number)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return allContacts.size
    }

    fun update(newList : List<Contact>){
        allContacts.clear()
        allContacts.addAll(newList)

        notifyDataSetChanged()
    }
}

interface IContactsRVAdapter{
    fun onItemClick(contact: Contact)
}