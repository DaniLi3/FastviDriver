package com.ultrafastappconductor.ultrafastconductor.Providers;

import android.content.Context;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ultrafastappconductor.ultrafastconductor.Utils.CompressorBitmapImage;

import java.io.File;

public class ImageProvider {


    private StorageReference mstorage;
    public  ImageProvider(String ref)
    {
        mstorage=  FirebaseStorage.getInstance().getReference().child("image_users").child(ref);
    }

    public UploadTask saveImage(Context context, String idUser, File file){
        byte[] imagebyte= CompressorBitmapImage.getImage(context,file.getPath(),300,300);
        StorageReference storage= mstorage.child("image_users").child(idUser+".jpg");
        mstorage=storage;
        UploadTask uploadTask=storage.putBytes(imagebyte);
        return uploadTask;
    }
    public StorageReference getstorage()
    {
        return mstorage;
    }
}
