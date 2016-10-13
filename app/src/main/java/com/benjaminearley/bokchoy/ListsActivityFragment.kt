package com.benjaminearley.bokchoy

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.benjaminearley.bokchoy.util.GridSpacingItemDecoration
import com.benjaminearley.bokchoy.util.Keys
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_lists.*

class ListsActivityFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater!!.inflate(R.layout.fragment_lists, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                (viewHolder as? ListsRecyclerAdapter.ViewHolder)?.let {
                    FirebaseDatabase.getInstance().reference.child(Keys.LISTS_CHILD).child(it.key).removeValue()
                }
            }
        }

        val itemTouchHelper = ItemTouchHelper(simpleItemTouchCallback)

        itemTouchHelper.attachToRecyclerView(listsRecyclerView)

        listsRecyclerView?.addItemDecoration(GridSpacingItemDecoration(2, resources.getDimensionPixelOffset(R.dimen.card_margin), true))

        setupFirebaseAdapter()
    }

    fun setupFirebaseAdapter() {
        val firebaseAdapter = ListsRecyclerAdapter(
                context,
                FirebaseDatabase.getInstance().reference.child(Keys.LISTS_CHILD))
        listsRecyclerView.adapter = firebaseAdapter
    }
}
