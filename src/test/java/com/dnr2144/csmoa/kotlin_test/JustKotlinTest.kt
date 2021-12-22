package com.dnr2144.csmoa.kotlin_test

import org.junit.jupiter.api.Test

class JustKotlinTest {

    // [87, 88, 21, 20, 10, 9]: progress
    // [10, 4, 40, 38, 80, 90]: speeds
    // [2, 3, 2, 3, 2, 2] -> [1, 5]
    @Test
    fun listTest() {
        val progresses = mutableListOf(87, 88, 21, 20, 10, 9)
        val speeds = mutableListOf(10, 4, 40, 38, 80, 90)
        val neededDays = mutableListOf<Int>()
        val answer = mutableListOf<Int>()

        progresses.forEachIndexed { index, progress ->
            val left = 100 - progress
            var needed = left / speeds[index]
            if (needed == 0 || (left % speeds[index]) != 0) {
                needed++
            }
            neededDays.add(needed)
        }

        println("before neededDays: $neededDays")
        var count = 0
        var maxNeededDay = 0
        for (i in 0 until neededDays.size) {
            if (i == neededDays.size - 1 || neededDays[i] < neededDays[i + 1]) {
                answer.add(++count)
                count = 0
            } else {
                count++
                maxNeededDay = if (maxNeededDay < neededDays[i]) neededDays[i] else maxNeededDay
            }
            println("now neededDay: ${neededDays[i]}, answer: $answer")
        }
        println("after neededDays: $neededDays, answer: $answer")
    }


}