package com.example.miprimerapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText etNombre, etApellido;
    Button btnGuardar, btnConsultar, btnActualizar, btnEliminar;
    TableLayout tableLayout;
    FeedReaderDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnConsultar = findViewById(R.id.btnConsultar);
        btnActualizar = findViewById(R.id.btnActualizar);
        btnEliminar = findViewById(R.id.btnEliminar);
        tableLayout = findViewById(R.id.tableLayout);

        dbHelper = new FeedReaderDbHelper(this);

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.column1, nombre);
            values.put(FeedReaderContract.FeedEntry.column2, apellido);

            long newRowId = db.insert(FeedReaderContract.FeedEntry.nameTable, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Usuario guardado exitosamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                consultarYMostrarTodos();
            } else {
                Toast.makeText(this, "Error al guardar el registro", Toast.LENGTH_SHORT).show();
            }
        });

        btnConsultar.setOnClickListener(v -> {
            consultarYMostrarTodos();
            Toast.makeText(this, "Registros consultados", Toast.LENGTH_SHORT).show();
        });

        btnActualizar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String apellido = etApellido.getText().toString().trim();

            if (nombre.isEmpty() || apellido.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre y el nuevo apellido para actualizar", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(FeedReaderContract.FeedEntry.column2, apellido);

            String selection = FeedReaderContract.FeedEntry.column1 + " LIKE ?";
            String[] selectionArgs = {nombre};

            int count = db.update(
                    FeedReaderContract.FeedEntry.nameTable,
                    values,
                    selection,
                    selectionArgs
            );

            if (count > 0) {
                Toast.makeText(this, "Usuario actualizado correctamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                consultarYMostrarTodos();
            } else {
                Toast.makeText(this, "No se encontró un usuario con ese nombre", Toast.LENGTH_SHORT).show();
            }
        });

        btnEliminar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingresa el nombre del usuario a eliminar", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();
            String selection = FeedReaderContract.FeedEntry.column1 + " LIKE ?";
            String[] selectionArgs = {nombre};

            int deletedRows = db.delete(FeedReaderContract.FeedEntry.nameTable, selection, selectionArgs);

            if (deletedRows > 0) {
                Toast.makeText(this, "Usuario eliminado correctamente", Toast.LENGTH_SHORT).show();
                limpiarCampos();
                consultarYMostrarTodos();
            } else {
                Toast.makeText(this, "No se encontró el usuario para eliminar", Toast.LENGTH_SHORT).show();
            }
        });

        consultarYMostrarTodos();
    }

    private void consultarYMostrarTodos() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                FeedReaderContract.FeedEntry._ID,
                FeedReaderContract.FeedEntry.column1,
                FeedReaderContract.FeedEntry.column2
        };

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.nameTable,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        ArrayList<String[]> listaDatos = new ArrayList<>();
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry._ID));
            String nombre = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column1));
            String apellido = cursor.getString(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.column2));
            listaDatos.add(new String[]{id, nombre, apellido});
        }
        cursor.close();

        tableLayout.removeAllViews();

        DynamicTable dynamicTable = new DynamicTable(tableLayout, this);
        dynamicTable.setCabecera(new String[]{"ID", "Nombre", "Apellido"});
        dynamicTable.setDatos(listaDatos);
        dynamicTable.crearCabecera();
        dynamicTable.crearFilas();
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etApellido.setText("");
    }
}