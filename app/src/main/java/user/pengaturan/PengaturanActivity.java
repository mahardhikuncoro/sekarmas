package user.pengaturan;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import base.screen.BaseDialogActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import id.sekarmas.mobile.application.R;
import ops.screen.adapter.PengaturanAdapter;

/**
 * @author KUN <robert.kuncoro@pitik.id>
 */
public class PengaturanActivity extends BaseDialogActivity {
    @BindView(R.id.rv_pengaturan)
    RecyclerView recyclerView;

    PengaturanAdapter pengaturanAdapter;

    List<String> menus = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);
        ButterKnife.bind(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fillMenu();
    }

    private void fillMenu(){
        menus.add("Ubah Kata Sandi");
        menus.add("Tentang Aplikasi");
        menus.add("Pengaturan Perizinan");
        menus.add("Pengaturan Notifikasi");
        pengaturanAdapter = new PengaturanAdapter(this, menus);
        recyclerView.setAdapter(pengaturanAdapter);

    }
}
