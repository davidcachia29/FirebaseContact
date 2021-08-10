package com.example.davidCachiaTask3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class getContacts extends AppCompatActivity {

    Button mainBTN;
    Button logContacts;
    Button showContacts;

    ListView contactViewList;

    ArrayList<String> arrayList;
    ArrayList<String> displayCONTList;
    ArrayAdapter<String> arrayAdapter;

    DatabaseReference dbREF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        mainBTN =findViewById(R.id.backTOMAIN);
        logContacts =findViewById(R.id.backupCONT);
        showContacts =findViewById(R.id.downldCONT);
        contactViewList =findViewById(R.id.contactDISP);

        displayCONTList=new ArrayList<>();
        arrayList=new ArrayList<>();
        arrayAdapter=new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1,arrayList);

        FirebaseDatabase db=FirebaseDatabase.getInstance("https://androidtask3-default-rtdb.firebaseio.com/");
        dbREF=db.getReference();







        showContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadFromFirebase();
            }
        });

        logContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadToFirebase();
                //dbREF.setValue("Testing connection");
            }
        });
    }

    private void getContacts()
    {
        Cursor cursor=getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        ,null,null,null,null);

        while (cursor.moveToNext())
        {
            //the cursors store a string each, one for the contact name and another for the contact number
            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String number=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            arrayList.add(name +" \n "+number );
            //setting adapter for list
            //contactDISP.setAdapter(arrayAdapter);
            //updating adapter with data change
            //arrayAdapter.notifyDataSetChanged();

            dbREF.setValue(arrayList).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    //all data in the arraylist is then uploaded onto firebase, once completed the below toast is shown
                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"contacts uploaded to firebase",Toast.LENGTH_LONG).show();
                        arrayList.clear();
                    }

                }
            });


        }
    }


    public void downloadFromFirebase()
    {
        dbREF.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists())
                {
                    displayCONTList.clear();//if data exists already, it is cleared out
                    for(DataSnapshot dataSnap:snapshot.getChildren())
                    {
                        String contactDetail=dataSnap.getValue(String.class);
                        displayCONTList.add(contactDetail);
                    }

                    StringBuilder sb=new StringBuilder();

                    for(int i=0; i<displayCONTList.size();i++)
                    {
                        sb.append(displayCONTList.get(i)+",");//name and number separated by comma once data is downloaded
                    }
                    //data obtained is added into the arraylist again and then shown in the listview
                    arrayList.add(displayCONTList.toString());
                    contactViewList.setAdapter(arrayAdapter);
                    arrayAdapter.notifyDataSetChanged();
                    Toast.makeText(getApplicationContext(),"contacts obtained",Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //arrayList.clear();
                Toast.makeText(getApplicationContext(),"download failed",Toast.LENGTH_SHORT).show();

            }
        });
    }

    public void uploadToFirebase()
    {
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED)
        {
            //if permission has not been granted yet, a prompt will appear asking for permission to access phonebook
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},1);
        }
        else//if permission is already accepted, then the contacts will be uploaded to firebase once the upload button is pressed
        {
            getContacts();//if permission is accepted, the contacts will appear once the download button is pressed
            //if internet is not available and permission is accepted, nothing happens
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1)
        {
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
            {
                //getContacts();
            }
        }
    }
}