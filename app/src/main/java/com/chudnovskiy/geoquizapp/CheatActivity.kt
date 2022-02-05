package com.chudnovskiy.geoquizapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


private const val EXTRA_ANSWER_IS_TRUE = "com.chudnovskiy.android.geoquiz.answer_is_true"
const val EXTRA_ANSWER_SHOWN = "com.chudnovskiy.android.geoquiz.answer_shown"

class CheatActivity : AppCompatActivity() {
    private var answerIsTrue = false
    private lateinit var answerTextView: TextView
    private lateinit var apiVersionTextView: TextView
    private lateinit var showAnswerButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)
        supportActionBar?.hide()
        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            setAnswerShownResult(true)
        }
        getDeviceInfo()
        getDeviceAPI()
    }

    private fun getDeviceInfo() {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        val version = Build.VERSION.SDK_INT
        val versionRelease = Build.VERSION.RELEASE

        Log.e(
            "CheatActivity", """manufacturer $manufacturer 
                 model $model 
                 version $version 
                 versionRelease $versionRelease"""
        )
    }

    private fun getDeviceAPI() {
        val androidOS = Build.VERSION.SDK_INT
        apiVersionTextView = findViewById(R.id.api_version_text_view)
        apiVersionTextView.text = getString(R.string.api_level, androidOS.toString())
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}