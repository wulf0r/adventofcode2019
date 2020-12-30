package year2020

import permutations
import readIntListInput
import java.math.BigInteger
import kotlin.math.pow


val day10Test = """16
10
15
5
1
11
7
19
6
12
4""".lines().map { it.toInt() }

val day10Test2 = """28
33
18
42
31
14
46
20
48
47
24
23
49
45
19
38
39
11
1
32
25
35
8
17
7
9
4
2
34
10
3""".lines().map { it.toInt() }

fun main() {
    val day10TestList = day10Test.toAdapterList()
    require(day10TestList.valid())
    require(8L == day10TestList.countVariations("day10Test1"))

    val day10TestDists = day10TestList.distributions()
    require(day10TestDists[1] == 7)
    require(day10TestDists[3] == 5)
    require(day10TestDists.day10Part1Result() == 35)

    require(19208L == day10Test2.toAdapterList().countVariations("day10Test2"))

    val day10 = readIntListInput("year2020/day10.txt").toAdapterList()
    println("day10 len: ${day10.size}")
    require(2040 == day10.distributions().day10Part1Result())
    println(day10.countVariations("day10"))

}
fun List<Int>.countVariations(name : String) : Long {
    println("__ $name")
    println(this.take(10))
    val numberToStep = this.windowed(2).map { (first, second) -> second to (second - first) }
    println(numberToStep.map { it.second }.take(10))

    val seqCount = numberToStep.fold(mutableListOf(0)) { acc, (_, step) ->
        when(step) {
            1 -> acc[acc.size-1] = acc.last() + 1
            3 -> if( acc.last() != 0) acc += 0
        }
        acc
    }.dropLast(1)

    println(seqCount.take(10))

    return seqCount.fold(0L) {acc, seq ->
        val possis = when(seq) {
            4 -> 7
            3 -> 4
            2 -> 2
            1 -> 1
            else -> error("UNPOSSIBLE")
        }
        when(acc) {
            0L -> possis.toLong()
            else -> acc * possis.toLong()
        }
    }
}




fun List<Int>.findNumberOfValidArrangements() : Int {
    return this.findValidArrangements().toSet().size
}
fun List<Int>.findValidArrangements() : List<List<Int>> {
    return this.map { digit -> this - digit }
        .filter {
            it.isNotEmpty() && it.contains(0) && it.contains(this.last()) && it.valid()
        }
        .map { validList -> validList.findValidArrangements()}
        .flatten() + listOf(this)
}

fun Map<Int, Int>.day10Part1Result() = this[1]!! * this[3]!!

fun List<Int>.valid() = this.distributions().keys.none { it > 3 }

fun List<Int>.distributions() : Map<Int, Int> {
    return this.windowed(2,1).map { (one, two) -> two - one }.groupBy { it }.map { (distribution, list) -> distribution to list.size }.toMap()
}
fun List<Int>.toAdapterList() = this.sorted().let { listOf(0) + it + (it.last() + 3) }