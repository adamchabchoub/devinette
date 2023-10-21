package com.example.devinette

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.*
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    private var number = Random.nextInt(1, 101)
    private var expertMode = false
    private var attempts = 0
    private val attemptsList = mutableListOf<String>()

    private lateinit var submit: Button
    private lateinit var result: TextView

    private var countdown: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val instructions = findViewById<TextView>(R.id.instructions)
        val help = findViewById<EditText>(R.id.help)
        submit = findViewById<Button>(R.id.submit)
        result = findViewById<TextView>(R.id.result)
        val list = findViewById<ListView>(R.id.list)
        val radioGroup = findViewById<RadioGroup>(R.id.radioGroup)
        val timer = findViewById<TextView>(R.id.timer)
        val restart = findViewById<Button>(R.id.restart)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, attemptsList)
        list.adapter = adapter

        restart.setOnClickListener {

            number = Random.nextInt(1, 999)
            attempts = 0
            attemptsList.clear()
            adapter.notifyDataSetChanged()
            submit.isEnabled = true
            result.text = ""


            if (expertMode) {
                countdown?.cancel()
                countdown = createCountDownTimer(timer, 21000)
                countdown?.start()
            }
        }
        radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.beginner -> {
                    expertMode = false
                    timer.visibility = View.GONE
                    list.visibility=View.VISIBLE
                    countdown?.cancel()
                    timer.text = ""
                }
                R.id.expert -> {
                    expertMode = true
                    timer.visibility = View.VISIBLE
                    list.visibility= View.GONE
                    if (attempts == 0) {
                        // Start the timer only on the first attempt in expert mode
                        countdown = createCountDownTimer(timer, 21000) // 20 seconds
                        countdown?.start()

                    }
                }
            }
        }

        submit.setOnClickListener {
            val userGuess = help.text.toString().toIntOrNull()
            if (userGuess != null) {
                attempts++
                when {
                    userGuess < number -> {
                        result.text = "The number is higher."
                        attemptsList.add("$userGuess is lower")
                    }
                    userGuess > number -> {
                        result.text = "The number is lower."
                        attemptsList.add("$userGuess is higher")
                    }
                    else -> {
                        result.text =
                            "Congratulations! You guessed the number $number in $attempts attempts."
                        submit.isEnabled = false
                        countdown?.cancel()
                    }
                }
                adapter.notifyDataSetChanged()

            } else {
                result.text = "Invalid input. Please enter a valid number."
            }
            help.text.clear()
        }
    }

    private fun createCountDownTimer(timer: TextView, millisInFuture: Long): CountDownTimer {
        return object : CountDownTimer(millisInFuture, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = millisUntilFinished / 1000
                timer.text = "Time Remaining: ${seconds}s"
            }
            override fun onFinish() {
                timer.text = "Time's up! You failed to guess the number in time."
                submit.isEnabled = false
            }
        }
    }
}
