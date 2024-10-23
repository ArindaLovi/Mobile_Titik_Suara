package com.arin.titik_suara.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.arin.titik_suara.R;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

public class TambahPengaduanFragment extends Fragment {
    private EditText etKodePengaduan, etJenisKerusakan, etIsiLaporan, etPathImage;
    private Spinner spinnerJenisKerusakan;
    private ImageView ivPreviewImg;
    private Button btnKirim;
    private MaterialButton btnEditImagePicker, btnEditCancel, btnEditSimpanPengaduan;
    private AlertDialog imagePickerDialog;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tambah_pengaduan_baru, container, false);

        // Inisialisasi views dari fragment tambah pengaduan
        initViews(view);
        // Setup spinner jenis kerusakan
        setupSpinner();
        // Setup listeners
        setupListeners();
        // Generate kode pengaduan otomatis
        generateKodePengaduan();

        return view;
    }

    private void initViews(View view) {
        etKodePengaduan = view.findViewById(R.id.etKodePengaduan);
        etJenisKerusakan = view.findViewById(R.id.JenisKerusakan);
        etIsiLaporan = view.findViewById(R.id.etIsiLaporan);
        spinnerJenisKerusakan = view.findViewById(R.id.spinnerJenisKerusakan);
        btnKirim = view.findViewById(R.id.btnKirim);

        // Inflate layout edit image sebagai dialog
        View dialogView = getLayoutInflater().inflate(R.layout.layout_edit_image, null);
        imagePickerDialog = new AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .create();

        // Inisialisasi views dari layout edit image
        ivPreviewImg = dialogView.findViewById(R.id.ivPreviewImg);
        etPathImage = dialogView.findViewById(R.id.etPathImage);
        btnEditImagePicker = dialogView.findViewById(R.id.btnEditImagePicker);
        btnEditCancel = dialogView.findViewById(R.id.btnEditCancel);
        btnEditSimpanPengaduan = dialogView.findViewById(R.id.btnEditSimpanPengaduan);
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.jenis_kerusakan_array, // Buat array resource untuk jenis kerusakan
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerJenisKerusakan.setAdapter(adapter);
    }

    private void setupListeners() {
        btnKirim.setOnClickListener(v -> {
            if (validateInput()) {
                showImagePickerDialog();
            }
        });

        btnEditImagePicker.setOnClickListener(v -> {
            openImagePicker();
        });

        btnEditCancel.setOnClickListener(v -> {
            imagePickerDialog.dismiss();
        });

        btnEditSimpanPengaduan.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                submitPengaduan();
            } else {
                Toast.makeText(requireContext(), "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generateKodePengaduan() {
        // Format: PGD-YYYYMMDD-XXX
        SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy", Locale.getDefault());
        String date = sdf.format(new Date());
        String random = String.format("%03d", new Random().nextInt(1000));
        String kodePengaduan = "PGD-" + date + "-" + random;
        etKodePengaduan.setText(kodePengaduan);
    }

    private boolean validateInput() {
        if (etJenisKerusakan.getText().toString().trim().isEmpty()) {
            etJenisKerusakan.setError("Jenis kerusakan harus diisi");
            return false;
        }
        if (etIsiLaporan.getText().toString().trim().isEmpty()) {
            etIsiLaporan.setError("Isi laporan harus diisi");
            return false;
        }
        return true;
    }

    private void showImagePickerDialog() {
        imagePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pilih Gambar"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                // Tampilkan preview image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                ivPreviewImg.setImageBitmap(bitmap);

                // Set path image
                String path = getPathFromUri(selectedImageUri);
                etPathImage.setText(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = requireActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;

        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();

        return path;
    }

    private void submitPengaduan() {
        // Buat object pengaduan
        Pengaduan pengaduan = new Pengaduan(
                etKodePengaduan.getText().toString(),
                etJenisKerusakan.getText().toString(),
                spinnerJenisKerusakan.getSelectedItem().toString(),
                etIsiLaporan.getText().toString(),
                selectedImageUri.toString()
        );

        // Di sini tambahkan kode untuk menyimpan data pengaduan
        // Misalnya menggunakan API atau database lokal

        // Setelah berhasil
        Toast.makeText(requireContext(), "Pengaduan berhasil dikirim", Toast.LENGTH_SHORT).show();
        imagePickerDialog.dismiss();
        // Reset form
        resetForm();
    }

    private void resetForm() {
        etJenisKerusakan.setText("");
        etIsiLaporan.setText("");
        spinnerJenisKerusakan.setSelection(0);
        generateKodePengaduan();
        selectedImageUri = null;
        ivPreviewImg.setImageResource(R.drawable.twotone_crop_original_24);
        etPathImage.setText("");
    }

    // Class model untuk pengaduan
    private static class Pengaduan {
        String kodePengaduan;
        String jenisKerusakan;
        String jenisKerusakanSpinner;
        String isiLaporan;
        String imagePath;

        public Pengaduan(String kodePengaduan, String jenisKerusakan,
                         String jenisKerusakanSpinner, String isiLaporan, String imagePath) {
            this.kodePengaduan = kodePengaduan;
            this.jenisKerusakan = jenisKerusakan;
            this.jenisKerusakanSpinner = jenisKerusakanSpinner;
            this.isiLaporan = isiLaporan;
            this.imagePath = imagePath;
        }
    }
}
