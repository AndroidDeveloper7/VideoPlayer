package com.kabouzeid.appthemehelper.common.prefs.supportv7.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import androidx.preference.DialogPreference;
import android.view.Window;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

/**
 * @author Karim Abou Zeid (kabouzeid)
 */
@SuppressWarnings("ConstantConditions")
public class ATEPreferenceDialogFragment extends DialogFragment implements MaterialDialog.SingleButtonCallback {
    private DialogAction mWhichButtonClicked;

    protected static final String ARG_KEY = "key";
    private DialogPreference mPreference;

    public static ATEPreferenceDialogFragment newInstance(String key) {
        ATEPreferenceDialogFragment fragment = new ATEPreferenceDialogFragment();
        Bundle b = new Bundle(1);
        b.putString(ARG_KEY, key);
        fragment.setArguments(b);
        return fragment;
    }

    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fragment rawFragment = this.getTargetFragment();
        if (!(rawFragment instanceof androidx.preference.DialogPreference.TargetFragment)) {
            throw new IllegalStateException("Target fragment must implement TargetFragment interface");
        } else {
            androidx.preference.DialogPreference.TargetFragment fragment = (DialogPreference.TargetFragment) rawFragment;
            String key = this.getArguments().getString(ARG_KEY);
            this.mPreference = fragment.findPreference(key);
        }
    }

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity context = this.getActivity();
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(this.mPreference.getDialogTitle())
                .icon(this.mPreference.getDialogIcon())
                .onAny(this)
                .positiveText(this.mPreference.getPositiveButtonText())
                .negativeText(this.mPreference.getNegativeButtonText());

        builder.content(this.mPreference.getDialogMessage());
        this.onPrepareDialogBuilder(builder);
        MaterialDialog dialog = builder.build();
        if (this.needInputMethod()) {
            this.requestInputMethod(dialog);
        }

        return dialog;
    }

    public DialogPreference getPreference() {
        return this.mPreference;
    }

    @SuppressWarnings("EmptyMethod")
    protected void onPrepareDialogBuilder(MaterialDialog.Builder builder) {
    }

    @SuppressWarnings("SameReturnValue")
    protected boolean needInputMethod() {
        return false;
    }

    private void requestInputMethod(Dialog dialog) {
        Window window = dialog.getWindow();
        window.setSoftInputMode(5);
    }

    @Override
    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
        mWhichButtonClicked = which;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        onDialogClosed(mWhichButtonClicked == DialogAction.POSITIVE);
    }

    @SuppressWarnings("EmptyMethod")
    public void onDialogClosed(boolean positiveResult) {

    }
}
