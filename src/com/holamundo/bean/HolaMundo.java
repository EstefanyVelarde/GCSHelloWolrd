package com.holamundo.bean;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

import com.holamundo.control.CSHandler;
import com.holamundo.model.Object;

@Named
@SessionScoped
public class HolaMundo implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private CSHandler handler;
	
	private ArrayList<Object> obj;
	
	private String bucket, id, type, imageUrl;

	private Part content;
	
	private int ind;
	
	public HolaMundo() {
		handler = new CSHandler();
		
		bucket = "prueba-nscs";
		obj = handler.listObjects(bucket);
		
		if(!obj.isEmpty()) 
			setData();
	}
	
	public void next() {
		if(ind < obj.size() -1) {
			ind++;
			setData();
		}
	}
	
	public void prev() {
		if(ind > 0) {
			ind--;
			setData();
		}
	}
	
	public void upload() {
		if (content == null) {
			return;
		}
		
		String fileName = id;
		String fileType = content.getContentType();
		
		try {
			InputStream inputStream = content.getInputStream();
			String url = handler.uploadImage(bucket, fileName, fileType, inputStream);
			obj.add(new Object(fileName, url, fileType));
			ind = obj.size() - 1;
			setData();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void delete() {
		if(!obj.isEmpty()) {
			handler.deleteImage(bucket, id);
			obj.remove(ind);
			ind = obj.size() - 1;
			setData();
		}
	}
	
	public void setData() {
		setId(obj.get(ind).getName());
		setType(obj.get(ind).getType());
		setImageUrl(obj.get(ind).getUrl());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Part getContent() {
		return content;
	}

	public void setContent(Part content) {
		this.content = content;
		
		if (content != null) 
			id = Long.toString(((long) hashCode()) + System.currentTimeMillis());
	}
	

}
