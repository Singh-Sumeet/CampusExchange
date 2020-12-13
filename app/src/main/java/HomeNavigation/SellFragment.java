package HomeNavigation;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.campusexchange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import Useful.User;
import io.grpc.Compressor;

public class SellFragment extends Fragment {
    FirebaseFirestore db;
    CollectionReference items;
    StorageReference mStorageRef;

    ImageView itemImage;
    ImageButton itemImageSelectButton;
    EditText itemName;
    EditText itemDescription;
    Spinner itemCategory;
    EditText itemPrice;
    Button itemOnSale;

    Uri imageInStorage = null, imageInCloud;
    byte[] imageBytes = null;
    String category = null;

    public SellFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sell, container, false);

        db = FirebaseFirestore.getInstance();
        items = db.collection("Items");
        mStorageRef = FirebaseStorage.getInstance().getReference();

        itemImage = view.findViewById(R.id.sellItemImage);
        itemImageSelectButton = view.findViewById(R.id.sellItemImageSelect);
        itemName = view.findViewById(R.id.sellItemName);
        itemDescription = view.findViewById(R.id.sellItemDescription);
        itemCategory = view.findViewById(R.id.sellItemCategory);
        itemPrice = view.findViewById(R.id.sellItemPrice);
        itemOnSale = view.findViewById(R.id.sellItemSubmit);

        itemImageSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 1);
            }
        });

        itemCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        itemOnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = itemName.getText().toString();
                final String desc = itemDescription.getText().toString();
                final String price = itemPrice.getText().toString();

                if(
                        imageInStorage == null
                        || name.isEmpty()
                        || desc.isEmpty()
                        || price.isEmpty()
                        || category == null
                ) {
                    Toast.makeText(getContext(), "Please select all the required items", Toast.LENGTH_LONG).show();
                    return;
                }

                mStorageRef.child(Calendar.getInstance().getTime().toString()).putFile(imageInStorage)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getMetadata().getReference().getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                imageInCloud = uri;
                                                Map<String, Object> item = new HashMap<>();
                                                item.put("Category", category);
                                                item.put("Date", Calendar.getInstance().getTime());
                                                item.put("Description", desc);
                                                item.put("Image", imageInCloud.toString());
                                                item.put("Name", name);
                                                item.put("Price", price);
                                                Map<String, Object> user = new HashMap<>();
                                                user.put("Name", User.getName());
                                                user.put("UID", User.getUID());
                                                item.put("User", user);
                                                items.add(item)
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                            @Override
                                                            public void onSuccess(DocumentReference documentReference) {
                                                                Toast.makeText(getContext(), "Item has been added", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                                return;
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                                return;
                            }
                        });
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {;
        imageInStorage = data.getData();
        itemImage.setImageURI(imageInStorage);
        //imageBytes = compressImage();
    }

    private byte[] compressImage() {
        if(imageInStorage == null) return null;
        Bitmap bitmap = null;
        File file = new File(imageInStorage.getPath());
        try {
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Exception e) {
            Log.d("Bitmap", e.getMessage());
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        int new_height = (int) (bitmap.getHeight() * (512 / bitmap.getWidth()));
        Bitmap compressed = Bitmap.createScaledBitmap(bitmap, 512, new_height, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressed.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }
}