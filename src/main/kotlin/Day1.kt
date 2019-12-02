
// part 2 answer: 4951265
fun main() {
    val lines = readInput("day1.txt")
    val totalFuel = lines.map(String::toLong).map(Long::requiredFuel).fold(0L) { acc, value -> acc + value}
    println("total fuel: $totalFuel")

}
fun Long.requiredFuel() : Long {
    val fuel = (toDouble() / 3.0).toLong() - 2
    return if( fuel < 0) 0 else {
        fuel + fuel.requiredFuel()
    }
}