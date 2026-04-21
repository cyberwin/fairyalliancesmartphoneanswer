package com.fairyalliance.smartanswer;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import java.util.List;

public class AutoCallService extends AccessibilityService {

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 监听来电界面，自动点击接听按钮
        try {
            String pkg = event.getPackageName().toString();
            if (pkg.contains("com.android.phone") || pkg.contains("dialer")) {
                findAndClickAnswerButton(event.getSource());
            }
        } catch (Exception e) {
            // 不闪退
        }
    }

    // 自动查找接听按钮并点击
    private void findAndClickAnswerButton(AccessibilityNodeInfo node) {
        if (node == null) return;

        List<AccessibilityNodeInfo> list = node.findAccessibilityNodeInfosByText("接听");
        if (list == null || list.isEmpty()) {
            list = node.findAccessibilityNodeInfosByText("Answer");
        }

        if (list != null && !list.isEmpty()) {
            AccessibilityNodeInfo btn = list.get(0);
            if (btn.isEnabled()) {
                btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }

        // 递归查找子控件
        for (int i = 0; i < node.getChildCount(); i++) {
            findAndClickAnswerButton(node.getChild(i));
        }
    }

    @Override
    public void onInterrupt() {}
}
