package com.example.kinopoisk

import com.example.kinopoisk.dataclasses.Tune

interface TuneListener {
    fun onTuneCreated(tune: Tune)
}