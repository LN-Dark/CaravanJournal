package com.luanegra.caravanjournal.ui.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.luanegra.caravanjournal.LogInActivity
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
            val intent = Intent(root!!.context, LogInActivity::class.java)
            startActivity(intent)
            FirebaseAuth.getInstance().signOut()
        }

        var img_paypal: ImageView = root!!.findViewById(R.id.img_paypal)
        var img_github: ImageView = root!!.findViewById(R.id.img_github)
        img_paypal.setOnClickListener {
            val url = "https://www.paypal.com/paypalme/pedrocruz77"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        img_github.setOnClickListener {
            val url = "https://github.com/LN-Dark"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }



        return root
    }
}