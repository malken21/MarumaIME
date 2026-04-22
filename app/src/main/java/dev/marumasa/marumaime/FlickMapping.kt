package dev.marumasa.marumaime

object FlickMapping {
    val mapping = mapOf(
        // Japanese
        "あ" to listOf("あ", "い", "う", "え", "お"),
        "か" to listOf("か", "き", "く", "け", "こ"),
        "さ" to listOf("さ", "し", "す", "せ", "そ"),
        "た" to listOf("た", "ち", "つ", "て", "と"),
        "な" to listOf("な", "に", "ぬ", "ね", "の"),
        "は" to listOf("は", "ひ", "ふ", "へ", "ほ"),
        "ま" to listOf("ま", "み", "む", "め", "も"),
        "や" to listOf("や", "（", "ゆ", "）", "よ"),
        "ら" to listOf("ら", "り", "る", "れ", "ろ"),
        "わ" to listOf("わ", "を", "ん", "ー", "わ"),
        "゛゜" to listOf("゛", "゜", "゛", "゜", "゛"),
        "小" to listOf("小", "小", "小", "小", "小"),
        
        // Symbols (Set 1)
        ".," to listOf(".", ",", "?", "!", ":"),
        "@#" to listOf("@", "#", "&", "*", "+"),
        "()" to listOf("(", ")", "[", "]", "<"),
        "-_" to listOf("-", "_", "=", "/", "\\"),
        "\"'" to listOf("\"", "'", "`", "~", "^"),
        "¥$" to listOf("¥", "$", "€", "£", "¢"),
        "%|" to listOf("%", "|", "{", "}", ">"),
        "…" to listOf("…", "・", "“", "”", "„"),
        "±" to listOf("±", "×", "÷", "≠", "≈"),

        // Symbols (Set 2 - Shifted)
        "S1" to listOf("1", "①", "⑴", "⒈", "❶"),
        "S2" to listOf("2", "②", "⑵", "⒉", "❷"),
        "S3" to listOf("3", "③", "⑶", "⒊", "❸"),
        "S4" to listOf("4", "④", "⑷", "⒋", "❹"),
        "S5" to listOf("5", "⑤", "⑸", "⒌", "❺"),
        "S6" to listOf("6", "⑥", "⑹", "⒍", "❻"),
        "S7" to listOf("7", "⑦", "⑺", "⒎", "❼"),
        "S8" to listOf("8", "⑧", "⑻", "⒏", "❽"),
        "S9" to listOf("9", "⑨", "⑼", "⒐", "❾"),
        "S0" to listOf("0", "⓪", "⓿", "⁰", "₀")
    )

    fun getFlickChar(baseKey: String, direction: FlickDirection, isShifted: Boolean = false): String? {
        val key = if (isShifted && !mapping.containsKey(baseKey)) {
            // Logic for shifted keys if needed
            baseKey
        } else {
            baseKey
        }
        
        val list = mapping[key] ?: return baseKey.take(1)
        return when (direction) {
            FlickDirection.Center -> list.getOrElse(0) { baseKey }
            FlickDirection.Left -> list.getOrElse(1) { "" }
            FlickDirection.Up -> list.getOrElse(2) { "" }
            FlickDirection.Right -> list.getOrElse(3) { "" }
            FlickDirection.Down -> list.getOrElse(4) { "" }
        }
    }
}
