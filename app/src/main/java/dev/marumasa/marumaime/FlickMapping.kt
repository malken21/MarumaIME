package dev.marumasa.marumaime

object FlickMapping {
    val mapping = mapOf(
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
        "小" to listOf("小", "小", "小", "小", "小")
    )

    fun getFlickChar(baseKey: String, direction: FlickDirection): String? {
        val list = mapping[baseKey] ?: return null
        return when (direction) {
            FlickDirection.Center -> list[0]
            FlickDirection.Left -> list[1]
            FlickDirection.Up -> list[2]
            FlickDirection.Right -> list[3]
            FlickDirection.Down -> list[4]
        }
    }
}
