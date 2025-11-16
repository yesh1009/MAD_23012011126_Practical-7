package com.example.MAD_23012011126_Practical_7

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.MAD_23012011126_Practical_7.db.DatabaseHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    val personList = ArrayList<Person>()
    lateinit var personsRecycleAdapter: PersonAdapter
    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView1)
        recyclerView.clipToPadding = false
        personsRecycleAdapter = PersonAdapter(this, personList)
        recyclerView.adapter = personsRecycleAdapter

        db = DatabaseHelper(applicationContext)

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener {
            networkDb()
        }

        getPersonDetailsFromSqliteDb()
    }

    private fun getPersonDetailsFromSqliteDb() {
        personList.clear()
        personList.addAll(db.allPersons)
        personsRecycleAdapter.notifyDataSetChanged()

        Toast.makeText(this, "Fetched details from DB", Toast.LENGTH_SHORT).show()
    }

    fun deletePerson(position: Int) {
        val deletedPerson = personList[position]
        db.deletePerson(deletedPerson)
        personList.removeAt(position)
        personsRecycleAdapter.notifyItemRemoved(position)

        Toast.makeText(this, "At $position, ${deletedPerson.name} Deleted", Toast.LENGTH_SHORT).show()
    }

    private fun networkDb() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val data = HttpRequest().makeServiceCall(
                    "https://api.json-generator.com/templates/4ooi5trCSIlq/data",
                    "lr41aflkb9ic0wdm2wnjjpwurymj8yvuc0q08085"
                )

                withContext(Dispatchers.Main) {
                    if (data != null) {
                        getPersonDetailsFromJson(data)
                    } else {
                        Toast.makeText(this@MainActivity, "No data received", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun getPersonDetailsFromJson(sJson: String) {

        db.deleteAllPersons()   // IMPORTANT FIX

        personList.clear()

        try {
            val jsonArray = JSONArray(sJson)

            for (i in 0 until jsonArray.length()) {
                val jsonObject = jsonArray[i] as JSONObject
                val person = Person(jsonObject)
                personList.add(person)

                db.insertPerson(person)
            }

            personsRecycleAdapter.notifyDataSetChanged()

        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Toast.makeText(this@MainActivity, "Fetched details from JSON", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_db -> {
                getPersonDetailsFromSqliteDb()
                true
            }
            R.id.action_newdb -> {
                networkDb()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}