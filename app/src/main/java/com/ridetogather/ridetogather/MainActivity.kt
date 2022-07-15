package com.ridetogather.ridetogather

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.ridetogather.ridetogather.ui.MapsActivity

class MainActivity : AppCompatActivity() {

    var login : TextView?=null
    var main_btn_loginWithFacebook:LoginButton?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        login=findViewById(R.id.main_btn_login)
        main_btn_loginWithFacebook=findViewById(R.id.main_btn_loginWithFacebook)
        login?.setOnClickListener({
            val i = Intent(this@MainActivity, MapsActivity::class.java)
            startActivity(i)

        })
        val callbackManager=CallbackManager.Factory.create()
        main_btn_loginWithFacebook?.registerCallback(callbackManager, object :
            FacebookCallback<LoginResult>{
            override fun onCancel() {

            }

            override fun onError(error: FacebookException) {

            }

            override fun onSuccess(result: LoginResult) {
                val i = Intent(this@MainActivity, MapsActivity::class.java)
                startActivity(i)
            }

        })



    }
}