package calculator

class InfixToPostfix(inputLine: String) {
    private var theStack: java.util.Stack<String> = java.util.Stack()
    private var input: String? = null
    private var output = ""

    init {
        input = inputLine
    }

    fun doTrans(): String {
        val funDelimiter = "(?<=[+/*^()])|(?=[+/*^()-])|(?<=-(?<!(?:[*/^(]|(?:sin|cos|tan)\\()-))"
        var splitFunction = input?.split(funDelimiter.toRegex())?.toTypedArray()
        splitFunction?.map { it.trim() }
        splitFunction = splitFunction?.filter { it != "" }?.toTypedArray()

        for (j in 0 until splitFunction!!.size) {
            when (val ch = splitFunction[j]) {
                "+", "-" -> gotOper(ch, 1)
                "*", "/" -> gotOper(ch, 2)
                "^" -> gotOper(ch, 3)
                "(" -> theStack.push(ch)
                ")" -> gotParen()
                else -> output += " $ch"
            }
        }
        while (!theStack.isEmpty()) {
            output += " " + theStack.pop()
        }
        output = output.trim().replace("\\s+".toRegex(), " ")
        return output
    }

    fun gotOper(opThis: String, prec1: Int) {
        while (!theStack.isEmpty()) {
            val opTop = theStack.pop()
            if (opTop == "(") {
                theStack.push(opTop)
                break
            } else {
                val prec2: Int = if (opTop == "+" || opTop == "-") 1 else 2
                output = if (prec2 < prec1) {
                    theStack.push(opTop)
                    break
                } else "$output $opTop"
            }
        }
        theStack.push(opThis)
    }

    fun gotParen() {
        while (!theStack.isEmpty()) {
            val chx = theStack.pop()
            output = if (chx == "(") break else "$output $chx"
        }
    }
}
