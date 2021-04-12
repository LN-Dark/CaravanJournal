package com.luanegra.caravanjournal.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.luanegra.caravanjournal.LogInActivity
import com.luanegra.caravanjournal.MainActivity
import com.luanegra.caravanjournal.R

class SettingsFragment : Fragment() {
    private var root: View? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_settings, container, false)
        val btn_logout: TextView = root!!.findViewById(R.id.btn_logout)
        btn_logout.setOnClickListener {
            activity?.let{
                val intent = Intent (it, LogInActivity::class.java)
                it.startActivity(intent)
            }
            FirebaseAuth.getInstance().signOut()
        }

        return root
    }
}