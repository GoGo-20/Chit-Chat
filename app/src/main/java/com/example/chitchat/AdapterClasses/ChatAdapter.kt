package com.example.chitchat.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chitchat.ModelClasses.Chat
import com.example.chitchat.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
): RecyclerView.Adapter<ChatAdapter.viewHolder?>()
{

    private val mContext: Context
    private val mChatList: List<Chat>
    private val imageUrl: String
    private val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser!!

    init {
        this.mChatList = mChatList
        this.mContext = mContext
        this.imageUrl = imageUrl
    }

    override fun onCreateViewHolder(parent: ViewGroup, position: Int): viewHolder {
        return if (position == 1){
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right, parent, false)
            viewHolder(view)
        }else{
            val view : View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left, parent, false)
            viewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
       val chat: Chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)

        if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){

            if (chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)
            }else if(!chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)
            }

        }else{
            holder.show_text_message!!.visibility = View.VISIBLE
            holder.show_text_message!!.text = chat.getMessage()
        }

        if (position == mChatList.size-1){
           if (chat.isIsSeen()!!){
               holder.text_seen!!.text = "Seen"

               if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                   val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.text_seen!!.layoutParams = lp
               }
           }else{
               holder.text_seen!!.text = "Sent"

               if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                   val lp: RelativeLayout.LayoutParams? = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   lp!!.setMargins(0, 245, 10, 0)
                   holder.text_seen!!.layoutParams = lp
               }
           }

        }else{
            holder.text_seen!!.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView){



        var profile_image: CircleImageView? = null
        var show_text_message: TextView? = null
        var left_image_view: ImageView? = null
        var text_seen: TextView? = null
        var right_image_view: ImageView? = null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
        }

    }

    override fun getItemViewType(position: Int): Int {

        return if(mChatList[position].getSender().equals(firebaseUser!!.uid)){
            1
        }else{
            0
        }
    }
}