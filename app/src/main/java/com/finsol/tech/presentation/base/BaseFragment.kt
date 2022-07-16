package com.finsol.tech.presentation.base

import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment: Fragment() {

    fun handleToast(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

}