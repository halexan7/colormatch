package edu.msudenver.colormatch

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s):
 * Description: App 03 - Score (model) class
 */

import java.util.*

class Score(
    var id: Int,
    var date: Date,
    var level: Int,
    var value: Double): Comparable<Score> {

    override fun compareTo(other: Score): Int {
        if (level == other.level)
            return other.date.compareTo(date)
        return other.level - level
    }
}