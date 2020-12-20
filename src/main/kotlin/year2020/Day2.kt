package year2020

import readInput

private val test =
"""1-3 a: abcde
1-3 b: cdefg
2-9 c: ccccccccc
200-99 d: ccccccccc"""

private data class PolicyAndPassword(val min : Int, val max : Int, val char : Char, val password : String) {
    companion object {
        val lineRegex = """(\d*)-(\d*) (\w): (.*)""".toRegex()
        fun parse(line : String) : PolicyAndPassword {
            return lineRegex.matchEntire(line)?.let { matchResult ->
                val (min, max, char, password) = matchResult.destructured
                PolicyAndPassword(min.toInt(), max.toInt(), char.first(), password)
            } ?: error("Regex does not match line $line")
        }
    }
    val validPart1 get() = password.count { it == char } in min..max
    val validPart2 get() = password.filterIndexed { index, _ ->  index == min -1 || index == max - 1}.toCharArray().let { (first, second) ->
        (first == char) xor (second == char)
    }
}
fun main() {
    val testPws = test.split("\n").map { PolicyAndPassword.parse(it) }
    require( listOf("abcde", "ccccccccc") == testPws.filter { it.validPart1 }.map { it.password })

    val day2Pws = readInput("year2020/day2.txt").map { PolicyAndPassword.parse(it) }
    val part1ValidDay2 = day2Pws.filter { it.validPart1 }
    require(645 == part1ValidDay2.size)

    require(737 == day2Pws.filter { it.validPart2 }.size)


}