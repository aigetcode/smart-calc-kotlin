package calculator

import java.math.BigInteger

class Common {
    companion object {
        fun isVariable(s: String, variables: MutableMap<String, BigInteger>): Boolean {
            return try {
                s.toBigInteger()
                true
            } catch (ex: NumberFormatException) {
                variables[s] != null
            }
        }

        fun isNumber(s: String): Boolean {
            return try {
                s.toBigInteger()
                true
            } catch (ex: NumberFormatException) {
                false
            }
        }

        fun isOperator(s: String): Boolean {
            return when(s) {
                "+" -> true
                "-" -> true
                "*" -> true
                "/" -> true
                "^" -> true
                else -> false
            }
        }

        fun isBrackets(s: String): Boolean {
            return when(s) {
                "(" -> true
                ")" -> true
                else -> false
            }
        }

        fun isOperator(s: Char): Boolean {
            return when(s) {
                '+' -> true
                '-' -> true
                '*' -> true
                '/' -> true
                '^' -> true
                else -> false
            }
        }
    }
}
