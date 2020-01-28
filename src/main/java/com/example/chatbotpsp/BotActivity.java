package com.example.chatbotpsp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatbotpsp.API.ChatterBot;
import com.example.chatbotpsp.API.ChatterBotFactory;
import com.example.chatbotpsp.API.ChatterBotSession;
import com.example.chatbotpsp.API.ChatterBotType;
import com.example.chatbotpsp.API.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class BotActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private ArrayList<String> result = null;
    private static final int CODIGO_STT = 100;
    private TextToSpeech textToSpeech;
    ImageButton btSend, btVoice;
    EditText etInput;
    TextView tvTest;
    pl.droidsonroids.gif.GifImageView gif;
    ChatterBot bot;
    ChatterBotSession botSession;
    RecyclerView recyclerView;
    AdapterMultiType adapter;
    String lngFrom = "es", lngTo = "en";
    String str = "";
    String translation = "";
    String strTrad = "";
    String strTradBot = "";
    String response = "";
    public static final String TAG = "marcos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bot);
        textToSpeech = new TextToSpeech(this, this);
        initComponents();
        initEvents();
        initBot();

    }

    private void initComponents() {
        btSend = findViewById(R.id.btSend);
        btVoice = findViewById(R.id.btVoice);
        etInput = findViewById(R.id.etInput);
        recyclerView = findViewById(R.id.recyclerView);
        gif = findViewById(R.id.gif);
        tvTest = findViewById(R.id.tvTest);

        adapter = new AdapterMultiType(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(adapter);
    }

    private void initEvents() {

        btVoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;

            }
        });
        btVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(v.getContext(),"Illo dime lo que quieras",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Illo dime lo que quieras");
                try {
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, CODIGO_STT);
                    } else {
                        Toast.makeText(v.getContext(),
                                "Tu dispositivo no soporta STT", Toast.LENGTH_SHORT).show();
                    }

                } catch (ActivityNotFoundException e) {

                }
            }
        });

        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btSend.setClickable(false);
                str = etInput.getText().toString();
                Log.v("megustanlosquecito", "Frase español usuario: " + str);
                BotActivity.TranslateToEng translateTask = new BotActivity.TranslateToEng(str);
                adapter.mensajes.add(new Mensaje(etInput.getText().toString(), true));
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
                etInput.setText("");
                gif.setVisibility(View.VISIBLE);
                lngFrom = "es";
                lngTo = "en";
                translateTask.execute();
            }
        });
    }

    public void doTheChat() {
        gif.setVisibility(View.GONE);
        tvTest.setText(tvTest.getText() + "User: " + translation + "\n");
        new BotActivity.Chat().execute();
    }

    private void initBot() {
        ChatterBotFactory factory = new ChatterBotFactory();

        try {
            bot = factory.create(ChatterBotType.PANDORABOTS, "b0dafd24ee35a477");
        } catch (Exception e) {
            e.printStackTrace();
        }

        botSession = bot.createSession();
    }

    private void chat(String msg) {
        try {
            Log.v("megustanlosquecito", "Frase ingles usuario: " + msg);
            strTrad = msg;
            response = botSession.think(msg);
            Log.v("megustanlosquecito", "FRASE INGLES BOT: " + response);

            new BotActivity.TranslateToEs(response).execute();

        } catch (Exception e) {
            Log.v("xyz", "Error: " + e.getMessage());
        }
    }

    private void showBotResponse() {
        adapter.mensajes.add(new Mensaje(translation, false));
        strTradBot = translation;
        Log.v("megustanlosquecito", "FRASE ESPAÑOL BOT: " + translation);
        hablarBot(translation);
        adapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
        //btSend.setClickable(true);
        gif.setVisibility(View.GONE);

        uploadMessages();
    }

    private void uploadMessages() {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        String uidAuth = FirebaseAuth.getInstance().getCurrentUser().getUid(); // coge id usuario logueado en uidAuth
        DatabaseReference referenciaItem = database.getReference("user/"+uidAuth); // coge la ruta de referencia de user/la id del usuario

        Date currentDatetime = Calendar.getInstance().getTime();

        // ZoneId zona = ZoneId.systemDefault();
        // LocalDate ahora = LocalDate.now();
        // ZonedDateTime inicioHoy = ahora.atStartOfDay(zona);
        // Instant instante = inicioHoy.toInstant();
        // Date fecha = Date.from(instante);

        String date = "" + currentDatetime.getTime();

        String time = currentDatetime.getHours()+":"+currentDatetime.getMinutes();
        /*referenciaItem.setValue("valor item");
        referenciaItem.child("uno").setValue("hola");

        String key = referenciaItem.push().getKey();
        referenciaItem.child(key).setValue("hola");*/

        // PERSONA
        ChatSentence item = new ChatSentence(strTrad, str, "User", time);
        Map<String, Object> map = new HashMap<>();
        String key = referenciaItem.child(date).push().getKey();
        map.put(date + "/" + key, item.toMap());
        referenciaItem.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
        // BOT
        item = new ChatSentence(response, strTradBot, "Bot", time);
        key = referenciaItem.child(date).push().getKey();
        map.put(date + "/" + key, item.toMap());
        referenciaItem.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });

        referenciaItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "data changed: " + dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "error: " + databaseError.toException());
            }
        });

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "This Language is not supported", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void hablarBot(String translation) {
        textToSpeech.speak(translation, TextToSpeech.QUEUE_FLUSH, null);
    }

    @Override
    public void onDestroy() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }

    public String decomposeJson(String json) {
        String translationResult = "Could not get";
        try {
            JSONArray arr = new JSONArray(json);
            JSONObject jObj = arr.getJSONObject(0);
            translationResult = jObj.getString("translations");
            JSONArray arr2 = new JSONArray(translationResult);
            JSONObject jObj2 = arr2.getJSONObject(0);
            translationResult = jObj2.getString("text");
        } catch (JSONException e) {
            translationResult = e.getLocalizedMessage();
        }
        return translationResult;
    }

    private class Chat extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            chat(translation);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            recyclerView.scrollToPosition(adapter.mensajes.size() - 1);
        }
    }

    private class TranslateToEng extends AsyncTask<Void, Void, Void> {

        private final Map<String, String> headers;
        private final Map<String, String> vars;
        String s = "Error";

        private TranslateToEng(String message) {
            headers = new LinkedHashMap<String, String>();
            headers.put("Content-type", "application/x-www-form-urlencoded");
            headers.put("User-Agent:", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

            vars = new HashMap<String, String>();
            vars.put("fromLang", "es");
            vars.put("text", message);
            vars.put("to", "en");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.performPostCall("https://www.bing.com/ttranslatev3", (HashMap) vars);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("xyz", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            translation = decomposeJson(s);
            doTheChat();
        }
    }

    private class TranslateToEs extends AsyncTask<Void, Void, Void> {

        private final Map<String, String> headers;
        private final Map<String, String> vars;
        String s = "Error";

        private TranslateToEs(String message) {
            headers = new LinkedHashMap<String, String>();
            headers.put("Content-type", "application/x-www-form-urlencoded");
            headers.put("User-Agent:", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.97 Safari/537.36");

            vars = new HashMap<String, String>();
            vars.put("fromLang", "en");
            vars.put("text", message);
            vars.put("to", "es");
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                s = Utils.performPostCall("https://www.bing.com/ttranslatev3", (HashMap) vars);
            } catch (Exception e) {
                e.printStackTrace();
                Log.v("xyz", "Error: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            translation = decomposeJson(s);

            showBotResponse();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CODIGO_STT:
                if (resultCode == RESULT_OK && data != null) {
                    result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.v("etinput", result.get(0));
                    etInput.setText(result.get(0));
                }
        }
    }

    private void initUID() {
        FirebaseDatabase database = FirebaseDatabase.getInstance(); // recoge instancia firebase
        String uidAuth = FirebaseAuth.getInstance().getCurrentUser().getUid(); // coge id usuario logueado en uidAuth
        DatabaseReference referenciaItem = database.getReference("user/"+uidAuth); // coge la ruta de referencia de user/la id del usuario

        /*referenciaItem.setValue("valor item");
        referenciaItem.child("uno").setValue("hola");

        String key = referenciaItem.push().getKey();
        referenciaItem.child(key).setValue("hola");*/

        referenciaItem.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.v(TAG, "data changed: " + dataSnapshot.toString());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.v(TAG, "error: " + databaseError.toException());
            }
        });

        ChatSentence item = new ChatSentence("hello", "hola", "bot", "1:54");
        Map<String, Object> map = new HashMap<>();
        String key = referenciaItem.child("20200113").push().getKey();
        map.put("20200113/" + key, item.toMap());
        referenciaItem.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.v(TAG, "task succesfull");
                } else {
                    Log.v(TAG, task.getException().toString());
                }
            }
        });
    }

}
