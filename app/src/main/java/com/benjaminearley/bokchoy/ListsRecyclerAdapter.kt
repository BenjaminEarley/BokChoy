package com.benjaminearley.bokchoy


import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v7.widget.CardView
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.benjaminearley.bokchoy.model.ListsItem
import com.benjaminearley.bokchoy.util.colorPrimaryMap
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference

internal class ListsRecyclerAdapter(val activity: FragmentActivity, ref: DatabaseReference)
: FirebaseRecyclerAdapter<ListsItem,
        ListsRecyclerAdapter.ViewHolder>(
        ListsItem::class.java,
        R.layout.lists_item,
        ListsRecyclerAdapter.ViewHolder::class.java,
        ref) {

    override fun populateViewHolder(viewHolder: ViewHolder, listsItem: ListsItem, position: Int) {


        viewHolder.listsItem.setCardBackgroundColor(
                ContextCompat.getColor(activity, colorPrimaryMap[listsItem.color] ?: R.color.green_custom))

        val title = listsItem.title
        val key = getRef(position).key

        viewHolder.title.setText(listsItem.title)
        viewHolder.key = key
        viewHolder.color = listsItem.color.toString()

        viewHolder.listsItem.setOnLongClickListener {
            val frag = AddListBottomSheetFragment()
            val args = Bundle()
            args.putString(AddListBottomSheetFragment.KEY_KEY, key)
            args.putString(AddListBottomSheetFragment.TITLE_KEY, title)
            args.putString(AddListBottomSheetFragment.COLOR_KEY, listsItem.color)
            frag.arguments = args
            frag.show(activity.supportFragmentManager, AddListBottomSheetFragment.TAG)
            true
        }
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        val listsItem = view.findViewById(R.id.listsItem) as CardView
        val title = view.findViewById(R.id.title) as TextView
        var key: String? = null
        lateinit var color: String

        init {
            listsItem.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            ListActivity.startListActivity(v.context, title.text.toString(), color, key!!)
        }
    }
}