package com.ok.yuuki.janken

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {
    val gu = 0
    val choki = 1
    val pa = 2

    val DRAW = 0
    val COM_LOSE = 1
    val COM_WIN = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val id = intent.getIntExtra("MY_HAND", 0)
        val myHand = when(id) {
            R.id.gu -> {
                myHandImage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.choki -> {
                myHandImage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.pa -> {
                myHandImage.setImageResource(R.drawable.pa)
                pa
            }
            else -> gu
        }

        // select com hand
        val comHand = getHand()
        when(comHand) {
            gu -> comHandImage.setImageResource(R.drawable.com_gu)
            choki -> comHandImage.setImageResource(R.drawable.com_choki)
            pa -> comHandImage.setImageResource(R.drawable.com_pa)
        }

        // which win
        val gameResult = (comHand - myHand + 3) % 3
        when(gameResult) {
            DRAW -> resultLabel.setText(R.string.result_draw)
            COM_LOSE -> resultLabel.setText(R.string.result_win)
            COM_WIN -> resultLabel.setText(R.string.result_lose)
        }

        backButton.setOnClickListener { finish() }

        saveData(myHand, comHand, gameResult)
    }

    val GAME_COUNT = "GAME_COUNT"
    val WINNING_STREAK_COUNT = "WINNING_STREAK_COUNT"
    val LAST_COM_HAND = "LAST_COM_HAND"
    val GAME_RESULT = "GAME_RESULT"
    val LAST_MY_HAND = "LAST_MY_HAND"
    val BEFORE_LAST_COM_HAND = "BEFORE_LAST_COM_HAND"

    private fun saveData(myHand: Int, comHand: Int, gameResult: Int) {
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt(GAME_COUNT, 0)
        val winningStreakCount = pref.getInt(WINNING_STREAK_COUNT, 0)
        val lastComHand = pref.getInt(LAST_COM_HAND, 0)
        val lastGameResult = pref.getInt(GAME_RESULT, -1)

        val editor = pref.edit()
        editor.putInt(GAME_COUNT, gameCount + 1)
                .putInt(WINNING_STREAK_COUNT,
                    if (lastGameResult == COM_WIN && gameResult == COM_WIN) {
                        winningStreakCount + 1
                    } else 0)
                .putInt(LAST_MY_HAND, myHand)
                .putInt(LAST_COM_HAND, comHand)
                .putInt(BEFORE_LAST_COM_HAND, lastComHand)
                .putInt(GAME_RESULT, gameResult)
                .apply()

    }

    private fun getHand(): Int {
        var hand = janken()
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt(GAME_COUNT, 0)
        val winningStreakCount = pref.getInt(WINNING_STREAK_COUNT, 0)
        val lastMyHand = pref.getInt(LAST_MY_HAND, gu)
        val lastComHand = pref.getInt(LAST_COM_HAND, gu)
        val beforeLastComHand = pref.getInt(BEFORE_LAST_COM_HAND, gu)
        val gameResult = pref.getInt(GAME_RESULT, -1)

        if (gameCount == 1) {
            if (gameResult == COM_WIN) {
                while (lastComHand == hand) {
                    hand = janken()
                }
            } else if (gameResult == COM_LOSE) {
                hand = (lastMyHand - 1 + 3) % 3
            }
        } else if (winningStreakCount > 0) {
            if (beforeLastComHand == lastComHand) {
                while (lastComHand == hand) {
                    hand = janken()
                }
            }
        }
        return hand
    }

    private fun janken() = (Math.random() * 3).toInt()
}
