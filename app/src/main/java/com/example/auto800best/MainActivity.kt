package com.example.auto800best

import android.content.Context
import android.content.Intent
import android.net.Uri
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
    private val LOGIN_URL = "https://ucp-sso-sea.800best.com/uc-pub/login?service=https://vn-crm-mobile.800best.com/web/ssoCallback&lang=en-US#/login/index"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        edtStationCode = findViewById(R.id.edtStationCode)
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnSave = findViewById(R.id.btnSave)
        btnLogin = findViewById(R.id.btnLogin)

        loadSavedCredentials()

        btnSave.setOnClickListener {
            saveCredentials()
        }

        btnLogin.setOnClickListener {
            val station = edtStationCode.text.toString().trim()
            val user = edtUsername.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            if (station.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lưu lại trước khi chạy
            saveCredentials()

            Toast.makeText(this, "Đang mở trang đăng nhập 800best...", Toast.LENGTH_SHORT).show()

            // Mở link trực tiếp bằng trình duyệt trên điện thoại
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(LOGIN_URL))
            startActivity(intent)
        }
    }

    private fun saveCredentials() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("station", edtStationCode.text.toString().trim())
            putString("username", edtUsername.text.toString().trim())
            putString("password", edtPassword.text.toString().trim())
            apply()
        }
    }

    private fun loadSavedCredentials() {
        val sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        edtStationCode.setText(sharedPreferences.getString("station", ""))
        edtUsername.setText(sharedPreferences.getString("username", ""))
        edtPassword.setText(sharedPreferences.getString("password", ""))
    }
}
