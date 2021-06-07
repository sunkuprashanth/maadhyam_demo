package com.example.android.maadhyam;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

public class GenericTextWatcher implements TextWatcher {
    private final EditText[] editText;
    private View view;

    public GenericTextWatcher(EditText[] editText, View view) {
        this.editText = editText;
        this.view = view;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String text = editable.toString();
        switch(view.getId()){
            case R.id.et1:
                if(text.length()==1)
                    editText[1].requestFocus();
                break;
            case R.id.et2:
                if(text.length()==1)
                    editText[2].requestFocus();
                break;
            case R.id.et3:
                if(text.length()==1)
                    editText[3].requestFocus();
                break;
            case R.id.et4:
                if(text.length()==1)
                    editText[4].requestFocus();
                break;
            case R.id.et5:
                if(text.length()==1)
                    editText[5].requestFocus();
                break;
        }
    }
}
