package com.multipay.android.utils;

import java.util.ArrayList;
import java.util.List;

public class ItemCategories {
	
	public class ItemCategory {
		private String id;
		private String description;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
	}

	private List<ItemCategory> itemCategories;

	private static ItemCategories instance = null;
	
	public static ItemCategories getInstance() {
		if (instance == null) {
			instance = new ItemCategories();
		}
		return instance;
	}

	protected ItemCategories() {
		itemCategories = new ArrayList<ItemCategory>();
	}

	public void fillItems(List<ItemCategory> itemCategories) {
		this.itemCategories = itemCategories;
	}
	
	public List<ItemCategory> getItems() {
		return this.itemCategories;
	}
}
