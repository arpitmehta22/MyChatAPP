package com.example.messenger.Messages

import LatestMessage
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.LoginandRegister.LoginActivity
import com.example.messenger.Model.ChatMessage
import com.example.messenger.Model.User
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder

import kotlinx.android.synthetic.main.activity_latest_message_acivity.*
import kotlinx.android.synthetic.main.content_latest_message_acivity.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageAcivity : AppCompatActivity() {

    companion object{
        var currentUser: User?=null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message_acivity)
        setSupportActionBar(toolbar)


        recyclerView_for_latestMessages.layoutManager=LinearLayoutManager(this)
        recyclerView_for_latestMessages.adapter=adapter
        recyclerView_for_latestMessages.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL ))
//set item click listener
        adapter.setOnItemClickListener{item,view->
            val intent= Intent(this,chatlogActivity::class.java)

            val row= item as LatestMessage
            intent.putExtra(newMessageActivity.USER_KEY,row.chatpartnerID)
            startActivity(intent)

        }
        ListenforMessages()
        fetchUser()
        VerifyuserLoggedin()
    }
    val latestmessagesMap=HashMap<String,ChatMessage>()


    private fun refreshRecyclerViewmessages()
    {adapter.clear()
        latestmessagesMap.values.forEach({

            adapter.add(LatestMessage(it))
        })
    }

    private fun ListenforMessages() {
        val fromId=FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object :ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatmessage=p0.getValue(ChatMessage::class.java)?:return
                latestmessagesMap[p0.key!!]= chatmessage
                refreshRecyclerViewmessages()


            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                val chatmessage=p0.getValue(ChatMessage::class.java)?:return

                latestmessagesMap[p0.key!!]= chatmessage
                refreshRecyclerViewmessages()

            }

            override fun onChildRemoved(p0: DataSnapshot) {}

        })
    }
    val adapter=GroupAdapter<ViewHolder>()



    private fun fetchUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                currentUser=p0.getValue(User::class.java )
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

    }

    private fun VerifyuserLoggedin() {
        val uid = FirebaseAuth.getInstance().uid

        if (uid == null) {
            val intent = Intent(
                this,
                LoginActivity::class.java
            )
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.nav_menu,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item?.itemId)
        {
            R.id.action_signout ->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this,
                    LoginActivity::class.java)

                startActivity(intent)


            }
            R.id.action_show_latest_message ->{
                val intent = Intent(this,
                    newMessageActivity::class.java)

                startActivity(intent)


            }


        }

        return super.onOptionsItemSelected(item)
    }

}
