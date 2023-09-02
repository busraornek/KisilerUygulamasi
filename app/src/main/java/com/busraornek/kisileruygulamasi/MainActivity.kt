package com.busraornek.kisileruygulamasi

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.busraornek.kisileruygulamasi.databinding.ActivityMainBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
class MainActivity : AppCompatActivity(),SearchView.OnQueryTextListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var kisilerListe: ArrayList<Kisiler>
    private lateinit var adapter: KisilerAdapter
    private lateinit var refKisiler: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.title = "Kişiler Uygulaması"
        setSupportActionBar(binding.toolbar)

        binding.rv.setHasFixedSize(true)
        binding.rv.layoutManager = LinearLayoutManager(this)

        val dp = FirebaseDatabase.getInstance()
        refKisiler = dp.getReference("kisiler")
        kisilerListe = ArrayList()
        adapter = KisilerAdapter(this,kisilerListe)
        binding.rv.adapter = adapter
        tumKisiler()

        binding.fab.setOnClickListener {
            alertGoster()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu,menu)

        val item = menu?.findItem(R.id.action_ara)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    fun alertGoster(){
        val tasarim = LayoutInflater.from(this).inflate(R.layout.alert_tasarim,null)
        val editTextAd = tasarim.findViewById(R.id.editTextAd) as EditText
        val editTextTel = tasarim.findViewById(R.id.editTextTel) as EditText
        val ad = AlertDialog.Builder(this)

        ad.setTitle("Kişi Ekle")
        ad.setView(tasarim)
        ad.setPositiveButton("Ekle"){ dialogInterface, i->
            val kisiAd = editTextAd.text.toString().trim()
            val kisiTel = editTextTel.text.toString().trim()

            Toast.makeText(applicationContext,"$kisiAd - $kisiTel",Toast.LENGTH_SHORT).show()
        }
        ad.setNegativeButton("İptal"){ dialogInterface , i->
        }
        ad.create().show()

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        Log.e("Gösterilen arama",p0.toString())
       return true
    }
    override fun onQueryTextChange(p0: String?): Boolean {
       Log.e("Harf girdikçe",p0.toString())
        return true
    }
    fun tumKisiler(){
        refKisiler.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot){
                kisilerListe.clear()
                for(c in snapshot.children){
                    val kisi = c.getValue(Kisiler::class.java)

                    if(kisi != null){
                        kisi.kisi_id = c.key
                        kisilerListe.add(kisi)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
            }
        })
    }
}