import kotlin.math.abs

/**
 * https://adventofcode.com/2019/day/3#part1
 * Answer 731
 */
fun main() {
    val dims = Point(20000, 20000)
    val diagram =  Array(dims.y) {
        Array(dims.x) {0}
    }
    val wires = readInput("day3.txt").map { it.split(",") }.map {
        it.map { WireMovement(it[0], it.substring(1).toInt()) }
    }
    val origin = Point(dims.x / 2, dims.y / 2)

    var intersection = mutableListOf<Int>()
    wires.forEachIndexed { wireIndex, wire ->
        var currentPoint = origin
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
                    diagram[y][x] = diagram[y][x] or ((wireIndex + 1) * 2)
                    if (diagram[y][x] == 6) {
                        intersection.add(abs(y - origin.y) + abs(x - origin.x))
                    }
                }
            }
            currentPoint = nextPoint
        }
    }
    val closest = intersection.sorted()[1]

    println(" ${closest} / $intersection")
}

