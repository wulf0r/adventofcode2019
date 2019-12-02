
// part 2 answer: 4951265
fun main() {
    val lines = readInput("day1.txt")
    val totalFuel = lines.map(String::toInt).sumBy(Int::requiredFuel)
    println("total fuel: $totalFuel")

}
fun Int.requiredFuel() : Int {
    val fuel = this / 3 - 2
    return if( fuel < 0) 0 else {
        fuel + fuel.requiredFuel()
    }
}