package e.user.mistridada;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

public class genericWatcher implements TextWatcher {
    View view;
    public genericWatcher(View view)
    {
        this.view=view;
    }
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text=s.toString();
        switch (view.getId())
        {
            case R.id.digit_1:
            {
                if(text.length()==1)
                {
              //      R.id.digit_2.requestFocus();
                }
                break;
            }
            case R.id.digit_2:
            {
                if(text.length()==1)
                {
                  //  digit_3.requestFocus();
                }
                else if(text.length()==0)
                {
                   // digit_1.requestFocus();
                }
                break;
            }
        }
    }
}
