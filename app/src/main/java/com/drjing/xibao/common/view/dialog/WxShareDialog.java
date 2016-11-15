package com.drjing.xibao.common.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.drjing.xibao.R;

/**
 * Created by kristain on 16/1/15.
 */
public class WxShareDialog {


    public static Dialog showDialog(final Context context, String message,
                                    final View.OnClickListener cancel_btn,
                                    final View.OnClickListener friend_btn,
                                    final View.OnClickListener circle_btn) {
        View layout = ((Activity) context).getLayoutInflater()
                .inflate(
                        R.layout.share_wx,
                        (ViewGroup) ((Activity) context)
                                .findViewById(R.id.parentPanel));
        ImageButton cancel_imageBtn = (ImageButton) layout.findViewById(R.id.cancel_imageButton);
        TextView friend_share_btn = (TextView) layout.findViewById(R.id.chatButton_textView);
        TextView circle_share_btn = (TextView) layout.findViewById(R.id.enterButton_textView);
        final Dialog payWinDialog = new Dialog(context,
                R.style.Dialog_Fullscreen);// 这里应用了自定义样式
        payWinDialog.setContentView(layout);
        payWinDialog.setCancelable(false);

        if (cancel_btn != null) {
            cancel_imageBtn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    cancel_btn.onClick(v);
                }
            });
        }
        if (friend_btn != null) {
            friend_share_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    friend_btn.onClick(v);
                }
            });
        }
        if (circle_btn != null) {
            circle_share_btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    circle_btn.onClick(v);
                }
            });
        }
        // payWinDialog.message(message, mDrawable);
        // dialog.title(R.string.ll_dialog_msg);
        payWinDialog.show();
        return payWinDialog;
    }

}
