package com.trainingmug.asset.exception;

/** Indicates that the requested asset or assignment does not exist. */
public class AssetNotFoundException extends RuntimeException {
    public AssetNotFoundException(String message) {
        super(message);
    }
}
