import com.example.messenger.Model.ChatMessage
import com.example.messenger.Model.User
import com.example.messenger.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessage(val chatMessage: ChatMessage): Item<ViewHolder>(){
    var chatpartnerID:User?=null
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val chatpartner:String
        if(chatMessage.fromid== FirebaseAuth.getInstance().uid)
        {
            chatpartner= chatMessage.toid
        }
        else {
            chatpartner= chatMessage.fromid

        }
        val ref= FirebaseDatabase.getInstance().getReference("/users/$chatpartner")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {

                val user= p0.getValue(User::class.java)
                chatpartnerID=user
                val targetImageView= viewHolder.itemView.imageView_latestMessage_row
                Picasso.get().load(user?.userProfileImageUrl).into(targetImageView)

                viewHolder.itemView.textView_latestMessage_from_user.text=user?.username
            }

        })


        viewHolder.itemView.textView_latestMessage_item.text=chatMessage.text

    }

}