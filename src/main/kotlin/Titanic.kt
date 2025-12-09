fun main() {
    val deathList = readInput("titanic.txt")
        .map { it.trim() }
        .filterNotBlank()
        .filter { it.contains('†') }
        .map { it.split(",")[2].trim().toIntOrNull()  ?: error("can parse age from $it") }

    println("deaths: ${deathList.size} average age: ${deathList.average()} max age: ${deathList.maxOrNull()} min age: ${deathList.minOrNull()}}")
}