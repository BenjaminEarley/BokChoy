package com.benjaminearley.bokchoy

import android.support.v4.app.Fragment
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benjaminearley.bokchoy.util.Keys
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*

class ListActivityFragment : Fragment() {

    lateinit var firebaseDatabaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firebaseDatabaseReference = FirebaseDatabase.getInstance().reference

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                (viewHolder as? MyFirebaseRecyclerAdapter.ViewHolder)?.let {
                    FirebaseDatabase.getInstance().reference.child(Keys.ITEMS_CHILD).child(it.key).removeValue()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        itemTouchHelper.attachToRecyclerView(listRecyclerView)

        setupFirebaseAdapter()
    }

    fun setupFirebaseAdapter() {
        val firebaseAdapter = MyFirebaseRecyclerAdapter(
                firebaseDatabaseReference.child(Keys.ITEMS_CHILD))
        listRecyclerView.adapter = firebaseAdapter
    }
}
