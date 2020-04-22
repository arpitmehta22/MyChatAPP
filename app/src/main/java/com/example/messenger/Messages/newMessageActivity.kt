package com.example.messenger.Messages

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager

import com.example.messenger.Model.User
import com.example.messenger.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.item_user_new_message.view.*

class newMessageActivity : AppCompatActivity() {
companion object {
    val USER_KEY = "USER_KEY"
}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)


        supportActionBar?.title= "Select User"

        recyclerview_new_message.layoutManager= LinearLayoutManager(this)

        fetchUsers()
        val USER_KEY="USER_KEY"
    }

    private fun fetchUsers() {
      val ref= FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter= GroupAdapter<ViewHolder>()

                p0.children.forEach{
                    Log.d("NewMessage",it.toString())
                    val user= it.getValue(User::class.java)
                    if(user!=null)
                    {
                        adapter.add(UserItem(user))
                    }



            }
                adapter.setOnItemClickListener({item,view->
                    val userItem= item as UserItem
                    val intent= Intent(view.context,chatlogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)


                })

                recyclerview_new_message.adapter= adapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}


class UserItem(val user: User): Item<ViewHolder>(){

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.user_name_list_item.text= user.username

        Picasso.get().load(user.userProfileImageUrl).into(viewHolder.itemView.user_imageView_item_list)



    }

    override fun getLayout(): Int {
        return R.layout.item_user_new_message
    }

}