package com.onval.capstone.viewmodel;

import android.app.Application;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.tasks.Task;
import com.onval.capstone.repository.DataModel;
import com.onval.capstone.room.Category;
import com.onval.capstone.room.Record;
import com.onval.capstone.utility.Utility;

import java.util.ArrayList;
import java.util.List;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

public class CategoriesViewModel extends AndroidViewModel {
    private Application application;
    private DataModel model;

    private final Observer<List<Record>> deleteRecordObs =
            this::deleteRecordingsFiles;

    public CategoriesViewModel(Application application) {
        super(application);
        this.application = application;
        model = DataModel.getInstance(application);
    }

    public LiveData<List<Category>> getCategories() {
        return model.getCategories();
    }

    public LiveData<Boolean> isCategoryAutouploadOn(int categoryId) {
        return model.categoryAutoupload(categoryId);
    }

    public void insertCategory(Category category) {
        model.insertCategories(category);
    }

    public void updateCategories(Category... categories) {
        model.updateCategories(categories);
    }

    public LiveData<ArrayList<Integer>> getUploadingCategoryIds() {
        return model.getCategoriesIds();
    }

    public void uploadRecordings(int categoryId) {
        if (Utility.isSignedIn(application)) {
            GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(application);
            DriveResourceClient resourceClient = Drive.getDriveResourceClient(application, account);

            LiveData<List<Record>> recordingsLiveData = model.getRecordingsFromCategory(categoryId);

            model.startUploadService();

            recordingsLiveData.observeForever(new Observer<List<Record>>() {
                @Override
                public void onChanged(List<Record> recordings) {
                    for (Record rec : recordings) {
                        Query query = Utility.matchRecordingDriveQuery(rec.getId(), rec.getName());

                        Task<MetadataBuffer> queryTask = resourceClient.query(query);
                        queryTask.addOnSuccessListener(metadataBuffer -> {
                            if (metadataBuffer.getCount() == 0) {
                                model.getServiceLiveData().observeForever(
                                        (uploadService -> uploadService.uploadRecordingToDrive(rec, account))
                                );
                            }
                        }).addOnFailureListener(e -> Log.d("debug", "failed to query"));
                    }
                    recordingsLiveData.removeObserver(this);
                }
            });
        }
    }

    public void deleteCategories(Category... categories) {
        deleteRecFilesOfCategories(categories);
        model.deleteCategories(categories);
    }

    private void deleteRecordingsFiles(List<Record> recordings) {
        if (recordings != null && recordings.size() != 0) {
            for (Record rec : recordings)
                Utility.deleteRecordingFromFilesystem(application, rec);
        }
    }

    private void deleteRecFilesOfCategories(Category... categories) {
        LiveData<List<Record>> recLiveData;

        for(Category category : categories) {
            recLiveData = model.getRecordingsFromCategory(category.getId());
            recLiveData.observeForever(deleteRecordObs);
        }
    }

    public LiveData<Integer> getNumOfCategories() {
        return model.getNumOfCategories();
    }

    public LiveData<Integer> getRecNumberInCategory(int categoryId) {
        return model.getRecNumberInCategory(categoryId);
    }

    public LiveData<String> getCategoryColor(int categoryId) {
        return model.getCategoryColor(categoryId);
    }
}