package year2020

import crossProduct
import readIntListInput
import times

private object Day1 {

    fun calcPart1(list : List<Int>) : Int {
        return (list * list)
            .filter {(one, two) -> one + two == 2020  }
            .map { (one, two) ->
                one * two
            }
            .firstOrNull() ?: error("No two items sum to 2020")
    }

    fun calcPart2(list : List<Int>) : Int {
        return list.crossProduct(list,list)
            .filter {(one, two, three) -> one + two + three == 2020  }
            .map { (one, two, three) ->
                one * two * three
            }
            .firstOrNull() ?: error("No two items sum to 2020")
    }
}

fun main() {
    require(514579 == Day1.calcPart1(readIntListInput("year2020/day1_test.txt")))
    require(538464 == Day1.calcPart1(readIntListInput("year2020/day1.txt")))

    require(241861950 == Day1.calcPart2(readIntListInput("year2020/day1_test.txt")))
    require(278783190 == Day1.calcPart2(readIntListInput("year2020/day1.txt")))
}