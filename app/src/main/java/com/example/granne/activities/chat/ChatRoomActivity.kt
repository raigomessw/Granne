package com.example.granne.activities.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.example.granne.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ChatRoomActivity : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var myNickName: String
    val db = Firebase.firestore

    private lateinit var chatKey: String
    private lateinit var secondUserNickName: String
    private lateinit var newMessageEdT: EditText
    private lateinit var messageTV: TextView
    private lateinit var messageBtn: Button
    private lateinit var userTitle: TextView
    private lateinit var messageList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_room)
        auth = Firebase.auth

        messageTV = findViewById(R.id.messageTextView)
        newMessageEdT = findViewById(R.id.newMessageEditText)
        messageBtn = findViewById(R.id.messageButton)
        userTitle = findViewById(R.id.userTitle)

        secondUserNickName = intent.getStringExtra("secondUserNickname").toString()
        val secondUserUid: String = intent.getStringExtra("secondUserUid").toString()
        val myDocRef = db.collection("userData").document(auth.currentUser!!.uid)


        myDocRef.get()
            .addOnSuccessListener { name ->
                myNickName = name.data!!.getValue("nickName").toString()

                myDocRef.collection("matchedUsers").document(secondUserUid).get()
                    .addOnSuccessListener { documents ->
                        chatKey = documents.data!!.getValue("chatId").toString()
                        createChatChannel(chatKey)
                    }
            }

    }

    private fun createChatChannel(chatKey: String) {
        val chatDocRef = db.collection("chatRooms").document(chatKey)
        messageList = arrayListOf()

        val chatInfo = hashMapOf(
            "user1" to secondUserNickName,
            "user2" to myNickName,
            "messages" to messageList
        )

        chatDocRef.get()
            .addOnSuccessListener { task ->
                if (!task.exists()) {
                    Log.d("!", "No chat with the key: $chatKey")

                    chatDocRef.set(chatInfo)
                        .addOnSuccessListener {
                            Log.d("!", "Created chat with $secondUserNickName")
                            updateChatUi()
                        }
                }
                if (task.exists()) {
                    Log.d("!", "Joining chat with $secondUserNickName with chatId: $chatKey")
                    updateChatUi()
                }
            }

    }

    private fun updateChatUi() {
        val chatDocRef = db.collection("chatRooms").document(chatKey)


        // Sets title for the chatroom
        val titleNickName = secondUserNickName
        userTitle.text = titleNickName

        chatDocRef.get()
            .addOnSuccessListener { list ->
                val oldList = list.data!!.getValue("messages").toString()
                messageList.add(oldList)

                // add $messageList to view
                Log.d("!", "new list$messageList")

                messageBtn.setOnClickListener {
                    val text = "$myNickName: ${newMessageEdT.text}"
                    messageList.add(text)
                    newMessageEdT.text.clear()

                    // add $text to view
                    Log.d("!", "Sent text: $text")

                    chatDocRef.update("messagelist", messageList)


                }
            }

        chatDocRef
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener // Stop listening to this snapshot
                }

                if (snapshot != null && snapshot.exists()) {
                    val currentList = snapshot.data!!.getValue("messagelist")
                    if (currentList.toString().isNotEmpty()) {

                        messageTV.text = currentList.toString()
                            .replace("]", "")
                            .replace("[", "")
                            .replace(",", "")

                    }

                } else {
                    Log.d("!", "Current data: null")
                }

            }


    }
}



