/**
 * https://adventofcode.com/2019/day/4#part1
 * Answer: 1660
 */
fun main() {


    val passwords = (172851 until 675869)
        .asSequence()
        .filter { "$it".length == 6 }
        .filter {
            var last : Char? = null
            var foundSame = false
            for( c in it.toString().toCharArray()) {
                if (last != null) {
                    when {
                        last.toInt() > c.toInt() -> return@filter false
                        last == c -> foundSame = true || foundSame
                    }
                }
                last = c
            }
            foundSame
        }.toList()

    println("${passwords.size} / " + passwords.take(5))


}