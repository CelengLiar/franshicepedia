package com.model.respond;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Response<T> {
	
	@JsonProperty(value = "status")
	private HttpStatus status;
	@JsonProperty(value = "respond")    
	private T respond;
}