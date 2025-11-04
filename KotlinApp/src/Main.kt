import java.io.File
import java.time.LocalDate
import java.util.*

data class Zadanie(val opis: String, var wykonane: Boolean = false)
data class Ocena(val przedmiot: String, val ocena: Double)
data class Wydarzenie(val data: LocalDate, val opis: String)
data class Lekcja(val dzien: String, val przedmiot: String, val godzina: String)

val scanner = Scanner(System.`in`)

val zadania = mutableListOf<Zadanie>()
val oceny = mutableListOf<Ocena>()
val wydarzenia = mutableListOf<Wydarzenie>()
val plan = mutableListOf<Lekcja>()

fun main() {
    println("🎓 Witaj w Szkolnym Asystencie!")
    println("Podaj swoje imię:")
    val imie = scanner.nextLine()
    println("Cześć, $imie! Zaczynajmy ")

    wczytajDaneZPliku()

    while (true) {
        println(
            """
            
            ====== MENU GŁÓWNE ======
            1. Plan lekcji
            2. Lista zadań domowych
            3. Oceny
            4. Kalendarz wydarzeń
            5. Przypomnienia
            6. Statystyki
            7. Zapisz dane
            8. Wyjście
            ==========================
        """.trimIndent()
        )

        when (scanner.nextLine()) {
            "1" -> menuPlanLekcji()
            "2" -> menuZadania()
            "3" -> menuOceny()
            "4" -> menuWydarzenia()
            "5" -> przypomnienia()
            "6" -> statystyki()
            "7" -> zapiszDaneDoPliku()
            "8" -> {
                println("Do zobaczenia, $imie! ")
                zapiszDaneDoPliku()
                return
            }

            else -> println("Nieprawidłowa opcja!")
        }
    }
}

// Plan Lekcji
fun menuPlanLekcji() {
    println("PLAN LEKCJI:")
    println("1. Dodaj lekcję")
    println("2. Usuń lekcję")
    println("3. Pokaż plan")
    when (scanner.nextLine()) {
        "1" -> {
            print("Dzień: "); val dzien = scanner.nextLine()
            print("Przedmiot: "); val przedmiot = scanner.nextLine()
            print("Godzina: "); val godzina = scanner.nextLine()
            plan.add(Lekcja(dzien, przedmiot, godzina))
        }

        "2" -> {
            plan.forEachIndexed { i, lekcja -> println("${i + 1}. ${lekcja.dzien} - ${lekcja.przedmiot} (${lekcja.godzina})") }
            print("Którą lekcję usunąć? "); val index = scanner.nextLine().toIntOrNull()?.minus(1)
            if (index != null && index in plan.indices) plan.removeAt(index)
        }

        "3" -> plan.forEach { println("${it.dzien}: ${it.przedmiot} o ${it.godzina}") }
    }
}


fun menuZadania() {
    println("ZADANIA DOMOWE:")
    println("1. Dodaj zadanie")
    println("2. Oznacz jako wykonane")
    println("3. Pokaż wszystkie")
    when (scanner.nextLine()) {
        "1" -> {
            print("Opis zadania: "); val opis = scanner.nextLine()
            zadania.add(Zadanie(opis))
        }

        "2" -> {
            zadania.forEachIndexed { i, z -> println("${i + 1}. ${z.opis} (${if (z.wykonane) "✅" else "❌"})") }
            print("Które zadanie wykonałeś? "); val index = scanner.nextLine().toIntOrNull()?.minus(1)
            if (index != null && index in zadania.indices) zadania[index].wykonane = true
        }

        "3" -> zadania.forEach { println("- ${it.opis} (${if (it.wykonane) "✅" else "❌"})") }
    }
}

// Oceny
fun menuOceny() {
    println("OCENY:")
    println("1. Dodaj ocenę")
    println("2. Pokaż wszystkie")
    println("3. Średnia ocen")
    when (scanner.nextLine()) {
        "1" -> {
            print("Przedmiot: "); val przedmiot = scanner.nextLine()
            print("Ocena (1-6): "); val ocena = scanner.nextLine().toDoubleOrNull() ?: 0.0
            oceny.add(Ocena(przedmiot, ocena))
        }

        "2" -> oceny.forEach { println("${it.przedmiot}: ${it.ocena}") }
        "3" -> {
            if (oceny.isNotEmpty()) {
                val srednia = oceny.map { it.ocena }.average()
                println("Średnia ocen: %.2f".format(srednia))
            } else println("Brak ocen!")
        }
    }
}

// Eventy
fun menuWydarzenia() {
    println("KALENDARZ WYDARZEŃ:")
    println("1. Dodaj wydarzenie")
    println("2. Pokaż wydarzenia")
    when (scanner.nextLine()) {
        "1" -> {
            print("Data (rrrr-mm-dd): "); val data = LocalDate.parse(scanner.nextLine())
            print("Opis: "); val opis = scanner.nextLine()
            wydarzenia.add(Wydarzenie(data, opis))
        }

        "2" -> wydarzenia.sortedBy { it.data }.forEach { println("${it.data}: ${it.opis}") }
    }
}

// Remindery
fun przypomnienia() {
    val dzis = LocalDate.now()
    val nadchodzace = wydarzenia.filter { it.data.isAfter(dzis) && it.data.isBefore(dzis.plusDays(7)) }
    if (nadchodzace.isEmpty()) println("Brak przypomnień na najbliższy tydzień.")
    else {
        println("Nadchodzące wydarzenia:")
        nadchodzace.forEach { println("${it.data}: ${it.opis}") }
    }
}

// Staty
fun statystyki() {
    println("📊 STATYSTYKI:")
    println("Liczba zadań: ${zadania.size}, wykonane: ${zadania.count { it.wykonane }}")
    println("Liczba ocen: ${oceny.size}")
    if (oceny.isNotEmpty()) println("Średnia ocen: %.2f".format(oceny.map { it.ocena }.average()))
}

// Zapisy i odczyty
fun zapiszDaneDoPliku() {
    val file = File("asystent_dane.txt")
    file.printWriter().use { out ->
        out.println("[ZADANIA]")
        zadania.forEach { out.println("${it.opis};${it.wykonane}") }
        out.println("[OCENY]")
        oceny.forEach { out.println("${it.przedmiot};${it.ocena}") }
        out.println("[WYDARZENIA]")
        wydarzenia.forEach { out.println("${it.data};${it.opis}") }
        out.println("[PLAN]")
        plan.forEach { out.println("${it.dzien};${it.przedmiot};${it.godzina}") }
    }
    println(" Dane zapisane do pliku!")
}

fun wczytajDaneZPliku() {
    val file = File("asystent_dane.txt")
    if (!file.exists()) return

    var sekcja = ""
    file.forEachLine { line ->
        when {
            line.startsWith("[") -> sekcja = line
            sekcja == "[ZADANIA]" -> {
                val dane = line.split(";")
                if (dane.size >= 2) zadania.add(Zadanie(dane[0], dane[1].toBoolean()))
            }
            sekcja == "[OCENY]" -> {
                val dane = line.split(";")
                if (dane.size >= 2) oceny.add(Ocena(dane[0], dane[1].toDouble()))
            }
            sekcja == "[WYDARZENIA]" -> {
                val dane = line.split(";")
                if (dane.size >= 2) wydarzenia.add(Wydarzenie(LocalDate.parse(dane[0]), dane[1]))
            }
            sekcja == "[PLAN]" -> {
                val dane = line.split(";")
                if (dane.size >= 3) plan.add(Lekcja(dane[0], dane[1], dane[2]))
            }
        }
    }
    println(" Dane wczytane z pliku.")
}
