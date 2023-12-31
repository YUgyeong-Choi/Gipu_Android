package com.example.gipu_android

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson

class LikeListAdapter(private var infoList: MutableList<InfoData>): RecyclerView.Adapter<LikeListAdapter.InfoViewHolder>() {

    inner class InfoViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        val info_category = itemView.findViewById<TextView>(R.id.item_category)
        val info_title = itemView.findViewById<TextView>(R.id.item_title)
        val info_content = itemView.findViewById<TextView>(R.id.item_content)
        val info_si = itemView.findViewById<TextView>(R.id.item_si)
        val info_dong = itemView.findViewById<TextView>(R.id.item_dong)
        val star = itemView.findViewById<ImageView>(R.id.item_tag)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InfoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.info_item, parent, false)
        return InfoViewHolder(itemView).apply{ //itemView 넘겨주기
            itemView.setOnClickListener{
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION){
                    val clickedItem = infoList[position]
                    val activityContext = parent.context as? Activity
                    (parent.context as Activity).finish()
                    val intent = Intent(parent.context, LikeDetailActivity::class.java)
                    intent.putExtra("info", clickedItem)
                    parent.context.startActivity(intent)
                    activityContext?.overridePendingTransition(R.anim.slide_right_enter, R.anim.slide_right_exit)
                }
            }
        }
    }


    override fun onBindViewHolder(holder: InfoViewHolder, position: Int) {
        holder.info_category.text = infoList[position].category
        if (infoList[position].category == "기부 하기") {
            holder.info_category.setBackgroundResource(R.drawable.background_give)
        }else{
            holder.info_category.setBackgroundResource(R.drawable.background_category)
        }
        holder.info_title.text = infoList[position].title

        if (infoList[position].content.length <= 25) {
            holder.info_content.text = infoList[position].content
        } else {
            holder.info_content.text = infoList[position].content.take(25) + "..."
        }

        holder.info_si.text = infoList[position].si
        holder.info_dong.text = infoList[position].dong
        holder.star.visibility = View.VISIBLE

        holder.star.setOnClickListener {
            val heartDB_name = infoList[position].writer + "_"+  infoList[position].title
            InfoDetailActivity.HeartDB.init(holder.itemView.context)
            val InfoData = InfoDetailActivity.HeartDB.getInstance()

            if(InfoData.contains(heartDB_name)){
                val editor = InfoData.edit()
                editor.remove(heartDB_name)
                editor.apply()
                holder.star.setImageResource(R.drawable.emptystar)

                // infoList에서 해당 항목을 찾아 제거
                val removedItem = infoList.find { it.writer + "_" + it.title == heartDB_name }
                if (removedItem != null) {
                    val position = infoList.indexOf(removedItem)
                    infoList.removeAt(position)
                    notifyItemRemoved(position)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return infoList.size
    }

    fun updateData(newLikeList: MutableList<InfoData>) {
        infoList = newLikeList
        notifyDataSetChanged()
    }
}