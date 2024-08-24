package com.example.testforsmokers.smoke.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.example.testforsmokers.R
import com.example.testforsmokers.smoke.vm.SmokeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Smoke : Fragment(R.layout.fragment_smoke) {

    private val viewModel: SmokeViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSmoke = view.findViewById<Button>(R.id.btnSmoke)
        val tvDay = view.findViewById<TextView>(R.id.tvDay)
        val tvWeek = view.findViewById<TextView>(R.id.tvWeek)
        val tvMonth = view.findViewById<TextView>(R.id.tvMonth)
        val tvTimer = view.findViewById<TextView>(R.id.tvTimer)

        btnSmoke.setOnClickListener {
            viewModel.smoke()
        }

        viewModel.dayCigaretteCount.observe(viewLifecycleOwner) { count ->
            tvDay.text = "Day $count"
        }

        viewModel.weekCigaretteCount.observe(viewLifecycleOwner) { count ->
            tvWeek.text = "Week $count"
        }

        viewModel.monthCigaretteCount.observe(viewLifecycleOwner) { count ->
            tvMonth.text = "Month $count"
        }

        viewModel.timerText.observe(viewLifecycleOwner) { timerText ->
            tvTimer.text = timerText
        }
    }
}