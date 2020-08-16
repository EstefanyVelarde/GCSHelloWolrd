package com.holamundo.control;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.holamundo.model.Object;
import com.google.api.gax.paging.Page;

public class CSHandler {
	Storage storage;
	
	public CSHandler() {
		credential();
	}
	
	public void credential() {
		String credentialsPath = getInitParameter("credencial");
		String projectId = getInitParameter("projectId");
		
		FileInputStream credentialsStream;

		try {
			credentialsStream = new FileInputStream(credentialsPath);
		} catch (IOException e1) {
			return;
		}
		
		Credentials credentials;
	
		try {
			credentials = GoogleCredentials.fromStream(credentialsStream);
			storage = StorageOptions.newBuilder().setCredentials(credentials).setProjectId(projectId).build()
					.getService();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	public String uploadImage(String bucket, String fileName, String fileType, InputStream content)
			throws FileNotFoundException, IOException {
		BlobId blobId = BlobId.of(bucket, fileName);
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(fileType).build();
		
		@SuppressWarnings("deprecation")
		Blob blob = storage.create(blobInfo, content);

		return "https://storage.googleapis.com/"+bucket+"/"+fileName;

	}

	public void deleteImage(String bucket, String fileName) {
		BlobId blobId = BlobId.of(bucket, fileName);

		storage.delete(blobId);
	}
	
	
	public String getInitParameter(String parametro) {
		String initParameter = FacesContext.getCurrentInstance().getExternalContext().getInitParameter(parametro);
		
		return initParameter;
	}
	
	public ArrayList<Object> listObjects(String bucketName) {
	    Bucket bucket = storage.get(bucketName);
	    Page<Blob> blobs = bucket.list();
	    
	    ArrayList<Object> obj = new ArrayList<Object>();
	    
	    for (Blob blob : blobs.iterateAll()) 
	      obj.add(new Object(blob.getName(), "https://storage.googleapis.com/"+bucketName+"/"+blob.getName(), blob.getContentType()));
	    
	    return obj;
	  }
}
