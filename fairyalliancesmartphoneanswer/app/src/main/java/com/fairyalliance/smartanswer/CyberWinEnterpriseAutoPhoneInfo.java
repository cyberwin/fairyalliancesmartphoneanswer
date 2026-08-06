package com.fairyalliance.smartanswer;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.text.TextUtils;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import android.provider.ContactsContract.RawContactsColumns;

public class CyberWinEnterpriseAutoPhoneInfo {

    // 静态Gson实例
    private static final Gson gson = new Gson();

    /**
     * 获取全部联系人分组(tag)，遍历每个分组读取联系人；返回完整JSON字符串
     * JSON数组每个元素：{"tag":"xxx","name":"xxx","updateTs":1234567890,"phone":"138xxxx"}
     * @param ctx Application Context，禁止传Activity上下文防止内存泄漏
     * @return json字符串
     */
    public static String readAllContactGroupsToJson(Context ctx) {
        List<ContactTagItem> outList = new ArrayList<>();
        if (ctx == null) {
            return gson.toJson(outList);
        }

        Cursor groupCursor = ctx.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI,
                new String[]{ContactsContract.Groups._ID, ContactsContract.Groups.TITLE},
                null, null, null
        );
        if (groupCursor == null) {
            return gson.toJson(outList);
        }

        try {
            while (groupCursor.moveToNext()) {
                long groupId = groupCursor.getLong(0);
                String groupTitle = groupCursor.getString(1);
                if (TextUtils.isEmpty(groupTitle)) {
                    continue;
                }

                // 查询该分组下面所有RawContactId
                List<Long> rawIdList = new ArrayList<>();
                Cursor memberCur = ctx.getContentResolver().query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.Data.RAW_CONTACT_ID},
                        ContactsContract.Data.MIMETYPE + "=? AND " + ContactsContract.Data.DATA1 + "=?",
                        new String[]{
                                ContactsContract.CommonDataKinds.GroupMembership.CONTENT_ITEM_TYPE,
                                String.valueOf(groupId)
                        },
                        null
                );
                if (memberCur != null) {
                    try {
                        while (memberCur.moveToNext()) {
                            rawIdList.add(memberCur.getLong(0));
                        }
                    } finally {
                        memberCur.close();
                    }
                }
                if (rawIdList.isEmpty()) {
                    continue;
                }

                StringBuilder inSb = new StringBuilder();
                for (int i = 0; i < rawIdList.size(); i++) {
                    if (i > 0) {
                        inSb.append(",");
                    }
                    inSb.append(rawIdList.get(i));
                }
                String sel = ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID + " IN (" + inSb + ")";
                Cursor phoneCur = ctx.getContentResolver().query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        new String[]{
                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                                ContactsContract.CommonDataKinds.Phone.NUMBER,
                                ContactsContract.RawContacts.CONTACT_LAST_UPDATED_TIMESTAMP
                        },
                        sel, null, null
                );
                if (phoneCur != null) {
                    try {
                        while (phoneCur.moveToNext()) {
                            ContactTagItem item = new ContactTagItem();
                            item.tag = groupTitle;
                            item.name = phoneCur.getString(0);
                            item.phone = phoneCur.getString(1);
                            item.updateTs = phoneCur.getLong(2);
                            outList.add(item);
                        }
                    } finally {
                        phoneCur.close();
                    }
                }
            }
        } finally {
            groupCursor.close();
        }
        return gson.toJson(outList);
    }
}