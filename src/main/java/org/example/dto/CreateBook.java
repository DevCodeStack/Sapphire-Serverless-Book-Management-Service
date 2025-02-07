package org.example.dto;

import lombok.Data;

@Data
public class CreateBook {
	
	private String title;
    private String author;
    private double price;
}
