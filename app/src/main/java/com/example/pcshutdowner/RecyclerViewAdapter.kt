package com.example.pcshutdowner

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class RecyclerViewAdapter(
    private val computers: ArrayList<Computer>,
    private val mActivity: MainActivity
) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_listitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.update(computers[position], mActivity)
    }

    override fun getItemCount(): Int {
        return computers.size
    }

    inner class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        private var ipText: TextView = itemView.findViewById(R.id.ipAddressText)
        private var image: ImageView = itemView.findViewById(R.id.computerImage)
        private var computerNameText: TextView = itemView.findViewById(R.id.computerNameText)
        private var parentLayout: RelativeLayout = itemView.findViewById(R.id.parent_layout)

        fun update(computer: Computer, activity: MainActivity) {
            parentLayout.setOnClickListener { v: View? ->
                mActivity.connect(
                    computers[position].ip
                )
            }
            ipText.text = computers[position].ip
            computerNameText.text = computers[position].name
        }
    }

}