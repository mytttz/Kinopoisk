package com.example.kinopoisk

import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher

// Класс, реализующий обработчик с задержкой
class DebounceTextWatcher(private val onDebounced: (String) -> Unit) : TextWatcher {
    private val handler = Handler(Looper.getMainLooper())
    private var lastQuery = ""

    // Время задержки
    private val debounceDelay = 1000L

    // Вызывается при изменении текста
    override fun afterTextChanged(editable: Editable?) {
        handler.removeCallbacksAndMessages(null)
        val newText = editable?.toString() ?: ""
        // Проверяем, изменился ли текст
        if (newText != lastQuery) {
            lastQuery = newText
            // Поставить задержку перед выполнением действия
            handler.postDelayed({
                onDebounced.invoke(newText)
            }, debounceDelay)
        }
    }

    // Остальные методы интерфейса TextWatcher
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
}
