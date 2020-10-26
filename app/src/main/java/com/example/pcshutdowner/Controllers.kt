package com.example.pcshutdowner

class Controllers {
    val controllers = ArrayList<Int>()

    init {
        //TODO add loading from file
        controllers.add(R.layout.activity_main)
        controllers.add(R.layout.activity_vlc)
    }

    fun next(layout_id:Int): Int {
        val idx = controllers.indexOf(layout_id)
        if (idx+1 == controllers.size){
            return layout_id
        }
        else{
            return controllers.get(idx+1)
        }

    }
    fun prev(layout_id:Int): Int {
        val idx = controllers.indexOf(layout_id)
        if (idx == 0){
            return layout_id
        }
        else{
            return controllers.get(idx-1)
        }

    }
}