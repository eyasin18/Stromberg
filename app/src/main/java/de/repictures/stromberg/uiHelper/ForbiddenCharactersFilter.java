package de.repictures.stromberg.uiHelper;

import android.app.Activity;
import android.text.InputFilter;
import android.text.Spanned;

import java.util.Arrays;

import de.repictures.stromberg.R;

public class ForbiddenCharactersFilter implements InputFilter {

    private Activity activity;

    public ForbiddenCharactersFilter(Activity activity){
        this.activity = activity;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

        String[] forbiddenCharacters = activity.getResources().getStringArray(R.array.forbidden_characters);
        if (source != null) {
            for (String forbiddenChar : forbiddenCharacters) {
                source = source.toString().replace(forbiddenChar, "");
            }
            return source;
        }
        return null;
    }
}
