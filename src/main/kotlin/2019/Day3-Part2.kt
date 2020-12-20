import kotlin.math.abs

/**
 * https://adventofcode.com/2019/day/3#part2
 * Answer 5672
 */
fun main() {
    println("main")
    val dims = Point(16000, 16000)
    val diagram =  Array(dims.y) {
        Array(dims.x) {
            IntArray(2) {
                0
            }
        }
    }
    println("dia")
    val wires = readInput("day3.txt").map { it.split(",") }.map {
        it.map { WireMovement(it[0], it.substring(1).toInt()) }
    }
    val origin = Point(dims.x / 2, dims.y / 2)

    val intersection = mutableListOf<Int>()
    wires.forEachIndexed { wireIndex, wire ->
        var currentPoint = origin
        var currentSteps = 0
        wire.forEach { movement ->
            val nextPoint = when (movement.direction) {
                'R' -> currentPoint.offsetX(movement.distance)
                'L' -> currentPoint.offsetX(-movement.distance)
                'U' -> currentPoint.offsetY(movement.distance)
                'D' -> currentPoint.offsetY(-movement.distance)
                else -> error(movement.direction)
            }
            for (y in progression(currentPoint.y, nextPoint.y)) {
                for (x in progression(currentPoint.x, nextPoint.x)) {
                    diagram[y][x][0] = diagram[y][x][0] or ((wireIndex + 1) * 2)

                    if (wireIndex == 1 && diagram[y][x][0] == 6) {
                        intersection += currentSteps + diagram[y][x][1]
                    }
                    if (wireIndex == 0) diagram[y][x][1] = currentSteps
                    currentSteps++
                }
            }
            currentSteps -= 1
            currentPoint = nextPoint
        }
    }
    val closest = intersection.sorted()[1]

    println(" ${closest} / $intersection")
}
