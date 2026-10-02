package com.example.auto800best // Đổi thành package name của dự án bạn

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var edtStationCode: EditText
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnLogin: Button

    private val PREF_NAME = "Auto800BestPrefs"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ánh xạ thành phần giao diện
        edtStationCode = findViewById(R.id.edtStationCode)
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnSave = findViewById(R.id.btnSave)
        btnLogin = findViewById(R.id.btnLogin)

        // Tự động nạp thông tin đã lưu trước đó
        loadSavedCredentials()

        // Sự kiện nút Lưu tài khoản
        btnSave.setOnClickListener {
            saveCredentials()
        }

        // Sự kiện nút Đăng nhập
        btnLogin.setOnClickListener {
            val station = edtStationCode.text.toString().trim()
            val user = edtUsername.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            if (station.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(this, "Đang khởi chạy tiến trình tự động...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveCredentials() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("station", edtStationCode.text.toString().trim())
        editor.putString("username", edtUsername.text.toString().trim())
        editor.putString("password", edtPassword.text.toString().trim())
        editor.apply()

        Toast.makeText(this, "Đã lưu tài khoản thành công!", Toast.LENGTH_SHORT).show()
    }

    private fun loadSavedCredentials() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        edtStationCode.setText(sharedPreferences.getString("station", ""))
        edtUsername.setText(sharedPreferences.getString("username", ""))
        edtPassword.setText(sharedPreferences.getString("password", ""))
    }
}
