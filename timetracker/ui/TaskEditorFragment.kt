package com.example.timetracker.ui

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.timetracker.data.Task
import com.example.timetracker.databinding.FragmentTaskEditorBinding
import com.example.timetracker.reminders.ReminderWorker
import com.example.timetracker.vm.TaskViewModel
import com.example.timetracker.vm.AuthViewModel
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

class TaskEditorFragment : Fragment() {

    private var _binding: FragmentTaskEditorBinding? = null
    private val binding get() = _binding!!

    private val vm: TaskViewModel by activityViewModels()
    private val authVm: AuthViewModel by activityViewModels()

    private var editingId: Long = -1
    private var dueAt: Long? = null
    private var selectedImage: String? = null  // ✅ Store URI string

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editingId = arguments?.getLong(ARG_ID) ?: -1
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskEditorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1001)
        }

        binding.spinnerPriority.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            listOf("Low", "Medium", "High")
        )

        binding.btnDue.setOnClickListener { pickDueDateTime() }

        // ✅ Image Picker Click
        binding.imgTask.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }

        // ✅ Load existing task if editing
        if (editingId > 0) {
            lifecycleScope.launch {
                val task = vm.getById(editingId)
                task?.let {
                    binding.edTitle.setText(it.title)
                    binding.edDesc.setText(it.description)
                    binding.spinnerPriority.setSelection(it.priority)
                    it.dueAt?.let { ts -> binding.txtDue.text = Date(ts).toString() }

                    selectedImage = it.imageUri
                    if (!selectedImage.isNullOrEmpty()) binding.imgTask.setImageURI(Uri.parse(selectedImage))
                }
            }
        }

        authVm.loggedInUserId.observe(viewLifecycleOwner) { uid ->
            if (uid == null) return@observe

            binding.btnSave.setOnClickListener {
                val title = binding.edTitle.text.toString().trim()
                val desc = binding.edDesc.text.toString().trim()
                val pr = binding.spinnerPriority.selectedItemPosition

                val task = Task(
                    id = if (editingId > 0) editingId else 0,
                    title = title,
                    description = desc,
                    priority = pr,
                    dueAt = dueAt,
                    completed = false,
                    userId = uid,
                    imageUri = selectedImage // ✅ Save selected image URI
                )

                lifecycleScope.launch {
                    vm.save(task)
                    scheduleReminderIfNeeded(task)
                }

                parentFragmentManager.popBackStack()
            }
        }
    }

    private fun scheduleReminderIfNeeded(task: Task) {
        val due = task.dueAt ?: return
        val delay = due - System.currentTimeMillis()

        if (delay <= 0) return

        val req = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(ReminderWorker.data(task.title, task.description))
            .build()

        WorkManager.getInstance(requireContext()).enqueue(req)
    }

    private fun pickDueDateTime() {
        val cal = Calendar.getInstance()

        DatePickerDialog(requireContext(), { _, y, m, d ->
            cal.set(y, m, d)

            TimePickerDialog(requireContext(), { _, hh, mm ->
                cal.set(Calendar.HOUR_OF_DAY, hh)
                cal.set(Calendar.MINUTE, mm)
                cal.set(Calendar.SECOND, 0)

                dueAt = cal.timeInMillis
                binding.txtDue.text = Date(dueAt!!).toString()

            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show()

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    // ✅ Handle Image Pick
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == IMAGE_PICK_CODE && data != null) {
            val uri = data.data
            selectedImage = uri.toString()
            binding.imgTask.setImageURI(uri)
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    companion object {
        private const val ARG_ID = "id"
        private const val IMAGE_PICK_CODE = 101

        fun newInstance(id: Long) = TaskEditorFragment().apply {
            arguments = Bundle().apply { putLong(ARG_ID, id) }
        }
    }
}
