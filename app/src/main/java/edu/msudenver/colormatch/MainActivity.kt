package edu.msudenver.colormatch

/*
 * CS3013 - Mobile App Dev. - Summer 2022
 * Instructor: Thyago Mota
 * Student(s): Horace Alexander
 * Description: App 03 - MainActivity (controller) class
 */

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity(), View.OnLongClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var dbHelper: DBHelper

    // TODOd #1: create the ScoreHolder inner class
    private inner class ScoreHolder(view: View): RecyclerView.ViewHolder(view) {
        val txtDate: TextView = view.findViewById(R.id.txtDate)
        val txtScore: TextView = view.findViewById(R.id.txtScore)
        val image: ImageView = view.findViewById(R.id.imageDifficulty)
    }

    // TODO #2: create the ScoreAdapter inner class
    // a score adapter binds scores from a list to holder objects in a recycler view
    private inner class ScoreAdapter(var scores: List<Score>, var onLongClickListener: View.OnLongClickListener): RecyclerView.Adapter<ScoreHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
            view.setOnLongClickListener(onLongClickListener)
            return ScoreHolder(view)
        }

        override fun onBindViewHolder(holder: ScoreHolder, position: Int) {
            val score = scores[position]
            holder.txtScore.text = score.value.toString()
            holder.txtDate.text = DBHelper.USA_FORMAT.format(score.date)

        }

        override fun getItemCount(): Int {
            return scores.size
        }
    }

    // TODO #3: populate the recycler view
    // this function should query the database for all of the scores; then uses a list to update the recycler view's adapter
    // don't forget to call "sort()" on your list so the items are displayed in the correct order
    fun populateRecyclerView() {
        val db = dbHelper.readableDatabase
        val items = mutableListOf<Score>()
        val cursor = db.query(
            "scores",
            arrayOf("rowid", "date", "level", "value"),
            null,
            null,
            null,
            null,
            null,
            null
        )
        with (cursor) {
            while (moveToNext()){
                val id = getInt(0)
                val creationDate = DBHelper.ISO_FORMAT.parse(getString(1))
                val level = getInt(2)
                val value = getDouble(3)
                val item = Score(id, creationDate, level, value)
                items.add(item)
                items.sort()
            }
        }
        recyclerView.adapter = ScoreAdapter(items, this)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DBHelper(this)

        // TODO #4: create and populate the recycler view
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        populateRecyclerView()


        // TODO #5: initialize the floating action button
        val fabCreate: FloatingActionButton = findViewById(R.id.fabCreate)
        fabCreate.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        populateRecyclerView()
    }

    // TODO #6: delete the long tapped item after a yes/no confirmation dialog
    override fun onLongClick(view: View?): Boolean {

        class MyDialogInterfaceListener(val id: Int): DialogInterface.OnClickListener {
            override fun onClick(dialogInterface: DialogInterface?, which: Int) {
                if (which == DialogInterface.BUTTON_POSITIVE) {
                    try {
                        val db = dbHelper.writableDatabase
                        db.execSQL("""
                            DELETE FROM scores
                            WHERE rowid = "$id"
                        """)
                        populateRecyclerView()
                    }
                    catch (ex: Exception) {

                    }
                }
            }
        }

        if (view != null) {
            val id = view.findViewById<TextView>(R.id.txtId).text.toString().toInt()
            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setMessage("Are you sure you want to delete this score?")
            alertDialogBuilder.setPositiveButton("Yes", MyDialogInterfaceListener(id))
            alertDialogBuilder.setNegativeButton("No", MyDialogInterfaceListener(id))
            alertDialogBuilder.show()
            return true
        }
        return false
    }
}