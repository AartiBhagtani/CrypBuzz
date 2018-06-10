package com.example.navin.cryptbuzz;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.Locale;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIConfiguration;

import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class Chatbot extends AppCompatActivity implements AIListener {
    //
    EditText editText;
    ImageView addBtn;
    private AIService aiService;
    MessageListAdapter adapter;
    Boolean flagFab = true;
    ArrayList<FriendlyMessage> arrayList;
    TextToSpeech textToSpeech;
    String username,lang;
    ImageButton mPhotoPickerButton;
    String message;
    public static final int RC_SIGN_IN = 1;
    public static final int RC_PHOTO_PICKER = 2;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mChatPhotosReference;
    ListView messageListView;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference fireRef;
    private FirebaseAuth firebaseAuth;
    public DatabaseReference chatReference;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.RECORD_AUDIO);
        mFirebaseStorage = FirebaseStorage.getInstance();
        mChatPhotosReference = mFirebaseStorage.getReference().child("chat_photos");

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 105);


        }
        setContentView(R.layout.activity_chatbot);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        SharedPreferences sharedPreferences = getSharedPreferences("Mypref", MODE_PRIVATE);
        username = sharedPreferences.getString("username", "");
        lang = sharedPreferences.getString("lang","");

        addBtn = findViewById(R.id.fab_img);


        messageListView = findViewById(R.id.messageListView);

        editText = (EditText) findViewById(R.id.editText1);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mPhotoPickerButton = findViewById(R.id.photoPickerButton);
        arrayList = new ArrayList<>();
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i == TextToSpeech.SUCCESS) {
                    if(!lang.equals("en"))
                        textToSpeech.setLanguage(new Locale("hin"));
                    else
                        textToSpeech.setLanguage(Locale.ENGLISH);
                }
            }
        });

        adapter = new  MessageListAdapter(this, R.layout.card_view, arrayList);
        messageListView.setAdapter(adapter);
        final AIConfiguration config = new AIConfiguration("5c4ef3cbd87243a581cec08679d4007e", AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        aiService = AIService.getService(this, config);
        aiService.setListener(this);


        final AIDataService aiDataService = new AIDataService(config);

        final AIRequest aiRequest = new AIRequest();
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                message = editText.getText().toString().trim();

                if (!message.equals("")) {
                    //user is typing
                    FriendlyMessage cmessage;
                    if(!lang.equals("en")) {
                        // if not english translate
                        cmessage = new FriendlyMessage(message, username,null);
                        message = Translate.translate("hi", "en", message, getApplicationContext());
                        Toast.makeText(ChatbotActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        cmessage = new FriendlyMessage(message,username,null);
                    }
                    aiRequest.setQuery(message);

                    adapter.add(cmessage);
                    // adapter.notifyDataSetChanged();

                    Log.d("Message", message);
                    new AsyncTask<AIRequest, Void, AIResponse>() {

                        @Override
                        protected AIResponse doInBackground(AIRequest... aiRequests) {
                            final AIRequest request = aiRequests[0];
                            try {
                                final AIResponse response = aiDataService.request(aiRequest);
                                return response;
                            } catch (AIServiceException e) {
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(AIResponse response) {
                            if (response != null) {

                                Result result = response.getResult();
                                String reply = result.getFulfillment().getSpeech();
                                if (reply.equals("") || reply.isEmpty()) {
                                    Toast.makeText(ChatbotActivity.this, "No Response", Toast.LENGTH_SHORT).show();
                                    updateAndTransfer();
                                    // checkQnA(message);
                                } else {
                                    if(!lang.equals("en"))
                                    {
                                        reply = Translate.translate("en","hi",reply,getApplicationContext());
                                    }
                                    FriendlyMessage message = new FriendlyMessage(reply, "bot",null);
                                    adapter.add(message);
                                    //adapter.notifyDataSetChanged();
                                    textToSpeech.speak(message.getText(), TextToSpeech.QUEUE_FLUSH, null);
                                    // recyclerView.scrollToPosition(arrayList.size() - 1);
                                }
                            }
                        }
                    }.execute(aiRequest);


                } else {
                    //ai service nhi google se lenge
                    //aiService.startListening();
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, new Locale("hi", "IN"));

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, 11);
                    } else {
                        Toast.makeText(ChatbotActivity.this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
                    }


                }

                editText.setText("");


            }
        });


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ImageView fab_img = findViewById(R.id.fab_img);
                Bitmap img = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_name_send);
                Bitmap img1 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_mic_white_24dp);


                if (s.toString().trim().length() != 0 && flagFab) {

                    flagFab = false;
                    fab_img.setImageBitmap(img);
                } else if (s.toString().trim().length() == 0) {

                    flagFab = true;
                    fab_img.setImageBitmap(img1);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


    }

    @Override
    public void onResult(AIResponse response) {

        Result result = response.getResult();

        String message = result.getResolvedQuery();

        FriendlyMessage chatMessage0 = new FriendlyMessage(message, username,null);
        adapter.add(chatMessage0);
        // ref.child("chat").push().setValue(chatMessage0);
        //adapter.notifyDataSetChanged();

        String reply = result.getFulfillment().getSpeech();
        if (reply.equals("") || reply.isEmpty()) {
            Toast.makeText(ChatbotActivity.this, "No Response", Toast.LENGTH_SHORT).show();
//            checkQnA(message);
        } else {
            FriendlyMessage chatMessage = new FriendlyMessage(reply, "bot",null);
            textToSpeech.speak(chatMessage.getText(), TextToSpeech.QUEUE_FLUSH, null);
            adapter.add(chatMessage);
        }
    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 11:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    Log.d("sst", result.get(0));
                    editText.setText(result.get(0));
                }
                break;
        }

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Signed in!", Toast.LENGTH_SHORT).show();

            } else if (resultCode == RESULT_CANCELED) {
                // Sign in was canceled by the user, finish the activity
                Toast.makeText(this, "Sign in canceled", Toast.LENGTH_SHORT).show();
                finish();
            }
        }  else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            Uri imageUri = data.getData();
            StorageReference photoReference = mChatPhotosReference.child(imageUri.getLastPathSegment());

            //upload file to firebase
            photoReference.putFile(imageUri).addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    Toast.makeText(ChatbotActivity.this, downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                    FriendlyMessage message = new FriendlyMessage(null, null, downloadUrl.toString());
                    adapter.add(message);
                    updateAndTransfer();
                    // mMessagesDatabaseReference.push().setValue(message);
                }
            });
        }
    }


    public void updateAndTransfer()
    {   FriendlyMessage fm;
        chatReference=FirebaseDatabase.getInstance().getReference().child("unresolved").child(FirebaseAuth.getInstance().getUid());
        for(int i =0; i<adapter.getCount();i++) {
            fm = arrayList.get(i);
            chatReference.push().setValue(fm);
            Toast.makeText(this, fm.getText(), Toast.LENGTH_SHORT).show();
        }
    }



}

