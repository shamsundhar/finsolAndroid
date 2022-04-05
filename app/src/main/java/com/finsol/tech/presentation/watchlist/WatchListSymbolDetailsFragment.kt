package com.finsol.tech.presentation.watchlist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.finsol.tech.R

class WatchListSymbolDetailsFragment: Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v: View = inflater.inflate(R.layout.fragment_watchlist_symbol_details, container, false)
        val buyButton: Button = v.findViewById(R.id.buyButton)
        val sellButton: Button = v.findViewById(R.id.sellButton)

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
            }
        })
        sellButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(
                    activity,
                    "sell clicked", Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

        return v
    }
}