package com.example.granne

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class PersonFindMatchRecycleViewAdapter(
    val context: Context,
    private val persons: List<PersonFindMatch>,
) : RecyclerView.Adapter<PersonFindMatchRecycleViewAdapter.ViewHolder>() {

    var auth = Firebase.auth
    val db = Firebase.firestore
    private val layoutInflater = LayoutInflater.from(context)

    override fun getItemCount(): Int {
        return persons.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_item_match, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = persons[position]

        holder.nameTV.text = person.name.toString()
        holder.interestTV.text = person.intressen.toString()
        holder.aboutMeTV.text = person.aboutMe.toString()

        holder.buttonAdd.setOnClickListener {
            val nickname = person.name.toString()
            val userUid = person.uid.toString()
            val veryBadIdGenerator = (234234324..4343434345).random().toString()


            val mapOfDetails = hashMapOf(
                "nickname" to nickname,
                "uid" to userUid,
                "chatId" to veryBadIdGenerator
            )

            Log.d("!", ">> ${person.name}}")
            Log.d("!", ">> ${person.aboutMe}}")
            Log.d("!", ">> ${person.intressen}}")
            Log.d("!", ">> ${person.uid}}")

            db.collection("userData").document(auth.currentUser!!.uid)
                .collection("matchedUsers").document(userUid).set(mapOfDetails)
                .addOnSuccessListener {

                    showToast("Added $nickname to chat list!")

                    addYourselfToSecondUserMatchedList(userUid, veryBadIdGenerator)
                }
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val nameTV: TextView = itemView.findViewById(R.id.tvName)
        val interestTV: TextView = itemView.findViewById(R.id.tvIntressen)
        val aboutMeTV: TextView = itemView.findViewById(R.id.tvAboutMe)
        val buttonAdd: ImageButton = itemView.findViewById(R.id.btnAdd)

    }

    private fun addYourselfToSecondUserMatchedList(uid: String, chatId: String) {
        db.collection("userData").document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                val myName = documents.data!!.getValue("nickName").toString()
                val myUid = auth.currentUser!!.uid

                val mapOfDetails = hashMapOf(
                    "nickName" to myName,
                    "uid" to myUid,
                    "chatId" to chatId
                )

                db.collection("userData").document(uid)
                    .collection("matchedUsers").document(auth.currentUser!!.uid)
                    .set(mapOfDetails)
                    .addOnSuccessListener {
                        Log.d("!", "Added yourself to the other persons Matched users list!")
                    }
            }
    }

    private fun showToast(toastMessage: String) {
        val toast = Toast.makeText(context, toastMessage, Toast.LENGTH_SHORT)
        toast.show()
    }
}