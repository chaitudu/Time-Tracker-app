package com.example.timetracker.ui

import android.app.TimePickerDialog
import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.timetracker.databinding.FragmentTimerBinding
import java.util.*

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var totalSeconds = 0
    private var remainingSeconds = 0
    private var isPaused = false

    private val handler = Handler(Looper.getMainLooper())

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.btnPickTime.setOnClickListener { showTimePicker() }
        binding.btnStart.setOnClickListener { startTimer() }
        binding.btnPauseResume.setOnClickListener { togglePause() }
        binding.btnStop.setOnClickListener { stopTimer() }
    }

    private fun showTimePicker() {
        val cal = Calendar.getInstance()
        val hour = cal.get(Calendar.HOUR_OF_DAY)
        val minute = cal.get(Calendar.MINUTE)

        TimePickerDialog(requireContext(), { _, h, m ->
            totalSeconds = (h * 3600) + (m * 60)
            remainingSeconds = totalSeconds
            updateTimerUI()
        }, hour, minute, true).show()
    }

    private fun startTimer() {
        if (totalSeconds == 0) return

        isPaused = false
        handler.post(timerRunnable)
    }

    private val timerRunnable = object : Runnable {
        override fun run() {
            if (!isPaused && remainingSeconds > 0) {
                remainingSeconds--
                updateTimerUI()
                handler.postDelayed(this, 1000)
            }

            if (remainingSeconds == 0) {
                timerFinished()
            }
        }
    }

    private fun togglePause() {
        isPaused = !isPaused
        binding.btnPauseResume.text = if (isPaused) "Resume" else "Pause"

        if (!isPaused) handler.post(timerRunnable)
    }

    private fun stopTimer() {
        isPaused = true
        remainingSeconds = totalSeconds
        updateTimerUI()
    }

    private fun timerFinished() {
        playAlarm()
        vibratePhone()

        binding.tvTimer.text = "DONE ✅"
    }

    private fun playAlarm() {
        mediaPlayer = MediaPlayer.create(requireContext(), android.provider.Settings.System.DEFAULT_ALARM_ALERT_URI)
        mediaPlayer?.start()
    }

    private fun vibratePhone() {
        val v = requireContext().getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        v.vibrate(500)
    }

    private fun updateTimerUI() {
        val min = remainingSeconds / 60
        val sec = remainingSeconds % 60
        binding.tvTimer.text = String.format("%02d:%02d", min, sec)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(timerRunnable)
        mediaPlayer?.release()
        _binding = null
    }
}
