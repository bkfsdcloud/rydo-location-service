package com.adroitfirm.rydo.location.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class SocketPayload implements Serializable {
	private static final long serialVersionUID = 6152086222920461322L;
	private String action;
	private String identifier;
	private Coordinate coordinate;
    private String message;
}
