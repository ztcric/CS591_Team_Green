package com.cs591_mobile.hangman;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.cs591_mobile.hangman.models.Game;
import com.google.gson.Gson;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    Game game;
    String guessWord = "";
    ImageView image;
    TextView textViewGoal, textViewHint;
    int currentImage;
    int lives = 6;
    int gotHint = 0;
    Button restart_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        textViewGoal = findViewById(R.id.txtGoal);
        textViewGoal.setGravity(Gravity.CENTER);
        textViewHint = findViewById(R.id.txtHint);
        image = (ImageView) findViewById(R.id.imgHang);
        restart_button = findViewById(R.id.button_restart);


        if (savedInstanceState == null) {
            currentImage = 0;

            game = new Game();
            int wordLength = game.getWord().length();
            guessWord = "";
            for(int i = 0; i < wordLength; i++){
                guessWord += "_";
            }
            updateTextField(textViewGoal);
            Log.i("Kobe", game.getWord());

        } else {
            guessWord = savedInstanceState.getString("guessWord");
            String StringGame = savedInstanceState.getString("game");
            game = new Gson().fromJson(StringGame, Game.class);
            currentImage = savedInstanceState.getInt("currentImage");
            updateTextField(textViewGoal);
            updateImage(image);
            lives = savedInstanceState.getInt("lives");
            gotHint = savedInstanceState.getInt("gotHint");

            Button btnHint = findViewById(R.id.btnHint);
            if (btnHint != null && gotHint == 1) {
                btnHint.setVisibility(View.INVISIBLE);
                textViewHint.setText(game.getHint());
            }
        }
    }

    public void click(View view){
        Button button = (Button)view;
        String str = button.getText().toString();
        if (str.equals("HINT")) {
            textViewHint.setText(game.getHint());
            button.setVisibility(View.INVISIBLE);
            gotHint = 1;
            return;
        }

        Log.i("T", str);
        ArrayList<Integer> indices = game.guess(str);

        if(indices.size() != 0){
            for(Integer i : indices){
                guessWord = guessWord.substring(0, i) + game.getWord().charAt(i) + guessWord.substring(i + 1);
                Log.i("Kobe", guessWord);
                updateTextField(textViewGoal);
            }
        }
        else{
            lives--;
            updateImage(image);
        }
        GameResult gameResult = checkStatus();
        if(gameResult == GameResult.LOSE){
            Toast toast = Toast.makeText(MainActivity.this, "You have got " + game.getScore() + " out of " + game.calculateTotalPoints() , Toast.LENGTH_LONG);
            toast.show();
            showDialog("You lose!!");
        }
        else if(gameResult == GameResult.WIN){
            Toast toast = Toast.makeText(MainActivity.this, "You have got " + game.getScore() + " out of " + game.calculateTotalPoints() , Toast.LENGTH_LONG);
            toast.show();
            showDialog("You win!!");
        }
    }
    public void restartClicked(View view){
        newGame();
    }

    public GameResult checkStatus(){
        if(lives <= 0){
            return GameResult.LOSE;
        }
        else{
            if(guessWord.equals(game.getWord())){
                return GameResult.WIN;
            }
            else{
                return GameResult.PLAYING;
            }
        }
    }

    public void updateTextField(TextView textView){
        String temp = "";
        for(int i = 0; i < guessWord.length(); i++){
            temp += " " + guessWord.charAt(i);
        }
        Log.i("t", guessWord);
        textView.setText(temp);
        textView.setGravity(Gravity.CENTER);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("guessWord", guessWord);
        outState.putString("game", new Gson().toJson(game));
        outState.putInt("currentImage", currentImage - 1);
        outState.putInt("lives", lives);
        outState.putInt("gotHint", gotHint);

        super.onSaveInstanceState(outState);
    }

    public void updateImage(ImageView image) {
        currentImage++;
        switch (currentImage) {
            case 1:
                Log.i("Kobe", "image 1");
                image.setImageResource(R.drawable.hangman1);
                break;
            case 2:
                image.setImageResource(R.drawable.hangman2);
                break;
            case 3:
                image.setImageResource(R.drawable.hangman3);
                break;
            case 4:
                image.setImageResource(R.drawable.hangman4);
                break;
            case 5:
                image.setImageResource(R.drawable.hangman5);
                break;
            case 6:
                image.setImageResource(R.drawable.hangman6);
                // call game over popup because you have reached the limit of incorrect answers
                break;
            default:
                image.setImageResource(R.drawable.hangman0);
                break;
        }
    }

    public void newGame() {
        currentImage = 0; // reset to the original hangman image
        image.setImageResource(R.drawable.hangman0);
        lives = 6;
        gotHint = 0;
        game = new Game();
        int wordLength = game.getWord().length();
        textViewGoal = findViewById(R.id.txtGoal);
        textViewHint = findViewById(R.id.txtHint);
        if(textViewHint != null) {
            textViewHint.setText("");
        }
        guessWord = "";
        for(int i = 0; i < wordLength; i++){
            guessWord += "_";
        }

        Button btnHint = findViewById(R.id.btnHint);
        if (btnHint != null) {
            btnHint.setVisibility(View.VISIBLE);
        }
        updateTextField(textViewGoal);
        Log.i("Kobe", game.getWord());
        textViewGoal.setGravity(Gravity.CENTER);

    }

    public void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(msg)
                .setPositiveButton("Restart", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        newGame();
                    }
                });

        // Create the AlertDialog object and return it
        Dialog dialog = builder.create();
        dialog.show();
    }


}
