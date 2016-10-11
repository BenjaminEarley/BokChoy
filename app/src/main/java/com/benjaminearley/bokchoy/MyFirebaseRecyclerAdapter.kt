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
import java.util.concurrent.TimeUnit

internal class MyFirebaseRecyclerAdapter(ref: DatabaseReference)
: FirebaseRecyclerAdapter<ListItem,
        MyFirebaseRecyclerAdapter.ViewHolder>(
        ListItem::class.java,
        R.layout.item,
        MyFirebaseRecyclerAdapter.ViewHolder::class.java,
        ref) {

    companion object {
        var cursorPosition: Pair<String?, Int>? = null
    }

    override fun populateViewHolder(viewHolder: ViewHolder, listItem: ListItem, position: Int) {

        viewHolder.checkBox.isChecked = listItem.checkbox
        viewHolder.text.setText(listItem.text)

        val key = getRef(position).key

        viewHolder.key = key

        cursorPosition?.first?.let {
            try {
                if (cursorPosition?.first == key) viewHolder.text.setSelection(cursorPosition?.second ?: 0)
            } catch (e: IndexOutOfBoundsException) {
                viewHolder.text.setSelection(viewHolder.text.text.length-1)
            }
        }
    }

    internal class ViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener, TextWatcher {

        var key: String? = null
        val checkBox: CheckBox
        val text: EditText
        val handler = Handler()
        val sync: Runnable

        init {
            checkBox = view.findViewById(R.id.check_box) as CheckBox
            text = view.findViewById(R.id.item) as EditText

            checkBox.setOnClickListener(this)
            text.addTextChangedListener(this)

            sync = Runnable {
                key?.let {
                    cursorPosition = Pair(it, text.selectionStart)
                    FirebaseDatabase.getInstance().reference.child(Keys.ITEMS_CHILD).child(it).child("text").setValue(text.text.toString())
                }
            }
        }

        override fun onClick(v: View) {
            key?.let {
                FirebaseDatabase.getInstance().reference.child(Keys.ITEMS_CHILD).child(it).child("checkbox").setValue((v as CheckBox).isChecked)
            }
        }

        override fun afterTextChanged(s: Editable?) {
            handler.removeCallbacks(sync)
            handler.postDelayed(sync, 1000)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}


    }
}
