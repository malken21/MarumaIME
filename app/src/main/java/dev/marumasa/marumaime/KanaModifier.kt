package dev.marumasa.marumaime

object KanaModifier {
    fun modify(text: String, modifier: String): String {
        if (text.isEmpty()) return text
        val lastChar = text.last()
        val baseText = text.dropLast(1)
        
        return when (modifier) {
            "゛" -> baseText + addDakuten(lastChar)
            "゜" -> baseText + addHandakuten(lastChar)
            "小" -> baseText + toggleSmall(lastChar)
            else -> text + modifier
        }
    }

    private fun addDakuten(c: Char): Char {
        return when (c) {
            'か' -> 'が'; 'き' -> 'ぎ'; 'く' -> 'ぐ'; 'け' -> 'げ'; 'こ' -> 'ご'
            'さ' -> 'ざ'; 'し' -> 'じ'; 'す' -> 'ず'; 'せ' -> 'ぜ'; 'そ' -> 'ぞ'
            'た' -> 'だ'; 'ち' -> 'ぢ'; 'つ' -> 'づ'; 'て' -> 'で'; 'と' -> 'ど'
            'は' -> 'ば'; 'ひ' -> 'び'; 'ふ' -> 'ぶ'; 'へ' -> 'べ'; 'ほ' -> 'ぼ'
            'が' -> 'か'; 'ぎ' -> 'き'; 'ぐ' -> 'く'; 'げ' -> 'け'; 'ご' -> 'こ'
            'ざ' -> 'さ'; 'じ' -> 'し'; 'ず' -> 'す'; 'ぜ' -> 'せ'; 'ぞ' -> 'そ'
            'だ' -> 'た'; 'ぢ' -> 'ち'; 'づ' -> 'つ'; 'で' -> 'て'; 'ど' -> 'と'
            'ば' -> 'は'; 'び' -> 'ひ'; 'ぶ' -> 'ふ'; 'べ' -> 'へ'; 'ぼ' -> 'ほ'
            'ウ' -> 'ヴ'; 'ヴ' -> 'ウ'
            else -> c
        }
    }

    private fun addHandakuten(c: Char): Char {
        return when (c) {
            'は' -> 'ぱ'; 'ひ' -> 'ぴ'; 'ふ' -> 'ぷ'; 'へ' -> 'ぺ'; 'ほ' -> 'ぽ'
            'ば' -> 'ぱ'; 'び' -> 'ぴ'; 'ぶ' -> 'ぷ'; 'べ' -> 'ぺ'; 'ぼ' -> 'ぽ'
            'ぱ' -> 'は'; 'ぴ' -> 'ひ'; 'ぷ' -> 'ふ'; 'ぺ' -> 'へ'; 'ぽ' -> 'ほ'
            else -> c
        }
    }

    private fun toggleSmall(c: Char): Char {
        return when (c) {
            'あ' -> 'ぁ'; 'い' -> 'ぃ'; 'う' -> 'ぅ'; 'え' -> 'ぇ'; 'お' -> 'ぉ'
            'つ' -> 'っ'; 'や' -> 'ゃ'; 'ゆ' -> 'ゅ'; 'よ' -> 'ょ'; 'わ' -> 'ゎ'
            'ぁ' -> 'あ'; 'ぃ' -> 'い'; 'ぅ' -> 'う'; 'ぇ' -> 'え'; 'ぉ' -> 'お'
            'っ' -> 'つ'; 'ゃ' -> 'や'; 'ゅ' -> 'ゆ'; 'ょ' -> 'よ'; 'ゎ' -> 'わ'
            'カ' -> 'ヵ'; 'ケ' -> 'ヶ'; 'ヵ' -> 'カ'; 'ヶ' -> 'ケ'
            else -> c
        }
    }
}
