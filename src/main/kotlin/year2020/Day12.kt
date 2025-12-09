package year2020

import readInput
import timed
import kotlin.math.absoluteValue
import kotlin.time.ExperimentalTime


enum class Direction(val symbol : Char) {
    north ('N'),
    east ('E'),
    south ('S'),
    west ('W'),

    left ('L'),
    right ('R'),
    forward ('F');

    companion object {
        val headings = listOf(north, east, south, west)
    }
}

data class NavInstruction(val distance : Int, val direction: Direction)

fun List<String>.parseNavInstructions() = this.map { line ->
    val direction = Direction.values().first { dir -> dir.symbol == line.first() }
    val distance = line.drop(1).toInt()
    NavInstruction(distance, direction)
}

val day12Test = """F10
N3
F7
R90
F11""".lines().parseNavInstructions()

class NavComp(private var eastOffset : Int = 0,
              private var northOffset : Int= 0,
              private var waypoint : Boolean = false) {

    private var facing = Direction.east


    private var east = 0
    private var north = 0
    private var south = 0
    private var west = 0

    fun navigate(instructions : List<NavInstruction>) : Int {
        instructions.forEach { inst -> nav(inst) }
        return manhattenDistance
    }
    private fun nav(instruction: NavInstruction) {
        if( waypoint ) {
            when(instruction.direction) {
                Direction.east -> east += instruction.distance
                Direction.west -> west += instruction.distance
                Direction.north -> north += instruction.distance
                Direction.south -> south += instruction.distance
                Direction.forward -> nav(NavInstruction(instruction.distance, facing))
                else -> {
                    when(instruction.distance) {
                        0 -> Unit
                        else -> {
                            val steps = instruction.distance / 90
                            val curIndex = Direction.headings.indexOf(facing)
                            val newIndex = if( instruction.direction == Direction.right) (curIndex + steps) % Direction.headings.size else ((curIndex - steps) + Direction.headings.size) % Direction.headings.size
                            facing = Direction.headings[newIndex]
                        }
                    }
                }
            }
        } else {
            when(instruction.direction) {
                Direction.forward -> (1..instruction.distance).forEach {
                    /**
                    north += waypoint.north
                    east += waypoint.east
                    south += waypoint.south
                    west += waypoint.west
                    **/
                }
                //else -> waypoint.nav(instruction)
            }
        }
    }

    override fun toString(): String {
        return "NavComp(facing=$facing, east=$east, west=$west, north=$north, south=$south, manhattenDistance=$manhattenDistance)"
    }

    val manhattenDistance get() = (east - west).absoluteValue + (north - south).absoluteValue



}

@ExperimentalTime
fun main() {
/**
    require(25 == NavComp().navigate(day12Test))

    println(println(NavComp(waypoint = NavComp(10, 1)).navigate(day12Test)))

    val day12Part1 = readInput("year2020/day12.txt").parseNavInstructions()
    timed("day12Part1") {
        require(2270 == NavComp().navigate(day12Part1))
    }
    timed("day12Part2") {
        println(NavComp(waypoint = NavComp(10, 1)).navigate(day12Part1))
    }
    //day12Part1.map { if(it.direction == Direction.left || it.direction == Direction.right) it.distance else -1  }.distinct().apply { println(this) }
**/

}