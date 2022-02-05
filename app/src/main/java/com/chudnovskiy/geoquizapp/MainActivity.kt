package com.chudnovskiy.geoquizapp

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val REQUEST_CODE_CHEAT = 0

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView
    private lateinit var hintsAvailableTextView: TextView
    private lateinit var cheatButton: Button

    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) MainActivity called")
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        quizViewModel.currentIndex = currentIndex

        /*
        val provider: ViewModelProvider = ViewModelProviders.of(this)
        val quizViewModel = provider.get(QuizViewModel::class.java)
        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")
        */
        initViews()
        setClickListners()
        updateQuestion()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState()")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_OK) {
            return
        }
        if (requestCode == REQUEST_CODE_CHEAT) {
            quizViewModel.isCheater = data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun setClickListners() {
        trueButton.setOnClickListener() {
            //            displayToastAboveButton(trueButton, R.string.correct_toast)
            checkAnswer(true)
            setDisableButton()
        }
        falseButton.setOnClickListener {
            //            displayToastAboveButton(falseButton, R.string.incorrect_toast)
            checkAnswer(false)
            setDisableButton()
        }
        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            setEnableButton()
        }
        cheatButton.setOnClickListener {
            val possibilityOfHints = quizViewModel.usingHints()
            hintsAvailableTextView.text = getString(R.string.hints_available, possibilityOfHints.toString())
            if (possibilityOfHints > NUMBER_OF_HINTS) {
                cheatButton.isEnabled = false
                return@setOnClickListener
            }
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            /**
             *  Build.VERSION_CODES
             *  https://developer.android.com/reference/android/os/Build.VERSION_CODES.html
             */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val options = ActivityOptions
                    .makeClipRevealAnimation(it, 0, 0, it.width, it.height)
                startActivityForResult(intent, REQUEST_CODE_CHEAT, options.toBundle())
            } else {
                startActivityForResult(intent, REQUEST_CODE_CHEAT)
            }
        }
    }


    private fun setDisableButton() {
        trueButton.isEnabled = false
        falseButton.isEnabled = false
    }

    private fun setEnableButton() {
        trueButton.isEnabled = true
        falseButton.isEnabled = true
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        try {
            val questionTextResId = quizViewModel.currentQuestionText
            questionTextView.setText(questionTextResId)
        } catch (ex: ArrayIndexOutOfBoundsException) {
            // Регистрация сообщения с уровнем регистрации "error" с трассировкой стека исключений
            Log.e(TAG, "Index was out of bounds", ex)
        }

    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_toast
            userAnswer == correctAnswer -> R.string.correct_toast
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    private fun initViews() {
        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        questionTextView = findViewById(R.id.question_text_view)
        cheatButton = findViewById(R.id.cheat_button)
        hintsAvailableTextView = findViewById(R.id.hints_available_text_view)
    }

    // v is the Button view that you want the Toast to appear above
    // and messageId is the id of your string resource for the message
    // https://stackoverflow.com/questions/2506876/how-to-change-position-of-toast-in-android
    private fun displayToastAboveButton(v: View, messageId: Int) {
        var xOffset = 0
        var yOffset = 0
        val gvr = Rect()
        val parent: View = v.getParent() as View
        val parentHeight: Int = parent.getHeight()
        if (v.getGlobalVisibleRect(gvr)) {
            val root: View = v.getRootView()
            val halfWidth: Int = root.getRight() / 2
            val halfHeight: Int = root.getBottom() / 2
            val parentCenterX: Int = (gvr.right - gvr.left) / 2 + gvr.left
            val parentCenterY: Int = (gvr.bottom - gvr.top) / 2 + gvr.top
            yOffset = if (parentCenterY <= halfHeight) {
                -(halfHeight - parentCenterY) - parentHeight
            } else {
                parentCenterY - halfHeight - parentHeight
            }
            if (parentCenterX < halfWidth) {
                xOffset = -(halfWidth - parentCenterX)
            }
            if (parentCenterX >= halfWidth) {
                xOffset = parentCenterX - halfWidth
            }
        }
        val toast = Toast.makeText(this, messageId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.CENTER, xOffset, yOffset)
        toast.show()
    }
}