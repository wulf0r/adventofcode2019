package year2020

import groupByBlankLine
import readInput


val testDay6 =
"""abc

a
b
c

ab
ac

a
a
a
a

b""".split("\n")

val day6Input = readInput("year2020/day6.txt")
fun main() {
    var count = testDay6.countAnyAnswered()
    require(count == 11)

    count = day6Input.countAnyAnswered()
    require(6170 == count)


    count = testDay6.countAllAnswered()
    require(6 == count)

    count = day6Input.countAllAnswered()
    require(2947 == count)

}

fun List<String>.countAllAnswered() = this
                .groupByBlankLine()
                .map { list -> list.map { answers -> answers.toList().filterNot { it == ' ' }.distinct()}}
                .map { list ->
                    list.filter { it.isNotEmpty() }.reduce { acc, other ->
                        acc.filter { other.contains(it) }
                    }
                }.sumBy { it.size }

fun List<String>.countAnyAnswered() = this.groupByBlankLine()
                .map { it.joinToString("").toSortedSet().filterNot { it == ' ' } }
                .map { it.size }
                .sum()