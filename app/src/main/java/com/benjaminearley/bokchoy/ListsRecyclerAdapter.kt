package com.benjaminearley.bokchoy


import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.benjaminearley.bokchoy.model.ListsItem
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference

internal class ListsRecyclerAdapter(ref: DatabaseReference)
: FirebaseRecyclerAdapter<ListsItem,
        ListsRecyclerAdapter.ViewHolder>(
        ListsItem::class.java,
        R.layout.lists_item,
        ListsRecyclerAdapter.ViewHolder::class.java,
        ref) {

    override fun populateViewHolder(viewHolder: ViewHolder, listsItem: ListsItem, position: Int) {

        viewHolder.title.setText(listsItem.title)
        viewHolder.key = getRef(position).key
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val listsItem = view.findViewById(R.id.listsItem) as CardView
        val title = view.findViewById(R.id.title) as TextView
        var key: String? = null

        init {
            listsItem.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            ListActivity.startListActivity(v.context, title.text.toString(), key!!)
        }
    }
}