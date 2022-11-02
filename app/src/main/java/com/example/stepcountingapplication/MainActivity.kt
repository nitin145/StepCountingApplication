package com.example.stepcountingapplication

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*


class MainActivity : AppCompatActivity(), SensorEventListener {
    // Added SensorEventListener the MainActivity class
    // Implement all the members in the class MainActivity
    // after adding SensorEventListener

    // we have assigned sensorManger to nullable
    private var sensorManager: SensorManager? = null

    private var isAccess = false

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    // Creating a variable which will give the running status
    // and initially given the boolean value as false
    private var running = false

    // Creating a variable which will counts total steps
    // and it has been given the value of 0 float
    private var totalSteps = 0f

    // Creating a variable  which counts previous total
    // steps and it has also been given the value of 0 float
    private var previousTotalSteps = 0f
    private var todaySteps = 0

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //Run time Permissions
        //Run time Permissions
        val PERMISSIONS = arrayOf<String>(
            android.Manifest.permission.ACTIVITY_RECOGNITION
        )

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACTIVITY_RECOGNITION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                PERMISSIONS,
                1
            )
        }


        loadData()
        resetSteps()

        // Adding a context of SENSOR_SERVICE aas Sensor Manager
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }


    override fun onResume() {
        super.onResume()
        running = true


        val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)


        if (stepSensor == null) {
            // This will give a toast message to the user if there is no sensor in the device
            Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
        } else {
            // Rate suitable for the user interface
            sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {

        // Calling the TextView that we made in activity_main.xml
        // by the id given to that TextView
        event?.let { eventValue ->
            if (Prefs.init().stepData != null) {
                Prefs.init().stepData?.let {
                    if (!DateUtils.isToday(it.date)) {
                        Prefs.init().stepData = StepsData(
                            date = Calendar.getInstance().timeInMillis,
                            stepsData = eventValue.values[0]
                        )
                        println("StepDataInfo nEWdAYIntitialValue ${  Prefs.init().stepData?.stepsData}")


                    }

                }
            } else {
                Prefs.init().stepData =
                    StepsData(
                        date = Calendar.getInstance().timeInMillis,
                        stepsData = eventValue.values[0]
                    )

                println("StepDataInfo FirstTIMEIntitialValue ${  Prefs.init().stepData?.stepsData}")

            }
            println("StepDataInfo pREVIOUSsTORED ${  Prefs.init().stepData?.stepsData}   Current ${eventValue.values[0]}")
            Prefs.init().stepData?.stepsData?.let {
                var totalSteps=eventValue.values[0].toInt() -it.toInt()
                val tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
                tv_stepsTaken.text = ("$totalSteps")
            }

        }



    }

    fun resetSteps() {
        val tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        tv_stepsTaken.setOnClickListener {
            // This will give a toast message if the user want to reset the steps
            Toast.makeText(this, "Long tap to reset steps", Toast.LENGTH_SHORT).show()
        }

        tv_stepsTaken.setOnLongClickListener {

            previousTotalSteps = totalSteps

            // When the user will click long tap on the screen,
            // the steps will be reset to 0
            tv_stepsTaken.text = 0.toString()

            // This will save the data

            true
        }
    }

    private fun saveData() {
        println("StepDaaaa${Prefs.init().stepData.toString()}")
        val tv_stepsTaken = findViewById<TextView>(R.id.tv_stepsTaken)
        tv_stepsTaken.text = ("$todaySteps")
    }

    private fun loadData() {

        // In this function we will retrieve data
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val savedNumber = sharedPreferences.getFloat("key1", 0f)

        // Log.d is used for debugging purposes
        Log.d("MainActivitysdf", "$savedNumber")

        previousTotalSteps = savedNumber
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // We do not have to write anything in this function for this app
    }

    override fun onPause() {
        super.onPause()
        sensorManager!!.unregisterListener(this)
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                ) {
                    val stepSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
                    if (stepSensor == null) {
                        // This will give a toast message to the user if there is no sensor in the device
                        Toast.makeText(this, "No sensor detected on this device", Toast.LENGTH_SHORT).show()
                    } else {
                        // Rate suitable for the user interface
                        sensorManager?.registerListener(this, stepSensor, SensorManager.SENSOR_DELAY_UI)
                    }
                } else {
                    Toast.makeText(this, "You don't allow the permission to track your steps.Please allow the permission to use this feature", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }
}