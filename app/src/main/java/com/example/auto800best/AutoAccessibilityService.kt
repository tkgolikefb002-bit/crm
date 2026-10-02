package com.example.auto800best

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log
import android.os.Handler
import android.os.Looper

class AutoAccessibilityService : AccessibilityService() {

    companion object {
        var instance: AutoAccessibilityService? = null
        var isAutoLoginRunning = false
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("AutoService", "Dịch vụ trợ năng đã được bật")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (!isAutoLoginRunning) return

        // Khi cửa sổ thay đổi nội dung (web load xong), tiến hành chạy quy trình tự động
        if (event?.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event?.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            
            // Chạy ngầm một nhịp ngắn để tránh chồng chéo luồng
            Handler(Looper.getMainLooper()).postDelayed({
                if (isAutoLoginRunning) {
                    executeAutoLoginStep()
                }
            }, 1000)
        }
    }

    override fun onInterrupt() {
        instance = null
        isAutoLoginRunning = false
    }

    private fun executeAutoLoginStep() {
        val rootNode = rootInActiveWindow ?: return

        // Bước 1: Tìm và đổi ngôn ngữ (English / 中文 -> Tiếng Việt)
        if (clickNodeByText(rootNode, arrayOf("English", "中文", "简体中文", "Language"))) {
            Log.d("AutoService", "Đã tìm thấy nút đổi ngôn ngữ, đang chọn Tiếng Việt...")
            Handler(Looper.getMainLooper()).postDelayed({
                val currentRoot = rootInActiveWindow
                if (currentRoot != null) {
                    clickNodeByText(currentRoot, arrayOf("Tiếng Việt", "Vietnamese", "越南语"))
                }
            }, 800)
            return
        }

        // Đọc thông tin tài khoản đã lưu
        val sharedPrefs = getSharedPreferences("Auto800BestPrefs", Context.MODE_PRIVATE)
        val station = sharedPrefs.getString("station", "") ?: ""
        val username = sharedPrefs.getString("username", "") ?: ""
        val password = sharedPrefs.getString("password", "") ?: ""

        // Bước 2: Điền Mã bưu cục
        if (typeTextByLabel(rootNode, arrayOf("Mã bưu cục", "Station", "Branch", "网点", "Code"), station)) {
            Log.d("AutoService", "Đã điền Mã bưu cục")
            return
        }

        // Bước 3: Điền Tên đăng nhập
        if (typeTextByLabel(rootNode, arrayOf("Tên người dùng", "Tên đăng nhập", "Tài khoản", "Username", "User", "用户名"), username)) {
            Log.d("AutoService", "Đã điền Tên đăng nhập")
            return
        }

        // Bước 4: Điền Mật khẩu
        if (typeTextByLabel(rootNode, arrayOf("Mật khẩu", "Password", "密码"), password)) {
            Log.d("AutoService", "Đã điền Mật khẩu")
            return
        }

        // Bước 5: Bấm nút Đăng nhập
        if (clickNodeByText(rootNode, arrayOf("Đăng nhập", "Login", "Sign in", "登录"))) {
            Log.d("AutoService", "Đã bấm Đăng nhập thành công!")
            isAutoLoginRunning = false // Hoàn tất quy trình đăng nhập
        }
    }

    private fun clickNodeByText(node: AccessibilityNodeInfo, keywords: Array<String>): Boolean {
        val text = node.text?.toString() ?: ""
        val desc = node.contentDescription?.toString() ?: ""

        for (kw in keywords) {
            if (text.contains(kw, ignoreCase = true) || desc.contains(kw, ignoreCase = true)) {
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                } else {
                    var parent = node.parent
                    while (parent != null) {
                        if (parent.isClickable) {
                            parent.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                            return true
                        }
                        parent = parent.parent
                    }
                }
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (clickNodeByText(child, keywords)) {
                return true
            }
        }
        return false
    }

    private fun typeTextByLabel(node: AccessibilityNodeInfo, labelKeywords: Array<String>, textToType: String): Boolean {
        val text = node.text?.toString() ?: ""
        for (kw in labelKeywords) {
            if (text.contains(kw, ignoreCase = true)) {
                val targetNode = findEditTextNearby(node)
                if (targetNode != null && targetNode.text.isNullOrEmpty()) {
                    val arguments = Bundle().apply {
                        putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, textToType)
                    }
                    targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments)
                    return true
                }
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (typeTextByLabel(child, labelKeywords, textToType)) {
                return true
            }
        }
        return false
    }

    private fun findEditTextNearby(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.className?.toString()?.contains("EditText") == true) {
            return node
        }
        val parent = node.parent ?: return null
        for (i in 0 until parent.childCount) {
            val child = parent.getChild(i) ?: continue
            if (child.className?.toString()?.contains("EditText") == true) {
                return child
            }
        }
        return null
    }
}
