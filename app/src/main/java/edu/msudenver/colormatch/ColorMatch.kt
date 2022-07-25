package edu.msudenver.colormatch

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 03 - ColorMatch (model) class
 */

import android.graphics.Color
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.random.Random

interface OnColorMatchListener {
    fun onNewColorMatchRound(round: Int, challenge: Pair<Pair<String, String>, Pair<String, String>>)
    fun onColorMatchTick(secondsLeft: Int)
    fun onColorMatchGameOver(score: Double)
}

class ColorMatch(
    rounds: Int,
    level: Int) {

    companion object {
        const val LEVEL_EASY     = 0
        const val LEVEL_MEDIUM   = 1
        const val LEVEL_HARD     = 2
        const val MIN_ROUNDS     = 3
        const val MAX_ROUNDS     = 15
        const val DEFAULT_ROUNDS = 10

        val COLOR_MAP = hashMapOf(
            "red"       to Color.RED,
            "green"     to Color.GREEN,
            "blue"      to Color.BLUE,
            "yellow"    to Color.YELLOW,
            "cyan"      to Color.CYAN,
            "magenta"   to Color.MAGENTA,
            "orange"    to Color.parseColor("#ffa500"),
            "purple"    to Color.parseColor("#800080"),
            "pink"      to Color.parseColor("#ffc0cb"),
            "brown"     to Color.parseColor("#663300"),
            "black"     to Color.BLACK
        )
    }

    var level = if (level in LEVEL_EASY..LEVEL_HARD) level else LEVEL_EASY
        set(value) {
            if (value in LEVEL_EASY..LEVEL_HARD) value else field
        }

    var rounds = if (rounds < MIN_ROUNDS || rounds > MAX_ROUNDS) DEFAULT_ROUNDS else rounds
        set (value) {
            if (rounds in MIN_ROUNDS..MAX_ROUNDS)
                value
            else
                field
        }

    private var correct  = 0
    private var challenge: Pair<Pair<String, String>, Pair<String, String>>? = null
    private var onColorMatchListener: OnColorMatchListener? = null

    fun setOnColorMatchListener(onColorMatchListener: OnColorMatchListener) {
        this.onColorMatchListener = onColorMatchListener
    }

    @Synchronized fun checkAnswer(answer: Boolean): Boolean {
        val correctAnswer = challenge?.first?.first == challenge?.second?.second
        challenge = null
        val result = correctAnswer == answer
        if (result) correct++
        return result
    }

    @Synchronized fun updateChallenge(newChallenge: Pair<Pair<String, String>, Pair<String, String>>) {
        challenge = newChallenge
    }

    @Synchronized fun getChallenge(): Pair<Pair<String, String>, Pair<String, String>>? {
        return challenge
    }

    private fun getRandomColor(): String {
        val colors = COLOR_MAP.keys
        return colors.elementAt(Random.nextInt(colors.size))
    }

    private fun getTicksPerLevel(): Int {
        if (level == LEVEL_EASY)
            return 7
        else if (level == LEVEL_MEDIUM)
            return 5
        else
            return 3
    }

    suspend fun start() {
        var round = 0
        while (true) {
            round += 1
            if (round > rounds)
                break
            val meanFirst   = getRandomColor()
            val meanSecond  = getRandomColor()
            val colorFirst  = getRandomColor()
            var colorSecond = meanFirst // assume answer yes
            // 50% chance to change answer to no
            if (Random.nextInt(2) == 1) {
                while (true) {
                    colorSecond = getRandomColor()
                    if (colorSecond != meanFirst)
                        break
                }
            }
            val newChallenge = Pair(
                Pair(meanFirst, meanSecond),
                Pair(colorFirst, colorSecond)
            )
            updateChallenge(newChallenge)
            onColorMatchListener?.onNewColorMatchRound(round, newChallenge)
            var ticks = getTicksPerLevel()
            while (true) {
                onColorMatchListener?.onColorMatchTick(ticks)
                delay(1000)
                // was the challenge answered?
                if (getChallenge() == null)
                    break
                // timeout?
                ticks -= 1
                if (ticks == 0)
                    break
            } // end while (ticks)
        } // end while (rounds)
        val score = correct.toDouble() / rounds
        onColorMatchListener?.onColorMatchGameOver(score)
    }
}