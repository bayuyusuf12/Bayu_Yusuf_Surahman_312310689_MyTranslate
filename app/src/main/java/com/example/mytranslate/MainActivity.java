package com.example.mytranslate;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText Sourcelanguage;
    private TextView destinationlanguagetv;
    private MaterialButton sourcelanguagechoosenbtn;
    private MaterialButton destinationlanguagechoosebtn;
    private MaterialButton translatebtn;

    private TranslatorOptions translatorOptions;
    private Translator translator;

    private ProgressDialog progressDialog;

    private ArrayList<ModelLanguage> languageArrayList;

    private static final String TAG = "MAIN_TAG";

    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "id";  // Ganti dengan kode bahasa tujuan
    private String destinationLanguageTitle = "Indonesian"; // Ganti dengan judul bahasa tujuan

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Mohon Tunggu");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailableLanguages();

        Sourcelanguage = findViewById(R.id.Sourcelanguage);
        destinationlanguagetv = findViewById(R.id.destinationlanguagetv);
        sourcelanguagechoosenbtn = findViewById(R.id.sourcelanguagechoosenbtn);
        destinationlanguagechoosebtn = findViewById(R.id.destinationlanguagechoosebtn);
        translatebtn = findViewById(R.id.translatebtn);

        sourcelanguagechoosenbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sourceLanguageChoose();
            }
        });

        destinationlanguagechoosebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destinationLanguageChoose();
            }
        });

        translatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    private String sourceLanguageText = "";

    private void validateData() {
        sourceLanguageText = Sourcelanguage.getText().toString().trim();

        Log.d(TAG, "validateData: sourceLanguageText: " + sourceLanguageText);

        if (sourceLanguageText.isEmpty()) {
            Toast.makeText(this, "Masukan teks untuk di terjemahkan...", Toast.LENGTH_SHORT).show();
        } else {
            startTranslation();
        }
    }

    private void startTranslation() {
        progressDialog.setMessage("Prosesing bahasa");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();

        translator = Translation.getClient(translatorOptions);

        // Pastikan model diunduh jika belum tersedia
        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi() // Mengunduh hanya ketika ada koneksi WiFi
                .build();

        // Mengunduh model jika diperlukan
        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Model berhasil diunduh.");
                    performTranslation();  // Lakukan terjemahan setelah model diunduh
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Gagal mengunduh model: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Gagal mengunduh model: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void performTranslation() {
        // Ambil teks dari input pengguna
        String sourceText = Sourcelanguage.getText().toString().trim();

        // Menangani terjemahan
        translator.translate(sourceText)
                .addOnSuccessListener(translatedText -> {
                    // Menampilkan hasil terjemahan
                    destinationlanguagetv.setText(translatedText);
                    progressDialog.dismiss();  // Tutup dialog progress setelah selesai
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.e(TAG, "Terjemahan gagal: " + e.getMessage());
                    Toast.makeText(MainActivity.this, "Terjemahan gagal: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void sourceLanguageChoose() {
        PopupMenu popupMenu = new PopupMenu(this, sourcelanguagechoosenbtn);

        for (int i = 0; i < languageArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).languageTitle);
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int position = menuItem.getItemId();
            sourceLanguageCode = languageArrayList.get(position).langaugeCode;
            sourceLanguageTitle = languageArrayList.get(position).languageTitle;

            sourcelanguagechoosenbtn.setText(sourceLanguageTitle);
            Sourcelanguage.setHint("Enter " + sourceLanguageTitle);

            Log.d(TAG, "onMenuItemClick: sourceLanguageCode: " + sourceLanguageCode);
            Log.d(TAG, "onMenuItemClick: sourceLanguageTitle: " + sourceLanguageTitle);
            return false;
        });
    }

    private void destinationLanguageChoose() {
        PopupMenu popupMenu = new PopupMenu(this, destinationlanguagechoosebtn);

        for (int i = 0; i < languageArrayList.size(); i++) {
            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getLanguageTitle());
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int position = menuItem.getItemId();
            destinationLanguageCode = languageArrayList.get(position).langaugeCode;
            destinationLanguageTitle = languageArrayList.get(position).languageTitle;

            destinationlanguagechoosebtn.setText(destinationLanguageTitle);

            Log.d(TAG, "onMenuItemClick: destinationLanguageCode: " + destinationLanguageCode);
            Log.d(TAG, "onMenuItemClick: destinationLanguageTitle: " + destinationLanguageTitle);
            return false;
        });
    }

    private void loadAvailableLanguages() {
        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for (String languageCode : languageCodeList) {
            String languageTitle = new Locale(languageCode).getDisplayLanguage();
            Log.d(TAG, "loadAvailableLanguages: languageCode: " + languageCode);
            Log.d(TAG, "loadAvailableLanguages: languageTitle: " + languageTitle);

            ModelLanguage modelLanguage = new ModelLanguage(languageCode, languageTitle);
            languageArrayList.add(modelLanguage);
        }
    }
}
