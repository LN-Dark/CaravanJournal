package com.luanegra.caravanjournal.ui.location

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.luanegra.caravanjournal.R

class LocationFragment : Fragment() {
    private var root: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       root = inflater.inflate(R.layout.fragment_location, container, false)

        return root
    }
}