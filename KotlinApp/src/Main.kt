import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeParseException
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
var nazwaPliku = "asystent_dane.txt"
// basic main
fun main() {
    println("Witaj w Szkolnym Asystencie!")
    print("Podaj swoje imię: ")
    val imie = scanner.nextLine().trim()
    nazwaPliku = if (imie.isNotBlank()) "asystent_${imie}.txt" else "asystent_dane.txt"
    println("Cześć, ${if (imie.isNotBlank()) imie else "Użytkowniku"}! Zaczynajmy.")

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

        when (scanner.nextLine().trim()) {
            "1" -> {
                menuPlanLekcji()
                clearConsole()
            }
            "2" -> {
                menuZadania()
                clearConsole()
            }
            "3" -> {
                menuOceny()
                clearConsole()
            }
            "4" -> {
                menuWydarzenia()
                clearConsole()
            }
            "5" -> {
                przypomnienia()
                clearConsole()
            }
            "6" -> {
                statystyki()
                clearConsole()
            }
            "7" -> {
                zapiszDaneDoPliku()
                clearConsole()
            }
            "8" -> {
                println("Do zobaczenia, ${if (imie.isNotBlank()) imie else "Użytkowniku"}!")
                zapiszDaneDoPliku()
                return
            }
            else -> println("Nieprawidłowa opcja!")
        }
    }
}
// funkcja odpowiadajaca za plan lekcji
fun menuPlanLekcji() {
    println("PLAN LEKCJI:")
    println("1. Dodaj lekcję")
    println("2. Usuń lekcję")
    println("3. Pokaż plan")
    when (scanner.nextLine().trim()) {
        "1" -> {
            print("Dzień: "); val dzien = scanner.nextLine().trim()
            print("Przedmiot: "); val przedmiot = scanner.nextLine().trim()
            print("Godzina: "); val godzina = scanner.nextLine().trim()
            if (dzien.isNotBlank() && przedmiot.isNotBlank() && godzina.isNotBlank()) {
                plan.add(Lekcja(dzien, przedmiot, godzina))
                println("Dodano lekcję.")
            } else println("Nieprawidłowe dane.")
        }
        "2" -> {
            if (plan.isEmpty()) {
                println("Plan jest pusty.")
                return
            }
            plan.forEachIndexed { i, lekcja -> println("${i + 1}. ${lekcja.dzien} - ${lekcja.przedmiot} (${lekcja.godzina})") }
            print("Którą lekcję usunąć? ")
            val index = scanner.nextLine().toIntOrNull()?.minus(1)
            if (index != null && index in plan.indices) {
                plan.removeAt(index)
                println("Usunięto lekcję.")
            } else println("Nieprawidłowy numer.")
        }
        "3" -> if (plan.isEmpty()) println("Plan jest pusty.") else plan.forEach { println("${it.dzien}: ${it.przedmiot} o ${it.godzina}") }
        else -> println("Nieprawidłowa opcja.")
    }
}
// menu zadan z downem
fun menuZadania() {
    println("ZADANIA DOMOWE:")
    println("1. Dodaj zadanie")
    println("2. Oznacz jako wykonane")
    println("3. Pokaż wszystkie")
    when (scanner.nextLine().trim()) {
        "1" -> {
            print("Opis zadania: ")
            val opis = scanner.nextLine().trim()
            if (opis.isNotBlank()) {
                zadania.add(Zadanie(opis))
                println("Dodano zadanie.")
            } else println("Opis nie może być pusty.")
        }
        "2" -> {
            if (zadania.isEmpty()) {
                println("Brak zadań.")
                return
            }
            zadania.forEachIndexed { i, z -> println("${i + 1}. ${z.opis} (${if (z.wykonane) "✅" else "❌"})") }
            print("Które zadanie wykonałeś? ")
            val index = scanner.nextLine().toIntOrNull()?.minus(1)
            if (index != null && index in zadania.indices) {
                zadania[index].wykonane = true
                println("Zadanie oznaczone jako wykonane.")
            } else println("Nieprawidłowy numer.")
        }
        "3" -> if (zadania.isEmpty()) println("Brak zadań.") else zadania.forEach { println("- ${it.opis} (${if (it.wykonane) "✅" else "❌"})") }
        else -> println("Nieprawidłowa opcja.")
    }
}
// jakies przyjebane ocenki
fun menuOceny() {
    println("OCENY:")
    println("1. Dodaj ocenę")
    println("2. Pokaż wszystkie")
    println("3. Średnia ocen")
    when (scanner.nextLine().trim()) {
        "1" -> {
            print("Przedmiot: "); val przedmiot = scanner.nextLine().trim()
            print("Ocena (np. 4.5): "); val ocena = scanner.nextLine().replace(',', '.').toDoubleOrNull()
            if (przedmiot.isNotBlank() && ocena != null) {
                oceny.add(Ocena(przedmiot, ocena))
                println("Dodano ocenę.")
            } else println("Nieprawidłowe dane.")
        }
        "2" -> if (oceny.isEmpty()) println("Brak ocen.") else oceny.forEach { println("${it.przedmiot}: ${it.ocena}") }
        "3" -> if (oceny.isNotEmpty()) println("Średnia ocen: %.2f".format(oceny.map { it.ocena }.average())) else println("Brak ocen.")
        else -> println("Nieprawidłowa opcja.")
    }
}
// eventy
fun menuWydarzenia() {
    println("KALENDARZ WYDARZEŃ:")
    println("1. Dodaj wydarzenie")
    println("2. Pokaż wydarzenia")
    when (scanner.nextLine().trim()) {
        "1" -> {
            print("Data (rrrr-mm-dd): ")
            val dataString = scanner.nextLine().trim()
            try {
                val data = LocalDate.parse(dataString)
                print("Opis: ")
                val opis = scanner.nextLine().trim()
                if (opis.isNotBlank()) {
                    wydarzenia.add(Wydarzenie(data, opis))
                    println("Dodano wydarzenie.")
                } else println("Opis nie może być pusty.")
            } catch (e: DateTimeParseException) {
                println("Nieprawidłowy format daty.")
            }
        }
        "2" -> if (wydarzenia.isEmpty()) println("Brak wydarzeń.") else wydarzenia.sortedBy { it.data }.forEach { println("${it.data}: ${it.opis}") }
        else -> println("Nieprawidłowa opcja.")
    }
}
// remindery
fun przypomnienia() {
    val dzis = LocalDate.now()
    val nadchodzace = wydarzenia.filter { it.data.isAfter(dzis) && it.data.isBefore(dzis.plusDays(7)) }
    if (nadchodzace.isEmpty()) println("Brak przypomnień na najbliższy tydzień.")
    else nadchodzace.forEach { println("${it.data}: ${it.opis}") }
}
//Staty
fun statystyki() {
    println("STATYSTYKI:")
    println("Liczba zadań: ${zadania.size}, wykonane: ${zadania.count { it.wykonane }}")
    println("Liczba ocen: ${oceny.size}")
    if (oceny.isNotEmpty()) println("Średnia ocen: %.2f".format(oceny.map { it.ocena }.average()))
}
// System zapisow danych
fun zapiszDaneDoPliku() {
    val file = File(nazwaPliku)
    file.printWriter().use { out ->
        out.println("[ZADANIA]")
        zadania.forEach { out.println("${escape(it.opis)};${it.wykonane}") }
        out.println("[OCENY]")
        oceny.forEach { out.println("${escape(it.przedmiot)};${it.ocena}") }
        out.println("[WYDARZENIA]")
        wydarzenia.forEach { out.println("${it.data};${escape(it.opis)}") }
        out.println("[PLAN]")
        plan.forEach { out.println("${escape(it.dzien)};${escape(it.przedmiot)};${escape(it.godzina)}") }
    }
    println("Dane zapisane do pliku: $nazwaPliku")
}
// Fun wczytujaca dane
fun wczytajDaneZPliku() {
    val file = File(nazwaPliku)
    if (!file.exists()) {
        println("Brak pliku z danymi.")
        return
    }
    zadania.clear(); oceny.clear(); wydarzenia.clear(); plan.clear()
    var sekcja = ""
    file.forEachLine { rawLine ->
        val line = rawLine.trim()
        if (line.isEmpty()) return@forEachLine
        if (line.startsWith("[")) {
            sekcja = line
            return@forEachLine
        }
        when (sekcja) {
            "[ZADANIA]" -> {
                val dane = line.split(";")
                if (dane.size >= 2) zadania.add(Zadanie(unescape(dane[0]), dane[1].toBooleanStrictOrNull() ?: false))
            }
            "[OCENY]" -> {
                val dane = line.split(";")
                if (dane.size >= 2) dane[1].replace(',', '.').toDoubleOrNull()?.let {
                    oceny.add(Ocena(unescape(dane[0]), it))
                }
            }
            "[WYDARZENIA]" -> {
                val dane = line.split(";")
                if (dane.size >= 2) try {
                    wydarzenia.add(Wydarzenie(LocalDate.parse(dane[0]), unescape(dane.subList(1, dane.size).joinToString(";"))))
                } catch (e: Exception) {}
            }
            "[PLAN]" -> {
                val dane = line.split(";")
                if (dane.size >= 3) plan.add(Lekcja(unescape(dane[0]), unescape(dane[1]), unescape(dane[2])))
            }
        }
    }
    println("Dane wczytane z pliku: $nazwaPliku")
}
// szyfrowanie i deszyfrowanie znakow specjalnych
fun escape(s: String): String = s.replace("\\", "\\\\").replace(";", "\\;")
fun unescape(s: String): String = s.replace("\\;", ";").replace("\\\\", "\\")
// czyszczenie konsoli po kazdej operacji
fun clearConsole() {
    if (System.getProperty("os.name").contains("win", ignoreCase = true)) {
        try {
            Thread.sleep(2000)
            Runtime.getRuntime().exec("cmd /c cls")
        } catch (e: Exception) {
            println("Nie udało się wyczyścić konsoli.")
        }
    }
}

