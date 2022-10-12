package com.example.countdown

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.Chronometer

class MainActivity : AppCompatActivity() {

    //variable
    lateinit var countdown: Chronometer     //The countdown
    var running = false                     //Is the countdown running?
    var offset: Long = 0   //The base offset for the countdown
    var input: Long = 0     //The user input for the countdown

    //Key strings for use with the bundle
    val OFFSET_KEY = "offset"
    val RUNNING_KEY = "running"
    val BASE_KEY = "base"
    val INPUT_KEY = "input"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Get a reference to the countdown
        countdown = findViewById<Chronometer>(R.id.countdown)

        countdown.onChronometerTickListener = Chronometer.OnChronometerTickListener { chronometer ->
            if(chronometer.text.toString().equals("00:00",ignoreCase = true)) {
                //Stop the countdown
                countdown.stop()
                //Set running = false
                running = false
            }
        }  //To stop countdown if value 00:00

        // Restore the previous state (Bundle)
        if(savedInstanceState != null){
            offset = savedInstanceState.getLong(OFFSET_KEY)
            running = savedInstanceState.getBoolean(RUNNING_KEY)
            input = savedInstanceState.getLong(INPUT_KEY)
            if(running){
                countdown.base = savedInstanceState.getLong(BASE_KEY)
                countdown.start()
            } else {
                setBaseTime()
            }
        }



        val startButton = findViewById<Button>(R.id.start_button)
        startButton.setOnClickListener{
            if(!running && countdown.text.toString() != "00:00"){
                //Set base time
                setBaseTime()
                //Start the countdown
                countdown.start()
                //Set running true
                running = true
            }
        }

        //The pause button pauses the countdown if it's running
        val pauseButton = findViewById<Button>(R.id.pause_button)
        pauseButton.setOnClickListener {
            if(running){
                //Save offset <-- Reset back down to input
                saveOffset()
                //Stop the countdown
                countdown.stop()
                //Set running = false
                running = false
            }
        }

        //The reset button sets the offset and countdown to input
        val resetButton = findViewById<Button>(R.id.reset_button)
        resetButton.setOnClickListener {
            //Offset set to input
            offset = input
            //Reset countdown to input
            setBaseTime()
        }

        //The add button add secs for offset
        val addButton = findViewById<Button>(R.id.add_button)
        addButton.setOnClickListener {
            offset -= 5000
            setBaseTime()
            input = offset
        }

        //The sub button substract secs for offset
        val subButton = findViewById<Button>(R.id.sub_button)
        subButton.setOnClickListener {
            if(offset <= -5000) {
                offset += 5000
                setBaseTime()
                input = offset
            }
        }

    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putLong(OFFSET_KEY, offset)
        savedInstanceState.putBoolean(RUNNING_KEY, running)
        savedInstanceState.putLong(BASE_KEY, countdown.base)
        savedInstanceState.putLong(INPUT_KEY, input)
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onPause(){
        super.onPause()
        if(running){
            saveOffset()
            countdown.stop()
        }
    }

    override fun onResume(){
        super.onResume()
        if(running){
            setBaseTime()
            countdown.start()
            offset = SystemClock.elapsedRealtime() - input
        }
    }

    private fun saveOffset() {
        offset = SystemClock.elapsedRealtime() - countdown.base
    }

    private fun setBaseTime() {
        countdown.base = SystemClock.elapsedRealtime() - offset
    }
}