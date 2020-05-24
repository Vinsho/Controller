package com.example.pcshutdowner

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
    lateinit var progressWheel: ProgressWheel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_look_up)
        StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder().permitAll().build())
        invalidAddress = findViewById(R.id.invalidAdress)
        disconnected = findViewById(R.id.disconnected)
        progressWheel = findViewById(R.id.loading)

        adapter = RecyclerViewAdapter(computers, this)
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }


    fun openManualConnectionScene() {
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
        disconnected.visibility = View.VISIBLE
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
        try {
            val inetIp = InetSocketAddress(ip, 9999)
            Socket().connect(inetIp, 10)
            File(applicationContext.filesDir, "info.txt").writeText(this.ip)
            disconnected.visibility = View.INVISIBLE
            invalidAddress.visibility = View.INVISIBLE
            setContentView(R.layout.activity_main)
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
            invalidAddress.visibility = View.VISIBLE
        }

    }

    fun connect(hostIP: String) {
        try {
            val socket = Socket(hostIP, 9999)
            File(applicationContext.filesDir, "info.txt").writeText(hostIP)
            disconnected.visibility = View.INVISIBLE
            invalidAddress.visibility = View.INVISIBLE
            setContentView(R.layout.activity_main)
            socket.close()
            this.ip = hostIP
        } catch (e: java.lang.Exception) {
            Log.e(TAG, e.toString())
            invalidAddress.visibility = View.VISIBLE

        }

    }

    fun sendMessage(inst: View) {
        try {
            val client = Socket(ip, 9999)
            client.outputStream.write(resources.getResourceEntryName(inst.id).toByteArray())
            client.close()
        } catch (e: java.lang.Exception) {
            openConnectionScene(inst)
            disconnected.visibility = View.VISIBLE
        }

    }


    fun sniffNetwork(inst: View) {
        noResults.visibility = View.GONE
        refresh.visibility = View.GONE
        progressWheel.visibility = View.VISIBLE
        computers.clear()
        adapter.notifyDataSetChanged()
        thread {
            val base = "192.168.1."
            for (i in 1..255) {
                val ip = base + i.toString()
                val inetIp = InetSocketAddress(ip, 9999)
                try {
                    Socket().connect(inetIp, 10)
                    Log.e(TAG, ip)
                    computers.add(Computer(ip, if (ip == inetIp.hostName) "" else inetIp.hostName))
                } catch (e: Exception) {
                }

            }

            runOnUiThread {
                adapter.notifyDataSetChanged()
                if (computers.size == 0) {
                    noResults.visibility = View.GONE
                }
                progressWheel.visibility = View.INVISIBLE
                refresh.visibility = View.GONE
                findViewById<ImageView>(R.id.refresh).visibility = View.VISIBLE
            }
        }


    }


}


