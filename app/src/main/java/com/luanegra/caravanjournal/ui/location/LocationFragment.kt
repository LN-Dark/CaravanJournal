package com.luanegra.caravanjournal.ui.location

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.luanegra.caravanjournal.LogInActivity
import com.luanegra.caravanjournal.MainActivity
import com.luanegra.caravanjournal.R
import com.luanegra.caravanjournal.ui.personanongrata.NewPersonaNonGrataLocationActivity
import de.hdodenhof.circleimageview.CircleImageView

class LocationFragment : Fragment() {
    private var root: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
       root = inflater.inflate(R.layout.fragment_location, container, false)
        val efab_location_new: com.nambimobile.widgets.efab.FabOption = root!!.findViewById(R.id.efab_location_new)
        val efab_personanongrata_new: com.nambimobile.widgets.efab.FabOption = root!!.findViewById(R.id.efab_personanongrata_new)
        efab_location_new.setOnClickListener {
            val intent = Intent(root!!.context, NewLocationActivity::class.java)
            startActivity(intent)
        }
        efab_personanongrata_new.setOnClickListener {
            val intent = Intent(root!!.context, NewPersonaNonGrataLocationActivity::class.java)
            startActivity(intent)
        }
        return root
    }
}