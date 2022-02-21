package calculator

import java.math.BigInteger
import java.util.*
import javax.naming.directory.InvalidAttributesException
import kotlin.system.exitProcess


class SmartCalculator {
    private val variables = mutableMapOf<String, BigInteger>()

    fun startCalculate() {
        while (true) {
            val line = readLine()!!

            try {
                when {
                    line.contains("=") -> {
                        val variableArr = line.trim().split("(\\s+)?=(\\s+)?".toRegex())
                        if (variableArr.size != 2) {
                            throw InvalidAssignmentException()
                        }
                        saveVariable(variableArr[0], variableArr[1])
                    }
                    line.startsWith("/") -> {
                        helpFun(line)
                    }
                    line.isNotBlank() -> {
                        val fixedOperatorsExp = fixExpOperators(line.trim())
                        formatExpWithVariables(fixedOperatorsExp)
                        checkExpression(fixedOperatorsExp)

                        val postfixLine = InfixToPostfix(fixedOperatorsExp).doTrans()
                        val result = evaluatePostfix(postfixLine)

                        println(result)
                    }
                }
            } catch (e: NumberFormatException) {
                println("Invalid assignment")
            } catch (e: InvalidAssignmentException) {
                println("Invalid assignment")
            } catch (e: InvalidExpressionException) {
                println("Invalid expression")
            } catch (e: InvalidIdentifierException) {
                println("Invalid identifier")
            } catch (e: UnknownVariableException) {
                println("Unknown variable")
            }
        }
    }

    private fun evaluatePostfix(postfix: String): BigInteger {
        val stack: Stack<String> = Stack()
        val postfixSplit = postfix.split(" ")

        for (it in postfixSplit) {
            if (Common.isNumber(it)) {
                stack.push(it)
                continue
            }

            if (Common.isVariable(it, variables)) {
                stack.push(variables[it].toString())
                continue
            }

            if (Common.isOperator(it)) {
                if (stack.size == 1) {
                    if (it == "-") {
                        return -stack.pop().toBigInteger()
                    }
                    return stack.pop().toBigInteger()
                }

                val secondNumber = getIntValue(stack.pop())
                var firstNumber = getIntValue(stack.pop())

                when(it) {
                    "+" -> firstNumber += secondNumber
                    "-" -> firstNumber -= secondNumber
                    "*" -> firstNumber *= secondNumber
                    "/" -> firstNumber /= secondNumber
                    "^" -> firstNumber = pow(firstNumber, secondNumber)
                }
                stack.push(firstNumber.toString())
            }
        }
        return stack.pop().toBigInteger()
    }

    private fun pow(baseInit: BigInteger, exponentInit: BigInteger): BigInteger {
        var base = baseInit
        var exponent = exponentInit
        var result = BigInteger.ONE
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) result = result.multiply(base)
            base = base.multiply(base)
            exponent = exponent.shiftRight(1)
        }
        return result
    }

    private fun getIntValue(input: String): BigInteger {
        val result: BigInteger = if (Common.isNumber(input)) {
            input.toBigInteger()
        } else {
            variables[input]!!
        }
        return result
    }

    private fun fixExpOperators(exp: String): String {
        var result = exp
        var currentOperator: Char? = null

        for(i in result.indices) {
            val isOperator = result[i] == '+' || result[i] == '-' || result[i] == '/' || result[i] == '*'
            if (isOperator && currentOperator != result[i]) {
                currentOperator = result[i]

                if (result[i + 1] == currentOperator) {
                    for (j in i + 1 until result.length) {
                        if (currentOperator == '*' || currentOperator == '/') {
                            throw InvalidExpressionException()
                        }

                        if (currentOperator != result[j]) {
                            if (currentOperator == '-' && (j - 1 - i) % 2 != 0) {
                                currentOperator = '+'
                                result = result.replaceRange(i, j, currentOperator.toString())
                            } else {
                                result = result.replaceRange(i, j, currentOperator.toString())
                            }
                            return fixExpOperators(result)
                        }
                    }
                }
            }
        }
        return result
    }

    private fun saveVariable(name: String, value: String) {
        val regex = Regex("[a-zA-Z]+")
        if (!regex.matches(name))
            throw InvalidIdentifierException()

        if (Common.isNumber(value)) {
            val intValue = value.toBigInteger()
            variables[name] = intValue
        } else {
            if (value.contains("^(?:[0-9]+[a-z]|[a-z]+[0-9])[a-z0-9]*".toRegex()))
                throw InvalidAssignmentException()

            val newValue = variables[value]
            if (newValue != null) {
                variables[name] = newValue
            } else {
                throw UnknownVariableException()
            }
        }
    }

    /**
     * Help function
     * @param command commands
     */
    private fun helpFun(command: String) {
        return when(command) {
            "/exit" -> {
                println("Bye!")
                exitProcess(0)
            }
            "/help" -> {
                println("The program calculates of numbers")
                println("Consider that the even number of minuses gives a plus, and the odd number of minuses gives a minus! Look at it this way: 2 -- 2 equals 2 - (-2) equals 2 + 2.")
            }
            else -> {
                println("Unknown command")
            }
        }
    }

    private fun formatExpWithVariables(expression: String): String {
        var result = expression
        val splitExp = splitExp(expression)
        for (it in splitExp) {
            if (!Common.isOperator(it) && !Common.isNumber(it) && !Common.isBrackets(it)) {
                if (variables[it] == null) throw UnknownVariableException()
                result = result.replace(it, variables[it].toString(), false)
            }
        }
        return result
    }

    private fun checkExpression(expression: String) {
        val splitFunction = splitExp(expression)
        if (splitFunction.size > 1) {
            if (Common.isOperator(splitFunction[splitFunction.size - 1])) {
                throw InvalidAttributesException()
            }

            var countBrackets = 0
            for (some in splitFunction) {
                if (some == "(") ++countBrackets
                if (some == ")") --countBrackets
            }

            if (countBrackets != 0) {
                throw InvalidExpressionException()
            }
        }
    }

    private fun splitExp(exp: String): Array<String> {
        val funDelimiter = "(?<=[+/*^()])|(?=[+/*^()-])|(?<=-(?<!(?:[*/^(]|(?:sin|cos|tan)\\()-))"
        var splitFunction = exp.split(funDelimiter.toRegex()).toTypedArray()
        splitFunction = splitFunction.filter { it.isNotBlank() }.toTypedArray()
        for (i in splitFunction.indices) {
            splitFunction[i] = splitFunction[i].trim()
        }
        return splitFunction
    }
}

class InvalidExpressionException : RuntimeException()
class InvalidAssignmentException : RuntimeException()
class InvalidIdentifierException : RuntimeException()
class UnknownVariableException : RuntimeException()
