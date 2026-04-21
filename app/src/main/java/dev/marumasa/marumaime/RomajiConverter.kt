package dev.marumasa.marumaime

object RomajiConverter {
    private val romajiMap = mapOf(
        "a" to "あ", "i" to "い", "u" to "う", "e" to "え", "o" to "お",
        "ka" to "か", "ki" to "き", "ku" to "く", "ke" to "け", "ko" to "こ",
        "sa" to "さ", "si" to "し", "su" to "す", "se" to "せ", "so" to "そ",
        "ta" to "た", "ti" to "ち", "tu" to "つ", "te" to "て", "to" to "と",
        "na" to "な", "ni" to "に", "nu" to "ぬ", "ne" to "ね", "no" to "の",
        "ha" to "は", "hi" to "ひ", "hu" to "ふ", "he" to "へ", "ho" to "ほ",
        "ma" to "ま", "mi" to "み", "mu" to "む", "me" to "め", "mo" to "も",
        "ya" to "や", "yu" to "ゆ", "yo" to "よ",
        "ra" to "ら", "ri" to "り", "ru" to "る", "re" to "れ", "ro" to "ろ",
        "wa" to "わ", "wo" to "を", "nn" to "ん",
        "ga" to "が", "gi" to "ぎ", "gu" to "ぐ", "ge" to "げ", "go" to "ご",
        "za" to "ざ", "zi" to "じ", "zu" to "ず", "ze" to "ぜ", "zo" to "ぞ",
        "da" to "だ", "di" to "ぢ", "du" to "づ", "de" to "で", "do" to "ど",
        "ba" to "ば", "bi" to "び", "bu" to "ぶ", "be" to "べ", "bo" to "ぼ",
        "pa" to "ぱ", "pi" to "ぴ", "pu" to "ぷ", "pe" to "ぺ", "po" to "ぽ",
        "sha" to "しゃ", "shu" to "しゅ", "sho" to "しょ",
        "cha" to "ちゃ", "chu" to "ちゅ", "cho" to "ちょ",
        "shi" to "し", "chi" to "ち", "tsu" to "つ", "fu" to "ふ"
    )

    fun convert(input: String): Pair<String, String> {
        var current = input
        var result = ""
        
        while (current.isNotEmpty()) {
            var found = false
            // Try matching longest prefix first
            for (len in 3 downTo 1) {
                if (current.length >= len) {
                    val prefix = current.substring(0, len)
                    val kana = romajiMap[prefix]
                    if (kana != null) {
                        result += kana
                        current = current.substring(len)
                        found = true
                        break
                    }
                }
            }
            if (!found) {
                // Special handling for 'n'
                if (current.startsWith("n") && current.length > 1 && !isVowel(current[1]) && current[1] != 'y') {
                    result += "ん"
                    current = current.substring(1)
                } else if (current.length >= 2 && current[0] == current[1] && isConsonant(current[0]) && current[0] != 'n') {
                    result += "っ"
                    current = current.substring(1)
                } else {
                    break // Cannot convert more
                }
            }
        }
        return Pair(result, current)
    }

    private fun isVowel(c: Char): Boolean = c in "aiueo"
    private fun isConsonant(c: Char): Boolean = c in "bcdfghjklmnpqrstvwxyz"
}
