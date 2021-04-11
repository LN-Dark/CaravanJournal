package com.luanegra.caravanjournal.ui.personanongrata

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.luanegra.caravanjournal.R

class PersonaNonGrataFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_persona_non_grata, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)

        return root
    }
}