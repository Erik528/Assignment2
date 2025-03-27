package u.edu.utas.wentianw.myapplication

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class EditPlayerActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_player)

        val playerName = intent.getStringExtra("playerName") ?: return

        val killsInput = findViewById<EditText>(R.id.inputKills)
        val kpgInput = findViewById<EditText>(R.id.inputKPG)
        val deathsInput = findViewById<EditText>(R.id.inputDeaths)
        val dpgInput = findViewById<EditText>(R.id.inputDPG)
        val assistsInput = findViewById<EditText>(R.id.inputAssists)
        val apgInput = findViewById<EditText>(R.id.inputAPG)
        val saveButton = findViewById<Button>(R.id.btnSave)

        db.collection("players").document(playerName).get().addOnSuccessListener {
            killsInput.setText(it.getLong("kills")?.toString())
            kpgInput.setText(it.getDouble("kpg")?.toString())
            deathsInput.setText(it.getLong("deaths")?.toString())
            dpgInput.setText(it.getDouble("dpg")?.toString())
            assistsInput.setText(it.getLong("assists")?.toString())
            apgInput.setText(it.getDouble("apg")?.toString())
        }

        saveButton.setOnClickListener {
            val updatedData = hashMapOf(
                "kills" to killsInput.text.toString().toInt(),
                "kpg" to kpgInput.text.toString().toDouble(),
                "deaths" to deathsInput.text.toString().toInt(),
                "dpg" to dpgInput.text.toString().toDouble(),
                "assists" to assistsInput.text.toString().toInt(),
                "apg" to apgInput.text.toString().toDouble()
            )
            db.collection("players").document(playerName).update(updatedData as Map<String, Any>)
                .addOnSuccessListener {
                    Toast.makeText(this, "Data updated", Toast.LENGTH_SHORT).show()
                    finish()
                }
        }
    }
}
