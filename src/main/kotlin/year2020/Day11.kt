package year2020

import adjacentPoints
import getOrNull
import mapIndexed2d
import pmapIndexed2d
import readInput
import timed
import valuesForIndices
import pointsOnAxes
import kotlin.time.ExperimentalTime

val day11Test1 = """L.LL.LL.LL
LLLLLLL.LL
L.L.L..L..
LLLL.LL.LL
L.LL.LL.LL
L.LLLLL.LL
..L.L.....
LLLLLLLLLL
L.LLLLLL.L
L.LLLLL.LL""".lines()

val day11Test2 = """.............
.L.L.#.#.#.#.
.............""".lines()

val day11Test3 = """.##.##.
#.#.#.#
##...##
...L...
##...##
#.#.#.#
.##.##."""

class SeatingArea(val area : List<List<Seat>>, _neighbors : List<List<List<Pair<Int, Int>>>>? = null, val changed : Boolean) {
    constructor(_area : List<String>) : this(_area.map { line -> line.map { Seat.fromSymbol(it) }}, null, true)

    private val neighbors : List<List<List<Pair<Int, Int>>>>
    init {
        neighbors = _neighbors
            ?: area.mapIndexed2d { x, y, value ->
                area.pointsOnAxes(x,y)
                    .mapNotNull { it.second.firstOrNull { (x,y) -> area.getOrNull(x,y) != Seat.floor } }
                    .toList()
            }
    }



    enum class Seat(val symbol : Char) {
        empty('L'),
        occupied('#'),
        floor('.');

        companion object {
            fun fromSymbol(symbol: Char): Seat {
                return values().firstOrNull { it.symbol == symbol } ?: error("No state for symbol '$symbol'")
            }
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SeatingArea

        if (area != other.area) return false

        return true
    }

    override fun hashCode(): Int {
        return area.hashCode()
    }

    override fun toString(): String {
        return area.joinToString("\n") { rows -> rows.map { it.symbol }.joinToString("") }
    }

    fun applyRulesPart1() : SeatingArea {
        var changed = false
        val newArea = area.mapIndexed2d { x, y, seat ->
            when(seat) {
                Seat.empty -> if(noAdjacentOccupied(x,y)) { Seat.occupied } else seat
                Seat.occupied -> if( fourOrMoreAdjacentOccupied(x,y)) { Seat.empty } else seat
                else -> seat
            }.apply {
                changed = changed || seat != this
            }
        }
        return SeatingArea(newArea, neighbors, changed)
    }

    private fun noAdjacentOccupied(x : Int, y : Int) = area.adjacentPoints(x,y).valuesForIndices(area).none { it == Seat.occupied }
    private fun fourOrMoreAdjacentOccupied(x : Int, y : Int) = area.adjacentPoints(x,y).valuesForIndices(area).count { it == Seat.occupied } >= 4

    fun applyRulesPart2() : SeatingArea {
        var changed = false
        val newArea = area.mapIndexed2d { x, y, seat ->
            when(seat) {
                Seat.empty -> if(noOccupiedVisible(x,y)) Seat.occupied else seat
                Seat.occupied -> if(fiveOrMoreVisibleOccupied(x,y)) Seat.empty else seat
                else -> seat
            }.apply {
                changed = changed || seat != this
            }
        }
        return SeatingArea(newArea, neighbors, changed)
    }

    fun nearestVisiblesOccupied(x : Int, y : Int) : List<Seat> {
        return neighbors[y][x].map {(x,y) -> area[y][x] }.filter { it == Seat.occupied }
    }

    private fun noOccupiedVisible(x : Int, y : Int) : Boolean = nearestVisiblesOccupied(x,y).isEmpty()
    private fun fiveOrMoreVisibleOccupied(x : Int, y : Int) : Boolean = nearestVisiblesOccupied(x,y).size >= 5

    fun countOccupied() = area.flatten().count { it == Seat.occupied }
}

@ExperimentalTime
fun main() {

    require(SeatingArea(day11Test2).nearestVisiblesOccupied(1,1).count() == 0)

    timed("day11Test") {
        val day11Test1 = SeatingArea(day11Test1)
        require(37 == day11Test1.applyRulesUntilStable(SeatingArea::applyRulesPart1).countOccupied())
        require(26 == day11Test1.applyRulesUntilStable(SeatingArea::applyRulesPart2).countOccupied())
    }

    val day11 = SeatingArea(readInput("year2020/day11.txt"))
    timed("day11 part 1") {
        require(2152 == day11.applyRulesUntilStable(SeatingArea::applyRulesPart1).countOccupied())
    }
    timed("day11 part 2") {
        require(1937 == day11.applyRulesUntilStable(SeatingArea::applyRulesPart2).countOccupied())
    }
}
fun SeatingArea.applyRulesUntilStable(func : (SeatingArea) -> SeatingArea, iteration : Int = 0, debug : Boolean = false) : SeatingArea {
    if( debug ) {
        println("iteration $iteration")
        println(this)
    }
    val other = func(this)
    return if( !other.changed) {
        //println("Final seating after iteration #$iteration")
        //println(other)
        other
    }
    else other.applyRulesUntilStable(func, iteration + 1)
}