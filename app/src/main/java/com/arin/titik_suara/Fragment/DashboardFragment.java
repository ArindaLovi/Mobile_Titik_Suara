package com.arin.titik_suara.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arin.titik_suara.Model.PengaduanModel;
import com.arin.titik_suara.R;
import com.arin.titik_suara.Util.DataApi;
import com.arin.titik_suara.Util.Interface.PengaduanInterface;
import com.arin.titik_suara.Fragment.CreatePengaduanFragment; // Corrected the import
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    FloatingActionButton fabAdd;
    TextView tvPengaduanDiajukan, tvValidasiOSIS;
    SharedPreferences sharedPreferences;
    PengaduanInterface pengaduanInterface;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        fabAdd = view.findViewById(R.id.fabAdd);
        tvPengaduanDiajukan = view.findViewById(R.id.tv_pengaduan_diajukan);
        tvValidasiOSIS = view.findViewById(R.id.tv_validasi);
        sharedPreferences = getContext().getSharedPreferences("user_data", Context.MODE_PRIVATE);

        // Set username as greeting
        String username = sharedPreferences.getString("username", "User");
        TextView tvUsername = view.findViewById(R.id.title1);
        tvUsername.setText("Halo " + username);

        // Retrieve total counts for each status
        getPengaduanCount("diajukan", tvPengaduanDiajukan);
        getPengaduanCount("validasi_osis", tvValidasiOSIS);

        // Set up Floating Action Button to navigate to create pengaduan fragment
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure NewPengaduanFragment is the correct fragment to navigate to
                getFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, new CreatePengaduanFragment()) // Corrected to Fragment
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void getPengaduanCount(String status, TextView tvTotal) {
        pengaduanInterface = DataApi.getClient().create(PengaduanInterface.class);
        String userId = sharedPreferences.getString("user_id", null);
        pengaduanInterface.getPengaduanByStatus(userId, status).enqueue(new Callback<List<PengaduanModel>>() {
            @Override
            public void onResponse(Call<List<PengaduanModel>> call, Response<List<PengaduanModel>> response) {
                if (response.body() != null) {
                    List<PengaduanModel> pengaduanList = response.body();
                    tvTotal.setText(String.valueOf(pengaduanList.size()));
                } else {
                    // If the response is null, show 0
                    tvTotal.setText("0");
                }
            }

            @Override
            public void onFailure(Call<List<PengaduanModel>> call, Throwable t) {
                // Handle failure (e.g., show error message)
                tvTotal.setText("Error");
            }
        });
    }
}