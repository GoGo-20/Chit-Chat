package com.example.chitchat.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.AdapterClasses.UserAdapter
import com.example.chitchat.ModelClasses.users
import com.example.chitchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? = null

    private var musers : List<users>? = null

    private var recyclerView: RecyclerView? = null

    private var searchEditText: EditText? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
       val view: View =  inflater.inflate(R.layout.fragment_search, container, false)

        recyclerView = view.findViewById(R.id.search_list)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager = LinearLayoutManager(context)
        searchEditText = view.findViewById(R.id.searchUsers)

        musers = ArrayList()

        retrieveAllUsers()

        searchEditText!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                SearchForUsers(s.toString().toLowerCase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    private fun retrieveAllUsers() {
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var refUsers = FirebaseDatabase.getInstance().reference.child("Users")

        refUsers.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (musers as ArrayList<users>).clear()
                if (searchEditText!!.text.toString() == ""){
                    for(snapshot in snapshot.children)
                    {
                        val users: users? = snapshot.getValue(users::class.java)
                        if(!(users!!.getUID()).equals(firebaseUserID)){
                            (musers as ArrayList<users>).add(users)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, musers!!, false )
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun SearchForUsers(str: String){
        var firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        var queryUsers = FirebaseDatabase.getInstance().reference
            .child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str + "\uf8ff")

        queryUsers.addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                (musers as ArrayList<users>).clear()
                for(snapshot in snapshot.children)
                {
                    val users: users? = snapshot.getValue(users::class.java)
                    if(!(users!!.getUID()).equals(firebaseUserID)){
                        (musers as ArrayList<users>).add(users)
                    }
                }
                userAdapter = UserAdapter(context!!, musers!!, false )

                recyclerView!!.adapter = userAdapter
            }


            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

}