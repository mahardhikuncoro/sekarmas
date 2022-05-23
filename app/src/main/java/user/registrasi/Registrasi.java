package user.registrasi;

import android.os.Bundle;

import androidx.annotation.Nullable;

import base.screen.BaseDialogActivity;
import butterknife.ButterKnife;
import id.sekarmas.mobile.application.R;

/**
 * Created by Mahardhi Kuncoro on 5/22/2022
 */
public class Registrasi extends BaseDialogActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi);
        ButterKnife.bind(this);
    }
}
