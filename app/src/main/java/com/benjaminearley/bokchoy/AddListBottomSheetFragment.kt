package com.benjaminearley.bokchoy

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.support.annotation.StyleRes
import android.support.design.widget.*
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.benjaminearley.bokchoy.model.ListsItem
import com.benjaminearley.bokchoy.util.Keys
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

class AddListBottomSheetFragment : BottomSheetDialogFragment() {

    companion object {
        val TAG: String = "addListBottomSheetFragment"
        val TITLE_KEY = "TITLE"
        val COLOR_KEY = "COLOR"
        val KEY_KEY = "KEY"
    }

    val mBottomSheetBehaviorCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss()
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return CustomWidthBottomSheetDialog(activity, theme)
    }

    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        val view = View.inflate(context, R.layout.fragment_add_list_bottom_sheet, null)
        dialog?.setContentView(view)

        fetchConfig(view)

        val params = (view.parent as View).layoutParams as CoordinatorLayout.LayoutParams
        val behavior = params.behavior

        if (behavior != null && behavior is BottomSheetBehavior<*>) {
            behavior.setBottomSheetCallback(mBottomSheetBehaviorCallback)
        }

        val submitButton = view.findViewById(R.id.submitList) as Button
        val titleFieldLayout = view.findViewById(R.id.titleFieldLayout) as TextInputLayout
        val titleField = view.findViewById(R.id.titleField) as EditText
        val spinner = view.findViewById(R.id.colorSpinner) as Spinner

        val adapter = ArrayAdapter.createFromResource(context,
                R.array.colors, R.layout.color_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        titleFieldLayout.isErrorEnabled = true

        arguments?.let {
            titleField.setText(it.getString(TITLE_KEY))
            spinner.setSelection(adapter.getPosition(it.getString(COLOR_KEY)))
        }

        submitButton.setOnClickListener {
            val titleFieldText = titleField.text.toString()

            if (!titleFieldText.isNullOrBlank()) {
                val listsItem = ListsItem(titleField.text.toString(), spinner.selectedItem.toString())
                val listsItemRef = FirebaseDatabase.getInstance().reference.child(Keys.LISTS_CHILD)
                arguments?.let {
                    listsItemRef.child(it.getString(KEY_KEY)).child(Keys.LISTS_TITLE).setValue(listsItem.title)
                    listsItemRef.child(it.getString(KEY_KEY)).child(Keys.LISTS_COLOR).setValue(listsItem.color)
                } ?: {
                    listsItemRef.push().setValue(listsItem)
                }()
                dismiss()
            } else {
                titleFieldLayout.error = "Please fill field"
            }
        }

        titleField.addTextChangedListener(object : TextWatcher {

            var hasTextBeenEntered = false

            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank() && hasTextBeenEntered) {
                    titleFieldLayout.error = "Please fill field"
                } else {
                    hasTextBeenEntered = true
                    titleFieldLayout.error = null
                }
            }
        })
    }

    fun fetchConfig(view: View) {

        FirebaseRemoteConfig.getInstance().fetch(0L).addOnSuccessListener({
            FirebaseRemoteConfig.getInstance().activateFetched()
            applyRetrievedLengthLimit(view)
        }).addOnFailureListener({
            applyRetrievedLengthLimit(view)
        })
    }

    fun applyRetrievedLengthLimit(view: View) {
        val listsTitleLength = FirebaseRemoteConfig.getInstance()?.getLong(Keys.LISTS_TITLE_LENGTH_CONFIG)
        (view.findViewById(R.id.titleField) as EditText).filters = arrayOf<InputFilter>(android.text.InputFilter.LengthFilter(listsTitleLength?.toInt() ?: 15))
    }

    internal class CustomWidthBottomSheetDialog(context: Context, @StyleRes theme: Int) : BottomSheetDialog(context, theme) {

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val width = context.resources.getDimensionPixelSize(R.dimen.bottom_sheet_width)
            window.setLayout(if (width > 0) width else ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT)
        }
    }
}