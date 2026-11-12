package com.example.miprimerapp; // Asegúrate de que coincida con tu paquete

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ListadoActivity extends AppCompatActivity {

    private final String[] cabecera = {"Id", "Nombre", "Apellido", "Email"};
    private final ArrayList<String[]> datos = new ArrayList<>();
    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout);

        dbHelper = new FeedReaderDbHelper(this);
        TableLayout tblistado = findViewById(R.id.tblistado);
        TextView txtvacio = findViewById(R.id.txtvacio);

        TraerDatos();

        if (datos.isEmpty()) {
            txtvacio.setVisibility(View.VISIBLE);
        } else {
            txtvacio.setVisibility(View.GONE);
            DynamicTable tablaDin = new DynamicTable(tblistado, this);
            tablaDin.setCabecera(cabecera);
            tablaDin.setDatos(datos);
            tablaDin.crearCabecera();
            tablaDin.crearFilas();
        }
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    /** Lee todas las filas de la tabla, ordenadas por apellido, y las guarda en "datos". */
    private void TraerDatos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2,
                FeedReaderContract.FeedEntry.column3
        };

        String sortOrder = FeedReaderContract.FeedEntry.column2 + " ASC";

        try (Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable, // tabla
                projection,                             // columnas a devolver
                null,                                   // sin WHERE: todas las filas
                null,
                null,                                   // GROUP BY
                null,                                   // HAVING
                sortOrder                               // ORDER BY apellido ASC
        )) {
            while (cursor.moveToNext()) {
                String[] fila = new String[4];
                fila[0] = String.valueOf(cursor.getLong(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID)));
                fila[1] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
                fila[2] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
                fila[3] = cursor.getString(
                        cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column3));
                datos.add(fila);
            }
        }
    }

    /** Vuelve a la pantalla anterior (MainActivity) sin crear una nueva copia de ella. */
    public void Regresar(View vista) {
        finish();
    }
}