package mx.edu.potros.practica11

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class MainActivity : AppCompatActivity() {

    private lateinit var txtid: EditText
    private lateinit var txtnom: EditText
    private lateinit var btnbus: Button
    private lateinit var btnmod: Button
    private lateinit var btnreg: Button
    private lateinit var btneli: Button
    private lateinit var lvDatos: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        txtid   = findViewById(R.id.txtid)
        txtnom  = findViewById(R.id.txtnom)
        btnbus  = findViewById(R.id.btnbus)
        btnmod  = findViewById(R.id.btnmod)
        btnreg  = findViewById(R.id.btnreg)
        btneli  = findViewById(R.id.btneli)
        lvDatos = findViewById(R.id.lvDatos)

        botonBuscar()
        botonModificar()
        botonRegistrar()
        botonEliminar()
    }

    private fun botonBuscar() {
        btnbus.setOnClickListener {
            if (txtid.text.toString().trim().isEmpty()) {
                ocultarTeclado()
                Toast.makeText(
                    this@MainActivity,
                    "Digite El ID del Luchador a Buscar!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val id = txtid.text.toString().toInt()
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.java.simpleName)
                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val aux = id.toString()
                        var res = false
                        for (x in snapshot.children) {
                            if (aux.equals(x.child("id").value.toString(), ignoreCase = true)) {
                                res = true
                                ocultarTeclado()
                                txtnom.setText(x.child("nombre").value.toString())
                                break
                            }
                        }
                        if (!res) {
                            ocultarTeclado()
                            Toast.makeText(
                                this@MainActivity,
                                "ID ($aux) No Encontrado!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    private fun botonModificar() {
        btnmod.setOnClickListener {
            if (txtid.text.toString().trim().isEmpty() || txtnom.text.toString().trim().isEmpty()) {
                ocultarTeclado()
                Toast.makeText(
                    this@MainActivity,
                    "Complete Los Campos Faltantes Para Actualizar!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val id = txtid.text.toString().toInt()
                val nom = txtnom.text.toString()
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.java.simpleName)
                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        var res2 = false
                        for (x in snapshot.children) {
                            if (x.child("nombre").value.toString().equals(nom, ignoreCase = true)) {
                                res2 = true
                                ocultarTeclado()
                                Toast.makeText(
                                    this@MainActivity,
                                    "El Nombre ($nom) Ya Existe.\nImposible Modificar!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        }
                        if (!res2) {
                            val aux = id.toString()
                            var res = false
                            for (x in snapshot.children) {
                                if (x.child("id").value.toString().equals(aux, ignoreCase = true)) {
                                    res = true
                                    ocultarTeclado()
                                    x.ref.child("nombre").setValue(nom)
                                    txtid.setText("")
                                    txtnom.setText("")
                                    listarLuchadores()
                                    break
                                }
                            }
                            if (!res) {
                                ocultarTeclado()
                                Toast.makeText(
                                    this@MainActivity,
                                    "ID ($aux) No Encontrado.\nImposible Modificar!!!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                txtid.setText("")
                                txtnom.setText("")
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    private fun botonRegistrar() {
        btnreg.setOnClickListener {
            if (txtid.text.toString().trim().isEmpty() || txtnom.text.toString().trim().isEmpty()) {
                ocultarTeclado()
                Toast.makeText(
                    this@MainActivity,
                    "Complete Los Campos Faltantes!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val id = txtid.text.toString().toInt()
                val nom = txtnom.text.toString()
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.java.simpleName)
                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val aux = id.toString()
                        var res = false
                        for (x in snapshot.children) {
                            if (x.child("id").value.toString().equals(aux, ignoreCase = true)) {
                                res = true
                                ocultarTeclado()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error. El ID ($aux) Ya Existe!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        }
                        var res2 = false
                        for (x in snapshot.children) {
                            if (x.child("nombre").value.toString().equals(nom, ignoreCase = true)) {
                                res2 = true
                                ocultarTeclado()
                                Toast.makeText(
                                    this@MainActivity,
                                    "Error. El Nombre ($nom) Ya Existe!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                break
                            }
                        }
                        if (!res && !res2) {
                            val luc = Luchador(id, nom)
                            dbref.push().setValue(luc)
                            ocultarTeclado()
                            Toast.makeText(
                                this@MainActivity,
                                "Luchador Registrado Correctamente!!",
                                Toast.LENGTH_SHORT
                            ).show()
                            txtid.setText("")
                            txtnom.setText("")
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    private fun listarLuchadores() {
        val db = FirebaseDatabase.getInstance()
        val dbref = db.getReference(Luchador::class.java.simpleName)
        val lisluc = ArrayList<Luchador?>()
        val ada = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, lisluc)
        lvDatos.adapter = ada
        dbref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                val luc = snapshot.getValue(Luchador::class.java)
                lisluc.add(luc)
                ada.notifyDataSetChanged()
            }

            override fun onChildChanged(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
                ada.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(
                snapshot: DataSnapshot,
                @Nullable previousChildName: String?
            ) {
            }

            override fun onCancelled(error: DatabaseError) {}
        })
        lvDatos.onItemClickListener =
            AdapterView.OnItemClickListener { adapterView, view, i, l ->
                val luc = lisluc[i]
                val a: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                a.setCancelable(true)
                a.setTitle("Luchador Seleccionado")
                var msg = "ID : " + luc!!.id + "\n\n"
                msg += "NOMBRE : " + luc.nombre
                a.setMessage(msg)
                a.show()
            }
    }

    private fun botonEliminar() {
        btneli.setOnClickListener {
            if (txtid.text.toString().trim().isEmpty()) {
                ocultarTeclado()
                Toast.makeText(
                    this@MainActivity,
                    "Digite El ID del Luchador a Eliminar!!",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val id = txtid.text.toString().toInt()
                val db = FirebaseDatabase.getInstance()
                val dbref = db.getReference(Luchador::class.java.simpleName)
                dbref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val aux = id.toString()
                        val res = booleanArrayOf(false)
                        for (x in snapshot.children) {
                            if (aux.equals(x.child("id").value.toString(), ignoreCase = true)) {
                                val a: AlertDialog.Builder = AlertDialog.Builder(this@MainActivity)
                                a.setCancelable(false)
                                a.setTitle("Pregunta")
                                a.setMessage("¿Está Seguro(a) De Querer Eliminar El Registro?")
                                a.setNegativeButton("Cancelar",
                                    DialogInterface.OnClickListener { dialogInterface, i -> })
                                a.setPositiveButton("Aceptar",
                                    DialogInterface.OnClickListener { dialogInterface, i ->
                                        res[0] = true
                                        ocultarTeclado()
                                        x.ref.removeValue()
                                        listarLuchadores()
                                    })
                                a.show()
                                break
                            }
                        }
                        if (!res[0]) {
                            ocultarTeclado()
                            Toast.makeText(
                                this@MainActivity,
                                "ID ($aux) No Encontrado.\nImposible Eliminar!!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }
        }
    }

    private fun ocultarTeclado() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}









