/**
 * https://adventofcode.com/2019/day/4#part2
 * Answer: 1135
 */
fun main() {


    val passwords = (172851 until 675869)
        .asSequence()
        .map { it.toString().toCharArray().toList() }
        .filter { it.size == 6 }
        .filter { it == it.sorted() && it != it.distinct() }
        .filter { chars -> chars.groupBy { it }.filterValues { it.size == 2 }.isNotEmpty()}
        .toList()

    println("${passwords.size} / " + passwords.take(5))


}