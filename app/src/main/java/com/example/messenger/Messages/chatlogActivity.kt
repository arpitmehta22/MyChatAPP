package com.example.messenger.Messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.messenger.Model.ChatMessage
import com.example.messenger.Model.User
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chatlog.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_from_row.view.text_row
import kotlinx.android.synthetic.main.chat_to_row.view.*

class chatlogActivity : AppCompatActivity() {
companion object{
    val TAG="Chat log"
}
    val adapter= GroupAdapter<ViewHolder>( )
    var touser:User?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatlog)
        chat_row_recyclerView.layoutManager=LinearLayoutManager(this)
        chat_row_recyclerView.adapter= adapter

        touser=intent.getParcelableExtra<User>(newMessageActivity.USER_KEY)

        supportActionBar?.title =touser?.username
        ListenMessage()

        send_button.setOnClickListener({
            Log.d(TAG,"message was sent")
            perform_send_message()


        })

    }

    private fun ListenMessage() {

        val fromid= FirebaseAuth.getInstance().uid
        val to_id=touser?.uid
        val ref_from_user= FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$to_id")
        val currentuser=LatestMessageAcivity.currentUser

        ref_from_user.addChildEventListener(object :ChildEventListener{
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                Log.d(TAG,chatMessage?.text)
                if(chatMessage!=null) {

                    if(chatMessage.toid==fromid){

                    adapter.add(ChatfromItem(chatMessage.text,touser!!))
                    }
                    else {

                        adapter.add(ChattoItem(chatMessage.text,currentuser!!))

                    }
                }


            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })
        

    }



    private fun perform_send_message() {
        //used to send message
        val text= chat_message_chat_log.text.toString()
        val fromid=FirebaseAuth.getInstance().uid

        val to_id=touser?.uid
        if(fromid==null)
            return

        val ref= FirebaseDatabase.getInstance().getReference("/user-messages/$fromid/$to_id").push()
        val toref= FirebaseDatabase.getInstance().getReference("/user-messages/$to_id/$fromid").push()

        val chat_message= ChatMessage(text, ref.key.toString(),fromid.toString(),to_id.toString(),System.currentTimeMillis()/1000)

        ref.setValue(chat_message).addOnSuccessListener {
            Toast.makeText(this,"sent",Toast.LENGTH_SHORT).show()
            chat_message_chat_log.text.clear()
            chat_row_recyclerView.scrollToPosition(adapter.itemCount-1)
        }
        toref.setValue(chat_message )
        val latestMessageref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromid/$to_id")
latestMessageref.setValue(chat_message)
        val latestMessageToref=FirebaseDatabase.getInstance().getReference("/latest-messages/$to_id/$fromid")
            latestMessageToref.setValue(chat_message)
    }


}
class ChatfromItem(val text:String,val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {

        viewHolder.itemView.text_row.text=text
        val uri=user.userProfileImageUrl
        val targetImageView=viewHolder.itemView.imageView_chat_from_row
        Picasso.get().load(uri).into(targetImageView)





    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

}

class ChattoItem(val text:String,val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.text_row.text= text
        //load our user image
        val uri=user.userProfileImageUrl
        val targetImageView=viewHolder.itemView.imageView_chat_to_row
        Picasso.get().load(uri).into(targetImageView)



    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

}