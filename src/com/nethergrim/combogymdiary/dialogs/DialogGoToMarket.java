package com.nethergrim.combogymdiary.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.nethergrim.combogymdiary.R;
import com.yandex.metrica.Counter;

public class DialogGoToMarket extends DialogFragment implements OnClickListener {

    public final static String MARKET_LEAVED_FEEDBACK = "market_leaved_feedback";
    public final static String TRAININGS_DONE_NUM = "trainings_done_num";
    private SharedPreferences sp;

    @Override
    public void onClick(DialogInterface dialog, int which) {

        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                sp.edit().putBoolean(MARKET_LEAVED_FEEDBACK, true).apply();
                try {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("market://details?id=com.nethergrim.combogymdiary")));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("http://play.google.com/store/apps/details?id=com.nethergrim.combogymdiary")));
                }
                Counter.sharedInstance().reportEvent("Go to martet: YES");
                break;
            case Dialog.BUTTON_NEGATIVE:
                sp.edit().putBoolean(MARKET_LEAVED_FEEDBACK, false).apply();
                Counter.sharedInstance().reportEvent("Go to martet: NO");
                break;
            case Dialog.BUTTON_NEUTRAL:
                sp.edit().putInt(TRAININGS_DONE_NUM, 1).apply();
                Counter.sharedInstance().reportEvent("Go to martet: LATER");
                break;
        }
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        sp = PreferenceManager.getDefaultSharedPreferences(getActivity()
                .getApplicationContext());
        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle("Google Play Market")
                .setPositiveButton(R.string.yes, this)
                .setNegativeButton(R.string.no, this)
                .setNeutralButton(R.string.later, this)
                .setMessage(R.string.leave_feedback_market)
                .setIcon(android.R.drawable.btn_star_big_on);
        return adb.create();
    }

}
