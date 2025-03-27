package au.edu.utas.kit305.tutorial05

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import u.edu.utas.wentianw.myapplication.R

class CreateMatchActivity : AppCompatActivity() {
    private val db = Firebase.firestore
    private val teamPlayers = mapOf(
        "IG" to listOf("TheShy (TOP)", "Rookie (MID)"),
        "RNG" to listOf("Xiaohu (MID)", "GALA (ADC)"),
        "EDG" to listOf("Scout (MID)", "Meiko (SUP)"),
        "WE" to listOf("Shanks (MID)", "Missing (SUP)")
    )

    private var selectedTeamA: String? = null
    private var selectedTeamB: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_match)

        val tournamentSpinner = findViewById<Spinner>(R.id.tournamentSpinner)
        val teamNameSpinner = findViewById<Spinner>(R.id.teamNameSpinner)
        val playerListLayout = findViewById<LinearLayout>(R.id.playerListLayout)
        val teamToggle = findViewById<RadioGroup>(R.id.teamToggle)
        val btnStartMatch = findViewById<Button>(R.id.btnStartMatch)

        val tournamentAdapter = ArrayAdapter.createFromResource(
            this,
            R.array.tournament_names,
            android.R.layout.simple_spinner_item
        )
        tournamentSpinner.adapter = tournamentAdapter

        val fullTeamList = resources.getStringArray(R.array.team_names).toList()
        val teamAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, fullTeamList.toMutableList())
        teamNameSpinner.adapter = teamAdapter

        // 监听选择团队
        teamNameSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: android.view.View?, position: Int, id: Long) {
                val selectedTeam = fullTeamList[position]
                if (teamToggle.checkedRadioButtonId == R.id.radioTeamA) {
                    selectedTeamA = selectedTeam
                } else {
                    selectedTeamB = selectedTeam
                }

                updateTeamList(teamNameSpinner, teamAdapter)
                updatePlayerList(selectedTeam, playerListLayout)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        teamToggle.setOnCheckedChangeListener { _, _ ->
            updateTeamList(teamNameSpinner, teamAdapter)
        }

        btnStartMatch.setOnClickListener {
            if (selectedTeamA == null || selectedTeamB == null || selectedTeamA == selectedTeamB) {
                Toast.makeText(this, "请选择两个不同的队伍", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val matchData = hashMapOf(
                "tournament" to tournamentSpinner.selectedItem.toString(),
                "teamA" to selectedTeamA,
                "teamB" to selectedTeamB
            )

            db.collection("matches").add(matchData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Match created！", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "create failed", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun updateTeamList(spinner: Spinner, adapter: ArrayAdapter<String>) {
        val current = if (findViewById<RadioButton>(R.id.radioTeamA).isChecked) selectedTeamB else selectedTeamA
        val allTeams = resources.getStringArray(R.array.team_names).toList()
        val filteredTeams = allTeams.filter { it != current }
        adapter.clear()
        adapter.addAll(filteredTeams)
        adapter.notifyDataSetChanged()
    }

    private fun updatePlayerList(team: String, layout: LinearLayout) {
        layout.removeAllViews()
        teamPlayers[team]?.forEach { name ->
            val view = TextView(this)
            view.text = name
            view.textSize = 16f
            layout.addView(view)
        }
    }
}
