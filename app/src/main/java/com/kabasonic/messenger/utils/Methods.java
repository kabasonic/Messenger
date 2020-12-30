package com.kabasonic.messenger.utils;

import com.kabasonic.messenger.R;

public class Methods {
    public void setColorTheme(){

        switch (Constant.color){
            case 0xffF44336:
                Constant.theme = R.style.AppTheme_red;
                break;
            case 0xffE91E63:
                Constant.theme = R.style.AppTheme_pink;
                break;
            case 0xff3F51B5:
                Constant.theme = R.style.AppTheme_blue;
                break;
            case 0xff4CAF50:
                Constant.theme = R.style.AppTheme_green;
                break;
            case 0xffFF9800:
                Constant.theme = R.style.AppTheme;
                break;
            case 0xFF7B1FA2:
                Constant.theme = R.style.AppTheme_purple;
                break;
            default:
                Constant.theme = R.style.AppTheme;
                break;
        }
    }
}
