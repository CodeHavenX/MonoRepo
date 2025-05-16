package com.cramsan.tokenmanager.lib.state

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class TokenModel(
    name: String = "",
    power: Int = 1,
    toughness: Int = 1,
    keywords: List<Keyword> = emptyList(),
    text: List<String> = emptyList(),
) {

    private val _name = mutableStateOf(name)
    val name: State<String>
        get() = _name

    private val _power = mutableStateOf(power)
    val power: State<Int>
        get() = _power

    private val _toughness = mutableStateOf(toughness)
    val toughness: State<Int>
        get() = _toughness

    private val _keywords = mutableStateOf(keywords)
    val keywords: State<List<Keyword>>
        get() = _keywords

    private val _text = mutableStateOf(text)
    val text: State<List<String>>
        get() = _text

    fun changeToughness(change: Int) {
        _toughness.value += change
    }

    fun changePower(change: Int) {
        _power.value += change
    }

    fun setName(name: String) {
        _name.value = name
    }

    fun addKeyword(keyword: Keyword) {
        _keywords.value = (_keywords.value + keyword).distinct()
    }

    fun removeKeyword(keyword: Keyword) {
        _keywords.value = _keywords.value - keyword
    }

    fun addText(text: String) {
        _text.value = (_text.value + text).distinct()
    }

    fun removeText(text: String) {
        _text.value = _text.value - text
    }
}
