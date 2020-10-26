package com.example.pcshutdowner

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.pnikosis.materialishprogress.ProgressWheel
import kotlinx.android.synthetic.main.activity_device_look_up.*
import java.io.File
import java.net.InetSocketAddress
import java.net.Socket
import java.util.*
import kotlin.concurrent.thread


class MainActivity : Activity() {
    private val computers = ArrayList<Computer>()
    lateinit var ip: String
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerViewAdapter
    lateinit var invalidAddress: TextView
    lateinit var disconnected: TextView
    var layoutId = 0
    var x1 = 0f; var x2 = 0f
    val MIN_DISTANCE = 500
    private var connected = false
    lateinit var con: Controllers

    override fun setContentView(id:Int) {
        layoutId = id
        super.setContentView(id)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = View(this)
        openConnectionScene(view)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        invalidAddress = findViewById(R.id.invalidAdress)
        disconnected = findViewById(R.id.disconnected)
        con = Controllers()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> x1 = event.x
            MotionEvent.ACTION_UP -> {
                x2 = event.x
                val deltaX: Float = x2 - x1
                if (connected) {
                    if (Math.abs(deltaX) > MIN_DISTANCE ){
                        if(x2>x1){
                            setContentView(con.prev(layoutId))
                        }else{
                            setContentView(con.next(layoutId))
                        }
                    }
                } else {
                }
            }
        }
        return super.onTouchEvent(event)
    }

    fun openManualConnectionScene(inst: View) {
        setContentView(R.layout.activity_manual_connect)
        try {
            ip = File(applicationContext.filesDir, "info.txt").readText()
            findViewById<EditText>(R.id.ipAdress).setText(ip)
        } catch (e: java.io.FileNotFoundException) {
            File(applicationContext.filesDir, "info.txt").createNewFile()
        }
    }

    fun openConnectionScene(inst: View) {
        setContentView(R.layout.activity_device_look_up)
        adapter = RecyclerViewAdapter(computers, this)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    fun areYouSure(inst: View) {
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

    fun manualConnect(inst: View) {
        this.ip = findViewById<EditText>(R.id.ipAdress).text.toString()
        File(applicationContext.filesDir, "info.txt").writeText(this.ip)
        try {
            val inetIp = InetSocketAddress(ip, 36969)
            Socket().connect(inetIp, 1000)
            disconnected.visibility = View.INVISIBLE
            findViewById<TextView>(R.id.invalidAdressManual).visibility = View.GONE
            setContentView(R.layout.activity_main)
            connected = true
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
            findViewById<TextView>(R.id.invalidAdressManual).visibility = View.VISIBLE
        }

    }

    fun connect(hostIP: String) {
        try {
            val socket = Socket(hostIP, 36969)
            setContentView(R.layout.activity_main)
            socket.close()
            this.ip = hostIP
            connected = true
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
            invalidAddress.visibility = View.VISIBLE

        }

    }

    fun sendMessage(inst: View) {
        try {
            val client = Socket(ip, 36969)
            client.outputStream.write(resources.getResourceEntryName(inst.id).toByteArray())
            client.close()
        } catch (e: java.lang.Exception) {
            openConnectionScene(inst)
            disconnected.visibility = View.VISIBLE
            connected = false
        }

    }

    fun sniffNetwork(inst: View) {
        noResults.visibility = View.GONE
        refresh.visibility = View.GONE
        findViewById<ProgressWheel>(R.id.loading).visibility = View.VISIBLE
        computers.clear()
        adapter.notifyDataSetChanged()
        thread {
            val base = "192.168."
            for (j in 0..1){
                val realBase = base+j.toString()+"."
                for (i in 1..255) {
                    val ip = realBase + i.toString()
                    val inetIp = InetSocketAddress(ip, 36969)
                    try {
                        Socket().connect(inetIp, 10)
                        Log.e(TAG, ip)
                        computers.add(Computer(ip, if (ip == inetIp.hostName) "" else inetIp.hostName))
                    } catch (e: Exception) {
                    }
                }
            }
            runOnUiThread {
                adapter.notifyDataSetChanged()
                if (computers.size == 0) {
                    noResults.visibility = View.VISIBLE
                }
                findViewById<ProgressWheel>(R.id.loading).visibility = View.INVISIBLE
                refresh.visibility = View.GONE
                findViewById<ImageView>(R.id.refresh).visibility = View.VISIBLE
            }
        }


    }


}


