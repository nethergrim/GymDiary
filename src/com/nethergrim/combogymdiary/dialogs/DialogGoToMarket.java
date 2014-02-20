package com.nethergrim.combogymdiary.dialogs;

import com.nethergrim.combogymdiary.R;
import com.nethergrim.combogymdiary.activities.BasicMenuActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class DialogGoToMarket extends DialogFragment implements OnClickListener {

	private SharedPreferences sp;

	@Override
	public void onClick(DialogInterface dialog, int which) {

		switch (which) {
		case Dialog.BUTTON_POSITIVE:
			sp.edit()
					.putBoolean(BasicMenuActivity.MARKET_LEAVED_FEEDBACK, true)
					.apply();
			try {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("market://details?id=com.nethergrim.combogymdiary")));
			} catch (android.content.ActivityNotFoundException anfe) {
				startActivity(new Intent(
						Intent.ACTION_VIEW,
						Uri.parse("http://play.google.com/store/apps/details?id=com.nethergrim.combogymdiary")));
			}
			break;
		case Dialog.BUTTON_NEGATIVE:
			sp.edit()
					.putBoolean(BasicMenuActivity.MARKET_LEAVED_FEEDBACK, false)
					.apply();
			break;
		case Dialog.BUTTON_NEUTRAL:
			sp.edit().putInt(BasicMenuActivity.TRAININGS_DONE_NUM, 1).apply();
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
