package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.core.app.Person.fromBundle
import com.finsol.tech.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class BottomSheetDialog: BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        @Nullable container: ViewGroup?,
        @Nullable savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(
            R.layout.dialog_bottom_watchlist_item_details,
            container, false
        )
        val buyButton: Button = v.findViewById(R.id.buyButton)
        val sellButton: Button = v.findViewById(R.id.sellButton)
        val viewMore: TextView = v.findViewById(R.id.viewMoreDetails)

        val model:WatchListModel? = arguments?.getParcelable("selectedModel")

        v.findViewById<TextView>(R.id.symbolName).setText(model?.symbolName)
        v.findViewById<TextView>(R.id.symbolPrice).setText(model?.symbolPrice)
        v.findViewById<TextView>(R.id.symbolTime).setText(model?.symbolTime)
        v.findViewById<TextView>(R.id.symbolCity).setText(model?.symbolCity)
        v.findViewById<TextView>(R.id.symbolValue).setText(model?.symbolValue)

        buyButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    activity,
                    "buy clicked", Toast.LENGTH_SHORT
                )
                    .show()
                dismiss()
            }
        })
        sellButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    activity,
                    "sell clicked", Toast.LENGTH_SHORT
                )
                    .show()
                dismiss()
            }
        })
        viewMore.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    activity,
                    "view more clicked", Toast.LENGTH_SHORT
                )
                    .show()
                dismiss()
            }
        })
        return v
    }

}