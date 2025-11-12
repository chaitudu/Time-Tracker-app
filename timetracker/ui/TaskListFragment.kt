package com.example.timetracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timetracker.R
import com.example.timetracker.data.Task
import com.example.timetracker.databinding.FragmentTaskListBinding
import com.example.timetracker.util.SessionManager
import com.example.timetracker.vm.AuthViewModel
import com.example.timetracker.vm.TaskViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {

    private var _binding: FragmentTaskListBinding? = null
    private val binding get() = _binding!!

    private val vm: TaskViewModel by activityViewModels()
    private val authVm: AuthViewModel by activityViewModels()

    private lateinit var adapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true) // ✅ enable menu
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTaskListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // ✅ Toolbar setup
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)

        adapter = TaskAdapter(
            onToggle = { vm.toggleComplete(it) },
            onEdit = { openEditor(it) },
            onDelete = { vm.delete(it) }
        )

        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        )

        binding.fabAdd.setOnClickListener { openEditor(null) }

        // ✅ Restore saved session
        if (authVm.loggedInUserId.value == null) {
            val savedId = SessionManager(requireContext()).getUserId()
            authVm.loggedInUserId.postValue(savedId)
        }

        // ✅ Load tasks after login
        authVm.loggedInUserId.observe(viewLifecycleOwner) { uid ->
            if (uid == null) return@observe

            viewLifecycleOwner.lifecycleScope.launch {
                vm.tasksFor(uid).collectLatest { list ->
                    adapter.submitList(list)
                    binding.empty.root.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_task_list, menu) // ✅ attach logout menu
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                authVm.logout()
                SessionManager(requireContext()).logout()

                requireActivity().finish()
                startActivity(Intent(requireContext(), AuthActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun openEditor(task: Task?) {
        val bundle = Bundle().apply { putLong("id", task?.id ?: -1) }
        findNavController().navigate(R.id.action_taskListFragment_to_taskEditorFragment, bundle)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
