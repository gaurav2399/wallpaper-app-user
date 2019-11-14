package com.gaurav.walllpaperhub.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //to manage time of splash screen handle systemclock in application class

        //we don't need to do that stuff as we set theme to splash screen
        //so no need of layout, it take much less time
        //and considered as the right way of splash screen implementation

//        setContentView(R.layout.activity_main)

//        GlobalScope.launch{
//
//            coroutineScope {
//                launch {
//                    delay(3000)
//                }
//
//            }
//            Log.e("time completed ","for splash")
//            val intent = Intent(this@MainActivity,HomeActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

        val intent = Intent(this@MainActivity,HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
