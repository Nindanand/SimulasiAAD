package com.dicoding.habitapp.ui.countdown

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.IntentCompat.getParcelableExtra
import androidx.lifecycle.ViewModelProvider
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.dicoding.habitapp.R
import com.dicoding.habitapp.data.Habit
import com.dicoding.habitapp.databinding.ActivityCountDownBinding
import com.dicoding.habitapp.notification.NotificationWorker
import com.dicoding.habitapp.utils.HABIT
import com.dicoding.habitapp.utils.HABIT_ID
import com.dicoding.habitapp.utils.HABIT_TITLE
import com.dicoding.habitapp.utils.NOTIF_UNIQUE_WORK

class CountDownActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCountDownBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCountDownBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Count Down"

        val habit = getParcelableExtra(intent, HABIT, Habit::class.java)

        if (habit != null){
            binding.tvCountDownTitle.text = habit.title

            val viewModel = ViewModelProvider(this)[CountDownViewModel::class.java]
            viewModel.setInitialTime(habit.minutesFocus)

            //TODO 10 : Set initial time and observe current time. Update button state when countdown is finished

            //TODO 13 : Start and cancel One Time Request WorkManager to notify when time is up.

            val myWork = OneTimeWorkRequestBuilder<NotificationWorker>()
                .setInputData(
                    workDataOf(
                        HABIT_ID to habit.id,
                        HABIT_TITLE to habit.title
                    )
                )
                .build()

            binding.btnStart.setOnClickListener {
                viewModel.startTimer()
                updateButtonState(true)
            }

            binding.btnStop.setOnClickListener {
                updateButtonState(false)
                viewModel.resetTimer()
            }

            viewModel.eventCountDownFinish.observe(this) {
                if (it) {
                    updateButtonState(!it)
                    WorkManager.getInstance(this)
                        .enqueueUniqueWork(NOTIF_UNIQUE_WORK, ExistingWorkPolicy.REPLACE, myWork)
                }
            }

            viewModel.currentTimeString.observe(this) {
                binding.tvCountDown.text = it
            }
        }
    }


    private fun updateButtonState(isRunning: Boolean) {
        findViewById<Button>(R.id.btn_start).isEnabled = !isRunning
        findViewById<Button>(R.id.btn_stop).isEnabled = isRunning
    }
}