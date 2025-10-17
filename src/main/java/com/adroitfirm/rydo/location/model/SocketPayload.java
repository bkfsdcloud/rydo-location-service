package com.adroitfirm.rydo.location.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SocketPayload implements Serializable {
	private static final long serialVersionUID = 6152086222920461322L;
	private String action;
	private String identifier;
	private Double latitude;
    private Double longitude;
    private String message;
}
