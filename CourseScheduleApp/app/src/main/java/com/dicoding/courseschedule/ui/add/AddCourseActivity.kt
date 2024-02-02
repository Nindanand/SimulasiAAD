package com.dicoding.courseschedule.ui.add

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.dicoding.courseschedule.R
import com.dicoding.courseschedule.databinding.ActivityAddCourseBinding
import com.dicoding.courseschedule.util.TimePickerFragment
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCourseActivity : AppCompatActivity(), TimePickerFragment.DialogTimeListener {

    private lateinit var viewModel: AddCourseViewModel
    private lateinit var binding: ActivityAddCourseBinding
    private var startTime = "00:00"
    private var endTime = "00:00"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddCourseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val createFactory = AddCourseViewModelFactory.createFactory(this)
        viewModel = ViewModelProvider(this, createFactory)[AddCourseViewModel::class.java]

        binding.btnStartTime.setOnClickListener {
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "startTime")
        }

        binding.btnEndTime.setOnClickListener {
            val dialogFragment = TimePickerFragment()
            dialogFragment.show(supportFragmentManager, "endTime")
        }

        viewModel.saved.observe(this) {
            if (it.getContentIfNotHandled() == true) finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_add, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_insert -> {
                    viewModel.insertCourse(
                        binding.edCourseName.text.toString(),
                        binding.spDay.selectedItemPosition,
                        startTime,
                        endTime,
                        binding.edLecturer.text.toString(),
                        binding.edNote.text.toString()
                    )
                true
            }
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDialogTimeSet(tag: String?, hour: Int, minute: Int) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
        }

        val formatter = SimpleDateFormat("HH:mm")
        val formattedDate = formatter.format(calendar.time)

        when (tag) {
            "endTime" -> {
                endTime = formattedDate
                binding.tvEndTime.text = endTime
            }
            "startTime" -> {
                startTime = formattedDate
                binding.tvStartTime.text = startTime
            }
        }
    }
}
