package com.chudnovskiy.geoquizapp

import android.util.Log
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"
const val NUMBER_OF_HINTS = 3

class QuizViewModel : ViewModel() {
    var currentIndex = 0
    var isCheater = false
    var hintsUsed = 3

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    fun usingHints(): Int {
        return --hintsUsed
    }
    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex - 1) % questionBank.size
    }

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    /**
     * Функция onCleared() вызывается непосредственно перед уничтожением View- Model.
     */
    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }
}