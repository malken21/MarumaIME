import app.cash.paparazzi.detectEnvironment
import app.cash.paparazzi.Environment

fun main() {
    val env = detectEnvironment()
    println(env.javaClass.declaredFields.joinToString { it.name })
}
