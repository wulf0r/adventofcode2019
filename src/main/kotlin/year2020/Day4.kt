package year2020

import readInput

data class Passport(val map : Map<String, String?>) {
    private val byr: String? by map
    private val iyr: String? by map
    private val eyr: String? by map
    private val hgt: String? by map
    private val hcl: String? by map
    private val ecl: String? by map
    private val pid: String? by map
    private val cid: String? by map

    companion object {
        fun parse(lines : List<String>) : List<Passport> {
            return (lines.mapIndexedNotNull { index, line ->
                if (line.isBlank()) index else null
            } + lines.size).fold(0 to mutableListOf<String>()) { (start, list), end ->
                list.add(lines.subList(start, end).joinToString(" "))
                end to list
            }.second.map { passportLine ->
                passportLine
                    .trim()
                    .split(" ")
                    .map { it.split(":") }
                    .map { (key, value) -> key.trim() to value.trim() }
                    .toMap()
                    .let { map -> Passport(map.withDefault { null }) }
            }
        }
    }

    val validPart1 = listOf(byr, iyr, eyr, hgt, hcl, ecl, pid).none { it == null }

    val validByr get() = byr!!.length == 4 && (1920 .. 2002).contains(byr!!.toInt())
    val validIyr get() = iyr!!.length == 4 && (2010 .. 2020).contains(iyr!!.toInt())
    val validEyr get() = eyr!!.length == 4 && (2020 .. 2030).contains(eyr!!.toInt())

    val validHgt get() =  """(\d*)(cm|in)""".toRegex().matchEntire(hgt!!)?.let { matchResult ->
        val (height, unit) = matchResult.destructured
        when(unit) {
            "in" -> (59 .. 76).contains(height.toInt())
            else -> (150 .. 193).contains(height.toInt())
        }
    } ?: false

    val validHcl get() = """#[0-9a-f]{6}""".toRegex().matches(hcl!!)
    val validEcl get() = "amb blu brn gry grn hzl oth".split(" ").contains(ecl!!)
    val validPid get() = """\d{9}""".toRegex().matches(pid!!)


    val validPart2 = validPart1 && validByr && validIyr && validEyr && validHgt && validHcl && validEcl && validPid

}

fun main() {
    val testPassports = Passport.parse(readInput("year2020/day4_test.txt"))
    require(testPassports[0].validPart1)
    require(testPassports[2].validPart1)
    require(!testPassports[3].validPart1)

    val day4Part1Passports = Passport.parse(readInput("year2020/day4.txt"))
    require(254 == day4Part1Passports.count { it.validPart1 })

    val invalidPassports = Passport.parse(readInput("year2020/day4_invalid.txt"))
    require(invalidPassports.none { it.validPart2 })


    val validPassports = Passport.parse(readInput("year2020/day4_valid.txt"))
    require(validPassports.all { it.validPart2 })

    val day4Passports = Passport.parse(readInput("year2020/day4.txt"))
    require(184 == day4Passports.count { it.validPart2 })

}