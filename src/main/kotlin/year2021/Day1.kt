package year2021

import readIntListInput

const val testInput = """199
200
208
210
200
207
240
269
260
263"""

fun main() {
    val testList = testInput.lines().map { it.toInt() }
    assert(7L == howManyIncreases(testList))
    println(howManyIncreases(readIntListInput("year2021/day1.txt")))

}

fun howManyIncreases(measurements : List<Int>) : Long {
    var prev : Int? = null
    return measurements.fold(0L) { acc: Long, measurement: Int ->
        try {
            acc + (prev?.let { prev ->
                if(measurement > prev) 1
                else 0
            } ?: 0)
        } finally {
            prev = measurement
        }
    }
}