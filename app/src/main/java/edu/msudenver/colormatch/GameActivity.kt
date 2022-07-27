package edu.msudenver.colormatch

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 03 - GameActivity (controller) class
 */

import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import kotlinx.coroutines.*
import java.util.*

class GameActivity : AppCompatActivity(), OnColorMatchListener {

    lateinit var db: SQLiteDatabase
    var colorMatch: ColorMatch? = null
    var btnMeaning: Button? = null
    var btnColor: Button? = null
    var btnYes: Button? = null
    var btnNo: Button? = null
    var txtTick: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val dbHelper = DBHelper(this)
        db = dbHelper.writableDatabase

        // TODOd #7: get references to the view objects (meaning and color buttons, yes and no buttons, tick text view)
        btnMeaning = findViewById(R.id.btnMeaning)
        btnColor = findViewById(R.id.btnColor)
        btnYes = findViewById(R.id.btnYes)
        btnNo = findViewById(R.id.btnNo)
        txtTick = findViewById(R.id.txtTick)


        // TODO #8: set the background of the meaning and color buttons to gray
        btnMeaning?.setBackgroundColor(Color.parseColor("#808080"))
        btnColor?.setBackgroundColor(Color.parseColor("#808080"))

        // TODO #9: instantiate a ColorMatch object with ColorMatch.DEFAULT_ROUNDS and suggestedLevel()
        val game = ColorMatch(ColorMatch.DEFAULT_ROUNDS,suggestedLevel())

        // TODO #10: set the listener for your ColorMatch object to current activity



        // TODO #11: run ColorMatch's start as a coroutine the way explained in the instructions
        val scope = CoroutineScope(Dispatchers.Main)
        scope.launch {
            game.start()
        }


        // TODO #12: set onClickListener for the YES button and have it call ColorMatch's checkAnswer passing true
        btnYes?.setOnClickListener{game.checkAnswer(true)}

        // TODO #13: set onClickListener for the NOT button and have it call ColorMatch's checkAnswer passing false
        btnNo?.setOnClickListener {game.checkAnswer(false)}
    }

    // given to you (have fun)
    fun suggestedLevel(): Int {
        var avgEasy = 0.0
        var avgMedium = 0.0
        var avgHard = 0.0
        val cursor = db.query(
            "scores",
            arrayOf<String>("level", "AVG(value)"),
            null,
            null,
            "level",
            null,
            "1"
        )
        with (cursor) {
            while (moveToNext()) {
                val level = cursor.getInt(0)
                val avgScore = cursor.getDouble(1)
                if (level == ColorMatch.LEVEL_EASY)
                    avgEasy = avgScore
                else if (level == ColorMatch.LEVEL_MEDIUM)
                    avgMedium = avgScore
                else
                    avgHard = avgScore
            }
            if (avgMedium >= .9)
                return ColorMatch.LEVEL_HARD
            if (avgEasy >= .9)
                return ColorMatch.LEVEL_MEDIUM
            return ColorMatch.LEVEL_EASY
        }
    }

    // TODO #14: update the views (buttons meaning and color)
    // use setTextColor to change a button's color
    // remember, a challenge is a pair containing the meaning and the color
    // the meaning is a pair with the name of a color (text) and how it should be displayed (color)
    // the color is also a pair with the name a color (text) and how it should be displayed (color)
    // you can get a color from a string using ColorMatch.COLOR_MAP hashtable
    // for example, to get the color for "red" you would use ColorMatch.COLOR_MAP["red"]
    override fun onNewColorMatchRound(
        round: Int,
        challenge: Pair<Pair<String, String>, Pair<String, String>>
    ) {

    }

    // TODO #15: update the view (tick text)
    override fun onColorMatchTick(secondsLeft: Int) {
    }

    // TODO #16: update the database with the score; call finish to resume to main activity
    override fun onColorMatchGameOver(score: Double) {
        try {

            )
            Toast.makeText(
                this,
                "Your score of ${score} was recorded!",
                Toast.LENGTH_SHORT
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                this,
                "Exception when trying to record your score!",
                Toast.LENGTH_SHORT
            ).show()
        }
        finish()
    }
}