package com.example.auto800best // Đổi đúng package name của dự án bạn

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

        // 1. Ánh xạ các thành phần giao diện từ activity_main.xml
        edtStationCode = findViewById(R.id.edtStationCode)
        edtUsername = findViewById(R.id.edtUsername)
        edtPassword = findViewById(R.id.edtPassword)
        btnSave = findViewById(R.id.btnSave)
        btnLogin = findViewById(R.id.btnLogin)

        // 2. Tự động nạp thông tin đã lưu trước đó (nếu có)
        loadSavedCredentials()

        // 3. Sự kiện bấm nút "Lưu tài khoản" thủ công
        btnSave.setOnClickListener {
            saveCredentials()
            Toast.makeText(this, "Đã lưu tài khoản thành công!", Toast.LENGTH_SHORT).show()
        }

        // 4. Sự kiện bấm nút "Bắt đầu tự động đăng nhập"
        btnLogin.setOnClickListener {
            val station = edtStationCode.text.toString().trim()
            val user = edtUsername.text.toString().trim()
            val pass = edtPassword.text.toString().trim()

            if (station.isEmpty() || user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Lưu lại thông tin trước khi chạy
            saveCredentials()

            // Kiểm tra xem người dùng đã bật quyền Trợ năng (Accessibility) chưa
            if (AutoAccessibilityService.instance == null) {
                Toast.makeText(
                    this, 
                    "Vui lòng bật quyền Trợ năng (Accessibility) cho ứng dụng trong Cài đặt trước!", 
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Kích hoạt cờ chạy vòng lặp tự động hóa trong Accessibility Service
            AutoAccessibilityService.isAutoLoginRunning = true

            Toast.makeText(this, "Đang mở trang đăng nhập & tự động hóa...", Toast.LENGTH_SHORT).show()

            // Mở trực tiếp link đăng nhập 800best bằng trình duyệt
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
