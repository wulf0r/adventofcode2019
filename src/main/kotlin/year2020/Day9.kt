package year2020

import readIntListInput
import times


val day9Test =
"""35
20
15
25
47
40
62
55
65
95
102
117
150
182
127
219
299
277
309
576""".trimIndent()
        .lines()
        .map { it.toInt() }

fun main() {
    require(listOf(127) == day9Test.findInvalids(5))
    require(listOf(15,25, 47, 40) == day9Test.findContiguousRange(5))
    val day9Input = readIntListInput("year2020/day9.txt")
    require(listOf(167829540) == day9Input.findInvalids(25))
    require(28045630 == day9Input.findContiguousRange(25).sumMinMax())
}

fun List<Int>.sumMinMax() = when(this.size) {
    0 -> 0
    1 -> this.first()
    else -> this.minOf {it} + this.maxOf {it}
}

fun List<Int>.findContiguousRange(preamble: Int) : List<Int> {
    val invalid = this.findInvalids(preamble).first()
    return (2..this.size).map { range -> this.windowed(range,1, true).filter { it.size >= 2 }.sortedBy { it.size }}.flatten().first { it.sum() == invalid}
}

fun List<Int>.findInvalids(preamble : Int) : List<Int> = this.windowed(preamble + 1).filterNot { check(it.take(preamble), it.last()) }.map { it.last() }

fun check(preambleList : List<Int>, toCheck : Int) = (preambleList * preambleList).map { (one, two) -> one + two }.contains(toCheck)