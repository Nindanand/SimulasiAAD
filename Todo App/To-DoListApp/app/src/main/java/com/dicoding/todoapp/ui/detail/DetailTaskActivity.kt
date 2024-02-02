package com.dicoding.todoapp.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dicoding.todoapp.databinding.ActivityTaskDetailBinding
import com.dicoding.todoapp.ui.ViewModelFactory
import com.dicoding.todoapp.utils.TASK_ID
import com.dicoding.todoapp.data.Task
import com.dicoding.todoapp.utils.DateConverter


class DetailTaskActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTaskDetailBinding
    private lateinit var viewModel: DetailTaskViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO 11 : Show detail task and implement delete action

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory.getInstance(this)
        )[DetailTaskViewModel::class.java]

        val idTask = intent.getIntExtra(TASK_ID, 0)

        viewModel.setTaskId(idTask)

        val observer = Observer<Task> {
            binding.apply {
                detailEdTitle.setText(it.title)
                detailEdDescription.setText(it.description)

                val formattedDueDate = DateConverter.convertMillisToString(it.dueDateMillis)
                detailEdDueDate.setText(formattedDueDate)
            }
        }
        viewModel.task.observe(this, observer)

        binding.btnDeleteTask.setOnClickListener {
            viewModel.task.removeObserver(observer)
            viewModel.deleteTask()
            finish()
        }
    }
}