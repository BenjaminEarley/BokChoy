package com.benjaminearley.bokchoy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benjaminearley.bokchoy.util.Keys
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_list.*

class ListActivityFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                (viewHolder as? ListRecyclerAdapter.ViewHolder)?.let {
                    FirebaseDatabase
                            .getInstance()
                            .reference
                            .child(Keys.LISTS_CHILD)
                            .child(activity.intent.getStringExtra(ListActivity.KEY))
                            .child(Keys.LIST_CHILD)
                            .child(it.key)
                            .removeValue()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        itemTouchHelper.attachToRecyclerView(listRecyclerView)

        setupFirebaseAdapter()
    }

    override fun onResume() {
        super.onResume()
        setupFirebaseAdapter()
    }

    fun setupFirebaseAdapter() {
        val firebaseAdapter = ListRecyclerAdapter(
                FirebaseDatabase
                        .getInstance()
                        .reference
                        .child(Keys.LISTS_CHILD)
                        .child(activity.intent.getStringExtra(ListActivity.KEY))
                        .child(Keys.LIST_CHILD),
                activity.intent.getStringExtra(ListActivity.KEY)
                )
        listRecyclerView.adapter = firebaseAdapter
    }
}
