import java.io.File

// ===== データ =====
data class Shift(
    val id: Int,
    val name: String,
    val day: String,
    val start: String,
    val end: String
)

// ===== 読み込み =====
fun loadShifts(fileName: String): MutableList<Shift> {
    val list = mutableListOf<Shift>()
    val file = File(fileName)

    if (file.exists()) {
        file.forEachLine { line ->
            val parts = line.split(",")
            if (parts.size == 5) {
                val shift = Shift(
                    parts[0].toIntOrNull() ?: return@forEachLine,
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4]
                )
                list.add(shift)
            }
        }
    }
    return list
}

// ===== 保存（1件追加）=====
fun saveShift(fileName: String, shift: Shift) {
    File(fileName).appendText(
        "${shift.id},${shift.name},${shift.day},${shift.start},${shift.end}\n"
    )
}

// ===== ID発行 =====
fun getNextId(fileName: String): Int {
    val file = File(fileName)
    if (!file.exists()) return 1

    var maxId = 0
    file.forEachLine { line ->
        val id = line.split(",")[0].toIntOrNull() ?: 0
        if (id > maxId) maxId = id
    }
    return maxId + 1
}

// ===== 入力 =====
fun inputShift(fileName: String): Shift {
    print("名前: ")
    val name = readLine() ?: ""

    print("曜日: ")
    val day = readLine() ?: ""

    print("時間（例 10:00-15:00）: ")
    val time = readLine() ?: ""

    val parts = time.split("-")
    if (parts.size != 2) {
        println("形式エラー")
        return inputShift(fileName)
    }

    val id = getNextId(fileName)

    return Shift(id, name, day, parts[0], parts[1])
}

// ===== 編集 =====
fun editShift(fileName: String) {
    val shifts = loadShifts(fileName).toMutableList()

    if (shifts.isEmpty()) {
        println("データがありません")
        return
    }

    for ((i, s) in shifts.withIndex()) {
        println("$i: ${s.name} ${s.day} ${s.start}-${s.end}")
    }

    print("編集する番号: ")
    val index = readLine()?.toIntOrNull()

    if (index == null || index !in shifts.indices) {
        println("無効な番号です")
        return
    }

    val target = shifts[index]

    println("1: 名前  2: 曜日  3: 時間")
    print("選択: ")

    when (readLine()?.trim()) {
        "1" -> {
            print("新しい名前: ")
            val newName = readLine() ?: return
            shifts[index] = target.copy(name = newName)
        }

        "2" -> {
            print("新しい曜日: ")
            val newDay = readLine() ?: return
            shifts[index] = target.copy(day = newDay)
        }

        "3" -> {
            print("新しい時間（例 10:00-15:00）: ")
            val time = readLine() ?: return
            val parts = time.split("-")

            if (parts.size != 2) {
                println("形式エラー")
                return
            }

            shifts[index] = target.copy(
                start = parts[0],
                end = parts[1]
            )
        }

        else -> {
            println("無効な入力")
            return
        }
    }

    // 上書き保存（全部書き直し）
    File(fileName).writeText(
        shifts.joinToString("\n") {
            "${it.id},${it.name},${it.day},${it.start},${it.end}"
        } + "\n"
    )

    println("更新しました！")
}

// ===== メイン =====
fun main() {
    val fileName = "shifts.txt"

    while (true) {
        println("\n1:追加 2:終了 3:編集 4:一覧 5:削除")
        print("選択: ")

        when (readLine()?.trim()) {
            "1" -> {
                val shift = inputShift(fileName)
                saveShift(fileName, shift)
                println("追加しました！")
            }

            "2" -> {
                println("終了します")
                break
            }

            "3" -> {
                println("編集します")
                editShift(fileName)
            }

            "4" -> {
                showShifts(fileName)
            }

            "5" -> {
                deleteShift(fileName)
            }

            else -> println("無効な入力です")
        }
    }
}

// ===== 一覧 =====
fun showShifts(fileName: String) {
    val shifts = loadShifts(fileName)

    if (shifts.isEmpty()) {
        println("データなし")
        return
    }

    println("===== シフト一覧 =====")

    shifts.forEach {
        println("${it.id}: ${it.name} ${it.day} ${it.start}-${it.end}")
    }
}

// ===== 削除 =====
fun deleteShift(fileName: String) {
    val shifts = loadShifts(fileName).toMutableList()

    if (shifts.isEmpty()) {
        println("データがありません")
        return
    }

    println("===== シフト一覧 =====")

    shifts.forEachIndexed { index, shift ->
        println(
            "$index: ${shift.name} ${shift.day} ${shift.start}-${shift.end}"
        )
    }

    print("削除する番号: ")

    val index = readLine()?.toIntOrNull()

    if (index == null || index !in shifts.indices) {
        println("無効な番号です")
        return
    }

    val removed = shifts.removeAt(index)

    // 全データを書き直し
    File(fileName).writeText(
        shifts.joinToString("\n") {
            "${it.id},${it.name},${it.day},${it.start},${it.end}"
        } + "\n"
    )

    println("${removed.name} を削除しました！")
}
