package com.benjaminearley.bokchoy

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.EditText
import com.benjaminearley.bokchoy.model.ListItem
import com.benjaminearley.bokchoy.util.Keys
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

internal class ListRecyclerAdapter(ref: DatabaseReference, val listKey: String)
: FirebaseRecyclerAdapter<ListItem,
        ListRecyclerAdapter.ViewHolder>(
        ListItem::class.java,
        R.layout.list_item,
        ListRecyclerAdapter.ViewHolder::class.java,
        ref) {

    companion object {
        var cursorPosition: Pair<String?, Int>? = null
    }

    override fun populateViewHolder(viewHolder: ViewHolder, listItem: ListItem, position: Int) {

        viewHolder.checkBox.isChecked = listItem.checkbox
        viewHolder.text.setText(listItem.text)

        val key = getRef(position).key

        viewHolder.listKey = listKey
        viewHolder.key = key

        cursorPosition?.first?.let {
            try {
                if (cursorPosition?.first == key) viewHolder.text.setSelection(cursorPosition?.second ?: 0)
            } catch (e: IndexOutOfBoundsException) {
                viewHolder.text.setSelection(
                        if (viewHolder.text.text.length > 0) viewHolder.text.text.length - 1
                        else 0
                )
            }
        }
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, TextWatcher {

        var listKey: String? = null
        var key: String? = null
        val checkBox = view.findViewById(R.id.checkBox) as CheckBox
        val text = view.findViewById(R.id.text) as EditText
        val handler = Handler()
        val sync = Runnable {
            key?.let {
                cursorPosition = Pair(it, text.selectionStart)
                FirebaseDatabase
                        .getInstance()
                        .reference
                        .child(Keys.LISTS_CHILD)
                        .child(listKey)
                        .child(Keys.LIST_CHILD)
                        .child(it)
                        .child(Keys.LIST_TEXT)
                        .setValue(text.text.toString())
            }
        }

        init {
            checkBox.setOnClickListener(this)
            text.addTextChangedListener(this)
        }

        override fun onClick(v: View) {
            key?.let {
                FirebaseDatabase
                        .getInstance()
                        .reference
                        .child(Keys.LISTS_CHILD)
                        .child(listKey)
                        .child(Keys.LIST_CHILD)
                        .child(it)
                        .child(Keys.LIST_CHECKBOX)
                        .setValue((v as CheckBox).isChecked)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            handler.removeCallbacks(sync)
            handler.postDelayed(sync, 1000)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }


    }
}
