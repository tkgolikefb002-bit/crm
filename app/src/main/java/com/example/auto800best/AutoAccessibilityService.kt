package com.example.auto800best

accessibility.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.view.accessibility.AccessibilityNodeInfo
import android.os.Bundle

import android.util.Log

class AutoAccessibilityService : AccessibilityService() {

    companion object {
        var instance: AutoAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        Log.d("AutoService", "Dịch vụ trợ năng đã được bật")
    }

    override fun onAccessibilityEvent(event: android.view.accessibility.AccessibilityEvent?) {
        // Lắng nghe sự kiện thay đổi màn hình (nếu cần xử lý tự động theo thời gian thực)
    }

    override fun onInterrupt() {
        instance = null
    }

    // Hàm quét màn hình tìm node chứa từ khóa và click vào nó
    fun clickByText(vararg keywords: String): Boolean {
        val rootNode: AccessibilityNodeInfo = rootInActiveWindow ?: return false
        return findAndClickRecursive(rootNode, keywords)
    }

    private fun findAndClickRecursive(node: AccessibilityNodeInfo, keywords: Array<out String>): Boolean {
        val text = node.text?.toString() ?: ""
        val desc = node.contentDescription?.toString() ?: ""

        for (kw in keywords) {
            if (text.contains(kw, ignoreCase = true) || desc.contains(kw, ignoreCase = true)) {
                // Nếu tìm thấy chữ khớp, thực hiện click vào node đó
                if (node.isClickable) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                    return true
                } else {
                    // Nếu bản thân node không click được, tìm node cha gần nhất để click
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

        // Đệ quy quét các node con bên trong
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            if (findAndClickRecursive(child, keywords)) {
                return true
            }
        }
        return false
    }

    // Hàm tìm ô nhập liệu (EditText) dựa theo nhãn và điền text vào
    fun typeTextByLabel(labelKeywords: Array<String>, textToType: String): Boolean {
        val rootNode: AccessibilityNodeInfo = rootInActiveWindow ?: return false
        return findAndTypeRecursive(rootNode, labelKeywords, textToType)
    }

    private fun findAndTypeRecursive(node: AccessibilityNodeInfo, labelKeywords: Array<String>, textToType: String): Boolean {
        val text = node.text?.toString() ?: ""
        for (kw in labelKeywords) {
            if (text.contains(kw, ignoreCase = true)) {
                // Tìm thấy nhãn, thử tìm ô EditText ở gần hoặc ngay cạnh node này để điền text
                val targetNode = findEditTextNearby(node)
                if (targetNode != null) {
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
            if (findAndTypeRecursive(child, labelKeywords, textToType)) {
                return true
            }
        }
        return false
    }

    private fun findEditTextNearby(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        // Kiểm tra xem node hiện tại có phải là ô nhập liệu không
        if (node.className?.toString()?.contains("EditText") == true) {
            return node
        }
        // Tìm trong các node con của cha nó
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
