package com.akshay.meetwm.ui.chatListFragment

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.akshay.meetwm.R
import com.akshay.meetwm.model.ChatAndMessages
import com.akshay.meetwm.ui.main.MainViewModel


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ChatFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private val mainViewModel: MainChatSharedViewModel by activityViewModels()
    private lateinit var viewModel : ChatListViewModel

    private var perList = ArrayList<ChatAndMessages>()
    private var list = ArrayList<ChatAndMessages>()
    var filteredList = ArrayList<ChatAndMessages>()
    lateinit var adapter : ChatListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(activity?.application!!)).get(ChatListViewModel::class.java)

        adapter = ChatListAdapter(list)
        val recyclerView = view.findViewById<RecyclerView>(R.id.chatRecyclerView)

        val linearLayoutManager = LinearLayoutManager(this.context)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = adapter

        viewModel.allChat.observe(viewLifecycleOwner, {
            perList.clear()
            list.clear()
            
            perList.addAll(it)
            list.addAll(it)
            adapter.update(it)
            Log.d("Adding from"," view model")
        })

        mainViewModel.changeHeadline.observe(viewLifecycleOwner, {
            list = perList
            Log.d("Adding from"," MainView Model")
            searchChat(it)
        })

    }

    private fun searchChat(query : String?){

        filteredList.clear()
        for (e in list){
            Log.d("Chat List name", e.chat.name)
        }
        for (e in list) {
            val name = e.chat.name
            if(name.toLowerCase().contains(query.toString())){
                Log.d("Found name", name)
                filteredList.add(e)
            }
        }
        adapter.update(filteredList)
    }

    companion object {

        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ChatFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}