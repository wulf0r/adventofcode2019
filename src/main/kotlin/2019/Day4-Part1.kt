/**
 * https://adventofcode.com/2019/day/4#part1
 * Answer: 1660
 */
fun main() {


    val passwords = (172851 until 675869)
        .asSequence()
        .map(Int::toString)
        .filter { it.length == 6 }
        .filter { it.toCharArray().asList().let { chars -> chars == chars.sorted() && chars != chars.distinct() } }
        .toList()

    println("${passwords.size} / " + passwords.take(5))


}