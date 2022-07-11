package com.ridetogather.ridetogather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.ridetogather.ridetogather.ui.MapsActivity

class MainActivity : AppCompatActivity() {

    var login : TextView?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        login=findViewById(R.id.main_btn_login)

        login?.setOnClickListener({
            val i = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(i)

        })
   

    }
}