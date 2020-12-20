package year2020

import readInput

class Terrain(lines: List<String>) {
    private val map : List<List<Boolean>>
    init {
        map = lines.map { it.trim() }
            .filter { it.isNotEmpty() }
            .map { line ->
                line.map { it == '#' }
            }
    }
    fun get(x : Int,y : Int) : Boolean {
        return  map.getOrNull(y)?.get(x % width) ?: error("map does not have $y rows")
    }
    override fun toString() = map.joinToString(separator = "\n") { it.joinToString(separator = "") {  if(it) "#" else "." }}

    fun countTreesFunctional(slopeX : Int, slopeY : Int) : Long {
        return (0..Int.MAX_VALUE step slopeX).zip( 0 until  height step slopeY).map { (x,y) -> get(x,y) }.count { it }.toLong()
    }
    fun countTreesImperative(slopeX : Int, slopeY : Int) : Int{
        var (x,y) = 0 to 0
        var trees = 0
        while (y < height) {
            if(get(x,y)) {
                trees += 1
            }
            println("$x,$y")
            x += slopeX
            y += slopeY
        }
        return trees
    }
    val height = map.size
    val width = map.first().size
}

fun main() {

    val testTerrain = Terrain(readInput("year2020/day3_test.txt"))
    require(testTerrain.width == 11)
    require(testTerrain.height == 11)

    require(!testTerrain.get(12,0))
    require(testTerrain.get(14,0))
    require(testTerrain.countTreesFunctional(3,1) == 7L)

    val day3Terrain = Terrain(readInput("year2020/day3.txt"))

    require(day3Terrain.countTreesFunctional(3,1) == 259L)

    val product = listOf(1 to 1, 3 to 1, 5 to 1, 7 to 1, 1 to 2)
        .map { (x,y) -> day3Terrain.countTreesFunctional(x,y) }
        .reduceRight { number, acc -> number * acc }

    require(2224913600 == product)
}