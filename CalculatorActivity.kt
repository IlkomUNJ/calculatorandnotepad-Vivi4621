package com.example.superapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.*

class CalculatorActivity : AppCompatActivity() {

    private lateinit var etDisplay: EditText
    private lateinit var tvExpression: TextView
    private var currentInput = "0"
    private var lastInputIsOperator = false
    private var radianMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        // Setup Toolbar
        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        initializeCalculator()
    }

    private fun initializeCalculator() {
        etDisplay = findViewById(R.id.etDisplay)
        tvExpression = findViewById(R.id.tvExpression)
        etDisplay.showSoftInputOnFocus = false

        setupNumberButtons()
        setupOperatorButtons()
        setupScientificButtons()
    }

    private fun setupNumberButtons() {
        val numberButtonIds = listOf(
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        )

        numberButtonIds.forEach { id ->
            findViewById<Button>(id).setOnClickListener {
                appendToInput((it as Button).text.toString())
            }
        }

        findViewById<Button>(R.id.btnDecimal).setOnClickListener {
            if (!currentInput.contains(".") || lastInputIsOperator) {
                appendToInput(".")
            }
        }
    }

    private fun setupOperatorButtons() {
        findViewById<Button>(R.id.btnAdd).setOnClickListener { appendOperator("+") }
        findViewById<Button>(R.id.btnSubtract).setOnClickListener { appendOperator("-") }
        findViewById<Button>(R.id.btnMultiply).setOnClickListener { appendOperator("×") }
        findViewById<Button>(R.id.btnDivide).setOnClickListener { appendOperator("÷") }
        findViewById<Button>(R.id.btnClear).setOnClickListener { clearAll() }
        findViewById<Button>(R.id.btnBackspace).setOnClickListener { backspace() }
        findViewById<Button>(R.id.btnEquals).setOnClickListener { calculateResult() }
        findViewById<Button>(R.id.btnPlusMinus).setOnClickListener { toggleSign() }
        findViewById<Button>(R.id.btnPercent).setOnClickListener { calculatePercent() }
        findViewById<Button>(R.id.btnOpenBracket).setOnClickListener { appendToInput("(") }
        findViewById<Button>(R.id.btnCloseBracket).setOnClickListener { appendToInput(")") }
    }

    private fun setupScientificButtons() {
        findViewById<Button>(R.id.btnSin).setOnClickListener { appendFunction("sin(") }
        findViewById<Button>(R.id.btnCos).setOnClickListener { appendFunction("cos(") }
        findViewById<Button>(R.id.btnTan).setOnClickListener { appendFunction("tan(") }
        findViewById<Button>(R.id.btnLog).setOnClickListener { appendFunction("log(") }
        findViewById<Button>(R.id.btnLn).setOnClickListener { appendFunction("ln(") }
        findViewById<Button>(R.id.btnSqrt).setOnClickListener { appendFunction("√(") }
        findViewById<Button>(R.id.btnPower).setOnClickListener { appendOperator("^") }
        findViewById<Button>(R.id.btnFactorial).setOnClickListener { appendFunction("!") }
        findViewById<Button>(R.id.btnPi).setOnClickListener { appendConstant("π", Math.PI.toString()) }
        findViewById<Button>(R.id.btnE).setOnClickListener { appendConstant("e", Math.E.toString()) }
    }

    private fun appendToInput(value: String) {
        currentInput = if (currentInput == "0" && value != ".") {
            value
        } else {
            currentInput + value
        }
        updateDisplay()
        lastInputIsOperator = false
    }

    private fun appendOperator(operator: String) {
        if (currentInput.isNotEmpty() && !lastInputIsOperator) {
            val displayOperator = when (operator) {
                "×" -> "*"
                "÷" -> "/"
                else -> operator
            }
            currentInput += displayOperator
            updateDisplay()
            lastInputIsOperator = true
        }
    }

    private fun appendFunction(function: String) {
        currentInput += function
        updateDisplay()
        lastInputIsOperator = false
    }

    private fun appendConstant(display: String, value: String) {
        currentInput += value
        updateDisplay()
        lastInputIsOperator = false
    }

    private fun updateDisplay() {
        etDisplay.setText(currentInput)
        tvExpression.text = currentInput
    }

    private fun clearAll() {
        currentInput = "0"
        updateDisplay()
        lastInputIsOperator = false
    }

    private fun backspace() {
        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.substring(0, currentInput.length - 1)
            if (currentInput.isEmpty()) {
                currentInput = "0"
            }
            updateDisplay()
        }
    }

    private fun toggleSign() {
        if (currentInput.isNotEmpty() && currentInput != "0") {
            try {
                val value = currentInput.toDouble()
                currentInput = (-value).toString()
                updateDisplay()
            } catch (e: NumberFormatException) {

            }
        }
    }

    private fun calculatePercent() {
        try {
            val value = currentInput.toDouble()
            val result = value / 100
            currentInput = result.toString()
            updateDisplay()
        } catch (e: NumberFormatException) {

        }
    }

    private fun calculateResult() {
        try {
            var expression = currentInput

            expression = expression.replace("×", "*")
            expression = expression.replace("÷", "/")
            expression = expression.replace("sin(", "sin(")
            expression = expression.replace("cos(", "cos(")
            expression = expression.replace("tan(", "tan(")
            expression = expression.replace("log(", "log10(")
            expression = expression.replace("ln(", "ln(")
            expression = expression.replace("√(", "sqrt(")
            expression = expression.replace("π", Math.PI.toString())
            expression = expression.replace("e", Math.E.toString())

            if (expression.contains("!")) {
                expression = handleFactorial(expression)
            }

            expression = expression.replace("^", ".pow(") + ")"

            if (!radianMode) {
                expression = convertToRadians(expression)
            }

            val result = evaluateExpression(expression)
            currentInput = result.toString()
            updateDisplay()

        } catch (e: Exception) {
            currentInput = "Error"
            updateDisplay()
            e.printStackTrace()
        }
        lastInputIsOperator = false
    }

    private fun handleFactorial(expression: String): String {
        val parts = expression.split("!")
        if (parts.isNotEmpty()) {
            try {
                val n = parts[0].trim().toInt()
                var factorial: Long = 1
                for (i in 1..n) {
                    factorial *= i
                }
                return factorial.toString()
            } catch (e: NumberFormatException) {
                return expression
            }
        }
        return expression
    }

    private fun convertToRadians(expression: String): String {
        var result = expression
        result = result.replace("sin(", "sin(toRadians(")
        result = result.replace("cos(", "cos(toRadians(")
        result = result.replace("tan(", "tan(toRadians(")
        return result
    }

    private fun toRadians(degrees: Double): Double {
        return degrees * Math.PI / 180
    }

    private fun evaluateExpression(expr: String): Double {
        return try {
            when {
                expr.contains("+") -> {
                    val parts = expr.split("+")
                    evaluateExpression(parts[0]) + evaluateExpression(parts[1])
                }
                expr.contains("-") -> {
                    val parts = expr.split("-")
                    evaluateExpression(parts[0]) - evaluateExpression(parts[1])
                }
                expr.contains("*") -> {
                    val parts = expr.split("*")
                    evaluateExpression(parts[0]) * evaluateExpression(parts[1])
                }
                expr.contains("/") -> {
                    val parts = expr.split("/")
                    evaluateExpression(parts[0]) / evaluateExpression(parts[1])
                }
                expr.contains(".pow(") -> {
                    val base = expr.substringBefore(".pow(")
                    val exponent = expr.substringAfter(".pow(").substringBefore(")")
                    Math.pow(evaluateExpression(base), evaluateExpression(exponent))
                }
                expr.startsWith("sin(") -> {
                    val value = evaluateExpression(expr.substringAfter("sin(").substringBefore(")"))
                    sin(if (radianMode) value else toRadians(value))
                }
                expr.startsWith("cos(") -> {
                    val value = evaluateExpression(expr.substringAfter("cos(").substringBefore(")"))
                    cos(if (radianMode) value else toRadians(value))
                }
                expr.startsWith("tan(") -> {
                    val value = evaluateExpression(expr.substringAfter("tan(").substringBefore(")"))
                    tan(if (radianMode) value else toRadians(value))
                }
                expr.startsWith("log10(") -> {
                    val value = evaluateExpression(expr.substringAfter("log10(").substringBefore(")"))
                    log10(value)
                }
                expr.startsWith("ln(") -> {
                    val value = evaluateExpression(expr.substringAfter("ln(").substringBefore(")"))
                    ln(value)
                }
                expr.startsWith("sqrt(") -> {
                    val value = evaluateExpression(expr.substringAfter("sqrt(").substringBefore(")"))
                    sqrt(value)
                }
                else -> expr.toDoubleOrNull() ?: 0.0
            }
        } catch (e: Exception) {
            0.0
        }
    }

    private fun sin(x: Double): Double = Math.sin(x)
    private fun cos(x: Double): Double = Math.cos(x)
    private fun tan(x: Double): Double = Math.tan(x)
    private fun log10(x: Double): Double = Math.log10(x)
    private fun ln(x: Double): Double = Math.log(x)
    private fun sqrt(x: Double): Double = Math.sqrt(x)
}