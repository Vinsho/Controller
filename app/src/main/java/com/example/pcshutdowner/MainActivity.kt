package com.example.pcshutdowner

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.util.Log
import android.widget.TextView
import java.io.File
import java.lang.Exception
import java.net.Socket


class MainActivity : Activity() {
    lateinit var ip : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        openConnectionScene()
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
    }


    fun openConnectionScene(){
        setContentView(R.layout.activity_connect)
        try{
            ip = File(applicationContext.filesDir,"info.txt").readText()
            findViewById<EditText>(R.id.ipAdress).setText(ip)
        }catch (e:java.io.FileNotFoundException){
            File(applicationContext.filesDir,"info.txt").createNewFile()
        }
    }

    fun areYouSure(inst:View){
        val dialogClickListener =
            DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        sendMessage(inst)
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                        dialog.dismiss()
                    }
                }
            }

        val builder = AlertDialog.Builder(this)
        builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
            .setNegativeButton("No", dialogClickListener).show()
    }

   fun connect(inst: View){
       this.ip = findViewById<EditText>(R.id.ipAdress).text.toString()
       File(applicationContext.filesDir,"info.txt").writeText(this.ip)
       findViewById<TextView>(R.id.disconnected).visibility = View.INVISIBLE
       setContentView(R.layout.activity_main)
   }

    fun sendMessage(inst: View){
        try{
            val client = Socket(ip, 9999)
            client.outputStream.write(resources.getResourceEntryName(inst.id).toByteArray())
            client.close()
        }catch (e:java.net.ConnectException){
            openConnectionScene()
            findViewById<TextView>(R.id.disconnected).visibility = View.VISIBLE
        }

    }


}


