package com.example.timetracker.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.timetracker.R
import com.example.timetracker.databinding.FragmentSignupBinding
import com.example.timetracker.vm.AuthViewModel

class SignupFragment : Fragment() {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!
    private val vm: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // ✅ Show/Hide password
        setupPasswordToggle()

        // ✅ Sign Up button
        binding.btnSignup.setOnClickListener {
            val email = binding.edEmail.text.toString().trim()
            val password = binding.edPassword.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.signup(email, password)
        }

        // ✅ Observe signup result
        vm.loggedInUserId.observe(viewLifecycleOwner) { uid ->
            if (uid != null) {
                Toast.makeText(requireContext(), "Account Created ✅ Login Now", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack() // Go to Login screen
            }
        }

        // ✅ Already have account → Go to Login

        binding.linkLogin.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupPasswordToggle() {
        var visible = false

        binding.edPassword.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val drawableEnd = 2
                if (event.rawX >= (binding.edPassword.right -
                            binding.edPassword.compoundDrawables[drawableEnd].bounds.width())
                ) {
                    visible = !visible

                    if (visible) {
                        binding.edPassword.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or
                                    android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                        binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye, 0)
                    } else {
                        binding.edPassword.inputType =
                            android.text.InputType.TYPE_CLASS_TEXT or
                                    android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                        binding.edPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_eye_off, 0)
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
