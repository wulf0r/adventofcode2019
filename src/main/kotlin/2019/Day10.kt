import kotlin.math.atan

fun main() {
   Day10.Part1.calc()
}

val test1 = """    .#..#
    .....
    #####
    ....#
    ...##""".trimIndent()

val test2 =
"""......#.#.
#..#.#....
..#######.
.#.#.###..
.#..#.....
..#....#.#
#..#....#.
.##.#..###
##...#..#.
.#....####"""

val test3 =
"""#.#...#.#.
.###....#.
.#....#...
##.#.#.#.#
....#.#.#.
.##..###.#
..#...##..
..##....##
......#...
.####.###"""

val test4 =
""".#..#..###
####.###.#
....###.#.
..###.##.#
##.##.#.#.
....###..#
..#.#..#.#
#..#.#.###
.##...##.#
.....#.#.."""

val test5=
""".#..##.###...#######
##.############..##.
.#.######.########.#
.###.#######.####.#.
#####.##.#.##.###.##
..#####..#.#########
####################
#.####....###.#.#.##
##.#################
#####.##.###..####..
..######..##.#######
####.##.####...##..#
.#####..#.######.###
##...#.##########...
#.##########.#######
.####.#.###.###.#.##
....##.##.###..#####
.#.#.###########.###
#.#.#.#####.####.###
###.##.####.##.#..##"""

val input = """#..#.#.###.#...##.##....
.#.#####.#.#.##.....##.#
##..#.###..###..#####..#
####.#.#..#....#..##.##.
.#######.#####...#.###..
.##...#.#.###..###.#.#.#
.######.....#.###..#....
.##..##.#..#####...###.#
#######.#..#####..#.#.#.
.###.###...##.##....##.#
##.###.##.#.#..####.....
#.#..##..#..#.#..#####.#
#####.##.#.#.#.#.#.#..##
#...##.##.###.##.#.###..
####.##.#.#.####.#####.#
.#..##...##..##..#.#.##.
###...####.###.#.###.#.#
..####.#####..#####.#.##
..###..###..#..##...#.#.
##.####...##....####.##.
####..#..##.#.#....#..#.
.#..........#..#.#.####.
###..###.###.#.#.#....##
########.#######.#.##.##"""
object Day10 {
    fun test(input : String, expectedPoint : Point, expectedVisible : Int) {
        val counted = fromText(input).apply { println(this) }.let {field ->
            field.mapValues { (point,_) ->
                field.keys.filter{it != point}.sortedBy { ( it - point) }.map { other ->
                    Slope(point.slope(other), other.x < point.x) to other
                }.groupBy({ it.first},{it.second})
            }
        }
        counted
            .filter {  it.key == Point(1,2)}
            .forEach { (point, data) ->

        }
        val baseStation = counted.maxBy { it.value.values.size }!!.apply {
            val count = this.value.map { it.value.first() }.toSet().size
            require(this.key == expectedPoint)
            require((expectedVisible == count))
        }
        val inclinationToPoints = baseStation.value.entries.map { (slope, points) ->
            slope.asInclination to points
        }.sortedBy {  it.first  }.toMap().apply {
            forEach { println("${it.key} : ${it.value}")}
        }

        var workset = inclinationToPoints.toMutableMap()

        var point : Point? = null
        var vaporized = 0
        for( i in 0 .. Int.MAX_VALUE) {
            val idx = i % workset.size
            val inclination = workset.keys.toList()[idx]
            var points = workset[inclination]?.sortedBy { baseStation.key - it } ?: error("key vanished")
            if( points.isEmpty()) continue
            else {
                point = points.first()
                println("$idx $point @ $inclination")
                workset[inclination] = points.drop(1)
                vaporized++
            }

            if( workset.count { it.value.isNotEmpty() } == 0 || vaporized == 200) break
        }

        println("last point vaporized point was $point for result ${point!!.x * 100 + point!!.y}")
        val pointToInclination = inclinationToPoints.map {(inclination , points ) ->
            points.map { point ->
                    point to inclination
            }
        }.flatten().toMap()

        val width = 24
        val height = 24
        val asString = (-1 until height).map { y ->
            if( y == -1 ) (-1 until width).joinToString(" ") { x -> x.render() }
            else (-1 until width).map { x ->
                val currentPoint = Point(x,y)
                when {
                    x == -1 -> y.render()
                    currentPoint == baseStation.key -> "BBB"
                    pointToInclination.containsKey(currentPoint) -> pointToInclination[currentPoint]?.render() ?: error("key not there")
                    else -> " - "
                }
            }.joinToString(" ")

        }.joinToString("\n")
        println(asString)


    }
    object Part1 {

        fun calc() {
            test(test1, Point(3,4), 8)
            test(test2, Point(5,8), 33)
            test(test3, Point(1,2), 35)
            test(test4, Point(6,3), 41)
            test(test5, Point(11,13), 210)
            test(input, Point(20,21), 247)
        }
    }

    object Part2 {

    }

    private fun fromText(text : String ) : AsteroidField {
        return text.lines().mapIndexed { y, row ->
            row.mapIndexedNotNull { x, col ->
                when(col) {
                    '.' -> null
                    '#' -> Point(x, y) to 1
                    else -> error("unhandled char col")
                }
            }
        }.flatten().toMap()
    }
}
data class Slope(val slope : Double, val leftSide : Boolean) {

    val asInclination = atan(slope) * 180.0 / Math.PI + 90 + (if(leftSide) 180 else 0)
}


fun <T> Pair<IntProgression, IntProgression>.map(transform :(Int, Int) -> T) : List<T> {
    return this.first.map { a ->
        this.second.map { b ->
            transform(a, b)
        }
    }.flatten()
}

fun Int.render() = "%03d".format(this)
fun Double.render() = when(this) {
        Double.POSITIVE_INFINITY -> "+IN"
        Double.NEGATIVE_INFINITY -> "-IN"
        Double.NaN -> "NAN"
        else -> "%03d".format(this.toInt())
    }

typealias SearchAxis = (origin : Point) -> List<Point>

typealias AsteroidField = Map<Point, Int>

