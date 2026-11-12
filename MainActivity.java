package com.example.miprimerapp; // Asegúrate de que coincida con tu paquete

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText txtid;
    private EditText txtnombre;
    private EditText txtapellido;
    private EditText txtemail;
    private FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtid = findViewById(R.id.txtid);
        txtnombre = findViewById(R.id.txtnombre);
        txtapellido = findViewById(R.id.txtapellido);
        txtemail = findViewById(R.id.txtemail);

        // El helper se crea cuando la Activity ya tiene un contexto valido
        dbHelper = new FeedReaderDbHelper(this);
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    // ---------- Navegacion ----------

    public void Listar(View vista) {
        startActivity(new Intent(this, ListadoActivity.class));
    }

    // ---------- CREATE (Guardar) ----------

    public void Guardar(View vista) {
        String nombre = txtnombre.getText().toString().trim();
        String apellido = txtapellido.getText().toString().trim();
        String email = txtemail.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingrese nombre, apellido y email");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        values.put(FeedReaderContract.FeedEntry.column3, email);

        long newRowId = db.insert(FeedReaderContract.FeedEntry.nameTable, null, values);

        if (newRowId == -1) {
            mostrarMensaje("No se pudo guardar el registro");
        } else {
            txtid.setText(String.valueOf(newRowId));
            mostrarMensaje("Se guardó el registro con clave: " + newRowId);
        }
    }

    // ---------- READ (Buscar) ----------

    public void Buscar(View vista) {
        String id = leerId();
        if (id == null) return;

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2,
                FeedReaderContract.FeedEntry.column3
        };

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};

        // try-with-resources cierra el Cursor automaticamente al terminar
        try (Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable, // tabla
                projection,                             // columnas a devolver
                selection,                              // clausula WHERE
                selectionArgs,                          // valores para WHERE
                null,                                   // GROUP BY
                null,                                   // HAVING
                null                                    // ORDER BY
        )) {
            if (cursor.moveToFirst()) {
                txtnombre.setText(cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1)));
                txtapellido.setText(cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2)));
                mostrarMensaje("Registro encontrado");
            } else {
                limpiarCampos();
                mostrarMensaje("No existe un registro con Id " + id);
            }
        }
    }

    // ---------- UPDATE (Actualizar) ----------

    public void Actualizar(View vista) {
        String id = leerId();
        if (id == null) return;

        String nombre = txtnombre.getText().toString().trim();
        String apellido = txtapellido.getText().toString().trim();
        String email = txtemail.getText().toString().trim();

        if (nombre.isEmpty() || apellido.isEmpty()) {
            mostrarMensaje("Ingrese nombre, apellido y email");
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.column1, nombre);
        values.put(FeedReaderContract.FeedEntry.column2, apellido);
        values.put(FeedReaderContract.FeedEntry.column3, email);

        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};

        int count = db.update(
                FeedReaderContract.FeedEntry.nameTable,
                values,
                selection,
                selectionArgs);

        if (count > 0) {
            mostrarMensaje("Se actualizó " + count + " registro(s)");
        } else {
            mostrarMensaje("No existe un registro con ese ID");
        }
    }

    // ---------- DELETE (Eliminar) ----------

    public void Eliminar(View vista) {
        String id = leerId();
        if (id == null) return;

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = FeedReaderContract.FeedEntry._ID + " = ?";
        String[] selectionArgs = {id};

        int deletedRows = db.delete(FeedReaderContract.FeedEntry.nameTable, selection, selectionArgs);

        if (deletedRows > 0) {
            limpiarCampos();
        }

        mostrarMensaje("Se eliminó " + deletedRows + " registro(s)");
    }

    // ---------- METODOS AUXILIARES ----------

    /** Devuelve el Id escrito, o null (y avisa) si el campo esta vacio. */
    private String leerId() {
        String id = txtid.getText().toString().trim();
        if (id.isEmpty()) {
            mostrarMensaje("Ingrese el Id del registro");
            return null;
        }
        return id;
    }

    private void limpiarCampos() {
        txtid.setText("");
        txtnombre.setText("");
        txtapellido.setText("");
        txtemail.setText("");
    }

    private void mostrarMensaje(String mensaje) {
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
    }
}