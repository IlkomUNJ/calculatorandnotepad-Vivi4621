package com.example.superapp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class NotepadActivity : AppCompatActivity() {

    private lateinit var etEditor: EditText
    private lateinit var styleOptions: LinearLayout
    private lateinit var tvTextSize: TextView
    private lateinit var btnIncreaseSize: Button
    private lateinit var btnDecreaseSize: Button
    private lateinit var btnBold: Button
    private lateinit var btnItalic: Button
    private lateinit var btnNormal: Button
    private lateinit var btnStyle: Button
    private lateinit var clipboardManager: ClipboardManager

    private var currentTextSize = 16f
    private var isBold = false
    private var isItalic = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notepad)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        initializeNotepad()
    }

    private fun initializeNotepad() {
        etEditor = findViewById(R.id.etEditor)
        styleOptions = findViewById(R.id.styleOptions)
        tvTextSize = findViewById(R.id.tvTextSize)
        btnIncreaseSize = findViewById(R.id.btnIncreaseSize)
        btnDecreaseSize = findViewById(R.id.btnDecreaseSize)
        btnBold = findViewById(R.id.btnBold)
        btnItalic = findViewById(R.id.btnItalic)
        btnNormal = findViewById(R.id.btnNormal)
        btnStyle = findViewById(R.id.btnStyle)

        setupClickListeners()
        setupClipboardManager()
        setupTextWatcher()
    }

    private fun setupClickListeners() {
        btnIncreaseSize.setOnClickListener { increaseTextSize() }
        btnDecreaseSize.setOnClickListener { decreaseTextSize() }
        btnBold.setOnClickListener { setBoldStyle() }
        btnItalic.setOnClickListener { setItalicStyle() }
        btnNormal.setOnClickListener { setNormalStyle() }
        btnStyle.setOnClickListener { toggleStyleOptions() }

        etEditor.setOnLongClickListener {
            showContextMenu()
            true
        }
    }

    private fun setupClipboardManager() {
        clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }

    private fun setupTextWatcher() {
        etEditor.addTextChangedListener {
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.notepad_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_new -> { createNewNote(); true }
            R.id.menu_save -> { saveNote(); true }
            R.id.menu_cut -> { cutText(); true }
            R.id.menu_copy -> { copyText(); true }
            R.id.menu_paste -> { pasteText(); true }
            R.id.menu_switch_calculator -> {
                switchToCalculator()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun switchToCalculator() {
        val intent = Intent(this, CalculatorActivity::class.java)
        startActivity(intent)
        finish()
    }

    // Fungsi-fungsi notepad lainnya (toggleStyleOptions, increaseTextSize, dll)
    // Copy dari kode notepad sebelumnya
    private fun toggleStyleOptions() {
        styleOptions.visibility = if (styleOptions.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

    private fun increaseTextSize() {
        if (currentTextSize < 72f) {
            currentTextSize += 2f
            updateTextSize()
        }
    }

    private fun decreaseTextSize() {
        if (currentTextSize > 8f) {
            currentTextSize -= 2f
            updateTextSize()
        }
    }

    private fun updateTextSize() {
        etEditor.textSize = currentTextSize
        tvTextSize.text = "${currentTextSize.toInt()}sp"
    }

    private fun setBoldStyle() {
        isBold = !isBold
        updateTextStyle()
        updateStyleButtons()
    }

    private fun setItalicStyle() {
        isItalic = !isItalic
        updateTextStyle()
        updateStyleButtons()
    }

    private fun setNormalStyle() {
        isBold = false
        isItalic = false
        updateTextStyle()
        updateStyleButtons()
    }

    private fun updateTextStyle() {
        var style = android.graphics.Typeface.NORMAL
        if (isBold && isItalic) {
            style = android.graphics.Typeface.BOLD_ITALIC
        } else if (isBold) {
            style = android.graphics.Typeface.BOLD
        } else if (isItalic) {
            style = android.graphics.Typeface.ITALIC
        }
        etEditor.typeface = android.graphics.Typeface.create(etEditor.typeface, style)
    }

    private fun updateStyleButtons() {
        btnBold.setBackgroundColor(
            if (isBold) resources.getColor(android.R.color.holo_blue_light, theme)
            else resources.getColor(android.R.color.transparent, theme)
        )
        btnItalic.setBackgroundColor(
            if (isItalic) resources.getColor(android.R.color.holo_blue_light, theme)
            else resources.getColor(android.R.color.transparent, theme)
        )
        btnNormal.setBackgroundColor(
            if (!isBold && !isItalic) resources.getColor(android.R.color.holo_blue_light, theme)
            else resources.getColor(android.R.color.transparent, theme)
        )
    }

    private fun createNewNote() {
        if (etEditor.text.isNotEmpty()) {
            android.app.AlertDialog.Builder(this)
                .setTitle("New Note")
                .setMessage("Are you sure you want to create a new note? Current content will be lost.")
                .setPositiveButton("Yes") { _, _ ->
                    etEditor.setText("")
                    Toast.makeText(this, "New note created", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No", null)
                .show()
        } else {
            etEditor.setText("")
            Toast.makeText(this, "New note created", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveNote() {
        val content = etEditor.text.toString()
        if (content.isNotEmpty()) {
            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Cannot save empty note", Toast.LENGTH_SHORT).show()
        }
    }

    private fun cutText() {
        val selectedText = etEditor.text.substring(etEditor.selectionStart, etEditor.selectionEnd)
        if (selectedText.isNotEmpty()) {
            copyToClipboard(selectedText)
            val newText = etEditor.text.replaceRange(etEditor.selectionStart, etEditor.selectionEnd, "")
            etEditor.setText(newText)
            Toast.makeText(this, "Text cut to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No text selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyText() {
        val selectedText = etEditor.text.substring(etEditor.selectionStart, etEditor.selectionEnd)
        if (selectedText.isNotEmpty()) {
            copyToClipboard(selectedText)
            Toast.makeText(this, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No text selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun pasteText() {
        val clipData = clipboardManager.primaryClip
        if (clipData != null && clipData.itemCount > 0) {
            val pasteData = clipData.getItemAt(0).text.toString()
            val currentText = etEditor.text.toString()
            val selectionStart = etEditor.selectionStart
            val selectionEnd = etEditor.selectionEnd

            val newText = StringBuilder(currentText)
                .replace(selectionStart, selectionEnd, pasteData)
                .toString()

            etEditor.setText(newText)
            etEditor.setSelection(selectionStart + pasteData.length)
            Toast.makeText(this, "Text pasted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Clipboard is empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clip = ClipData.newPlainText("notepad_text", text)
        clipboardManager.setPrimaryClip(clip)
    }

    private fun showContextMenu() {
        etEditor.showContextMenu()
    }

    override fun onCreateContextMenu(menu: android.view.ContextMenu, v: View,
                                     menuInfo: android.view.ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        if (v.id == R.id.etEditor) {
            menu.add(0, 1, 0, "Cut")
            menu.add(0, 2, 0, "Copy")
            menu.add(0, 3, 0, "Paste")
            menu.add(0, 4, 0, "Select All")
        }
    }

    override fun onContextItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            1 -> { cutText(); true }
            2 -> { copyText(); true }
            3 -> { pasteText(); true }
            4 -> { etEditor.selectAll(); true }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun onBackPressed() {
        if (styleOptions.visibility == View.VISIBLE) {
            styleOptions.visibility = View.GONE
        } else {
            super.onBackPressed()
        }
    }
}