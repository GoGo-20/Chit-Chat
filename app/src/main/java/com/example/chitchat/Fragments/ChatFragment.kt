package com.example.chitchat.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.AdapterClasses.UserAdapter
import com.example.chitchat.ModelClasses.ChatList
import com.example.chitchat.ModelClasses.users
import com.example.chitchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChatFragment : Fragment() {

    private var userAdpater: UserAdapter? = null
    private  var musers: List<users>? = null
    private var usersChatList: List<ChatList>? = null

    lateinit var recycler_view_chat_list: RecyclerView
    private var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_chat, container, false)

        recycler_view_chat_list = view.findViewById(R.id.recycler_view_chat_list)
        recycler_view_chat_list.setHasFixedSize(true)
        recycler_view_chat_list.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        usersChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (usersChatList as ArrayList).clear()

                for(dataSnapshot in snapshot.children){
                    val chatList = dataSnapshot.getValue(ChatList::class.java)

                    (usersChatList as ArrayList).add(chatList!!)
                }
                retreiveChatList()
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

        return view
    }

    private fun retreiveChatList(){
        musers = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")
        ref!!.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (musers as ArrayList).clear()

                for (dataSnapshot in snapshot.children){
                    val user = dataSnapshot.getValue(users::class.java)

                    for(eachChatList in usersChatList!!){
                        if (user!!.getUID().equals(eachChatList.getId())){
                            (musers as ArrayList).add(user!!)
                        }
                    }
                }
                userAdpater = UserAdapter(context!!, (musers as ArrayList<users>), true)
                recycler_view_chat_list.adapter = userAdpater
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}