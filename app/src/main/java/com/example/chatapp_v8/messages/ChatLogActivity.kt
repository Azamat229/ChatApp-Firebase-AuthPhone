package com.example.chatapp_v8.messages

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.chatapp_v8.Home
import com.example.chatapp_v8.NewMessageActivity
import com.example.chatapp_v8.R
import com.example.chatapp_v8.model.ChatMessage
import com.example.chatapp_v8.model.User
import com.example.chatapp_v8.view.ChatFromItem
import com.example.chatapp_v8.view.ChatToItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    companion object {
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<ViewHolder>()
    var toUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter


        //val username = intent.getStringExtra(NewMessageActivity.USER_KEY)
        toUser = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)

            supportActionBar?.title = toUser?.username
         // display custom user name at top of chat


//        setupDummyData()
        listenForMessages()

        send_botton_chat_log.setOnClickListener() {
            Log.d(TAG, "SEND BUTTON")
            performSendMessage()
        }

    }

    private fun listenForMessages() {
        /** Get data from Firebase Database*/
        val ref = FirebaseDatabase.getInstance().getReference("/messages")

        ref.addChildEventListener(object : ChildEventListener {
//             ChildEventListener сообщает если появились новые данные

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if (chatMessage != null) {
                    Log.d(TAG, chatMessage.text)

                    if (chatMessage.fromId == FirebaseAuth.getInstance().uid) {
                        val currentUser = Home.currentUser ?: return
                        adapter.add(ChatFromItem(chatMessage.text, currentUser))
                    }
                    else {
                        adapter.add(ChatToItem(chatMessage.text, toUser!!))
//
//
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
            }
        })
    }




    private fun performSendMessage() {
        // how do we actually send a message to firebase...
        val text = edittext_chat_log.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId = user?.uid // changed

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("/messages").push()// generate id in firebase

        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId!!, System.currentTimeMillis() / 1000)
        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Log.d(TAG, "Saved our chat message: ${reference.key}")
                }
    }


//    private fun setupDummyData() {
//        val adapter = GroupAdapter<ViewHolder>()
//        adapter.add(ChatFromItem("Hello"))
//        adapter.add(ChatToItem("good what \n about you?"))
//        adapter.add(ChatFromItem("I am also great")) // stoped at 20:00
//        adapter.add(ChatToItem("hwo is your brother?"))


//        recyclerview_chat_log.adapter = adapter
//    }
}
//
//class ChatFromItem(val text: String,  val user: User) : Item<ViewHolder>() {
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.itemView.textView_from_row.text = text
//
//        //load our user image into the star
//        val uri = user.profileImageUrl
//        val targetImageView = viewHolder.itemView.imageView_chat_from
//        Picasso.get().load(uri).into(targetImageView)
//    }
//
//
//    override fun getLayout(): Int {
//        return R.layout.chat_from_row
//    }
//}
//
//class ChatToItem(val text: String,  val user: User) : Item<ViewHolder>() {
//    override fun bind(viewHolder: ViewHolder, position: Int) {
//        viewHolder.itemView.textView_to_row.text = text
//
//        //load our user image into the star
//        val uri = user.profileImageUrl
//        val targetImageView = viewHolder.itemView.imageView_chat_to
//        Picasso.get().load(uri).into(targetImageView)
//    }
//
//    override fun getLayout(): Int {
//        return R.layout.chat_to_row
//    }
//}