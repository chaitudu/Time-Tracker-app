package com.example.timetracker.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.timetracker.data.Task
import com.example.timetracker.databinding.ItemTaskCardBinding
import com.example.timetracker.R


class TaskAdapter(
    private val onToggle: (Task) -> Unit,
    private val onEdit: (Task) -> Unit,
    private val onDelete: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.VH>(DIFF) {

    inner class VH(val b: ItemTaskCardBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemTaskCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(h: VH, pos: Int) {
        val t = getItem(pos)

        h.b.txtTitle.text = t.title
        h.b.txtDesc.text = t.description
        h.b.txtPriority.text = listOf("Low", "Medium", "High")[t.priority]
        h.b.chkDone.isChecked = t.completed

        if (!t.imageUri.isNullOrEmpty())
            h.b.imgTask.setImageURI(Uri.parse(t.imageUri))
        else
            h.b.imgTask.setImageResource(R.drawable.ic_image_placeholder)

        h.b.chkDone.setOnCheckedChangeListener { _, _ -> onToggle(t) }
        h.b.btnEdit.setOnClickListener { onEdit(t) }
        h.b.btnDelete.setOnClickListener { onDelete(t) }
    }

    companion object {
        val DIFF = object : DiffUtil.ItemCallback<Task>() {
            override fun areItemsTheSame(old: Task, new: Task) = old.id == new.id
            override fun areContentsTheSame(old: Task, new: Task) = old == new
        }
    }
}
