/**
 * https://adventofcode.com/2019/day/4#part2
 * Answer: 1135
 */
fun main() {


    val passwords = (172851 until 675869)
        .asSequence()
        .filter { "$it".length == 6 }
        .filter {
            var last : Char? = null
            val map = mutableMapOf<Char, Int>()
            for( c in it.toString().toCharArray()) {
                map.compute(c) { _, value ->
                    if( value == null) 1 else value +1
                }
                if (last != null && last.toInt() > c.toInt()) {
                    return@filter false
                }
                last = c
            }
            map.filterValues { it == 2 }.size >= 1
        }.toList()

    println("${passwords.size} / " + passwords.take(5))


}