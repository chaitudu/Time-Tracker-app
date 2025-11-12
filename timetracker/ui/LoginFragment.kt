package com.example.timetracker.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.timetracker.databinding.FragmentLoginBinding
import com.example.timetracker.vm.AuthViewModel
import com.example.timetracker.R

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val vm: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // ✅ Login Button Click
        binding.btnLogin.setOnClickListener {
            val email = binding.edEmail.text.toString().trim()
            val pass = binding.edPassword.text.toString().trim()

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(requireContext(), "Enter Email & Password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            vm.login(email, pass)
        }

        // ✅ Go to Signup
        binding.linkSignup.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(com.example.timetracker.R.id.auth_container, SignupFragment())
                .addToBackStack(null)
                .commit()
        }

        // ✅ Observe login state
        vm.loggedInUserId.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                startActivity(Intent(requireContext(), MainActivity::class.java))
                requireActivity().finish()
            }
        }

        // ✅ Show/Hide Password Eye Toggle
        setupPasswordToggle()
    }

    private fun setupPasswordToggle() {
        var isVisible = false

        binding.edPassword.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableRight = 2 // end drawable index

                if (event.rawX >= (binding.edPassword.right -
                            binding.edPassword.compoundDrawables[drawableRight].bounds.width())) {

                    isVisible = !isVisible

                    if (isVisible) {
                        binding.edPassword.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or
                                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.ic_eye, 0
                        )
                    } else {
                        binding.edPassword.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or
                                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(
                            0, 0, R.drawable.ic_eye_off, 0
                        )
                    }
                    binding.edPassword.setSelection(binding.edPassword.text!!.length)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}
