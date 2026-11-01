package com.trainingmug.asset.exception;

/** Indicates that the requested lifecycle action is not allowed for an asset. */
public class AssetConflictException extends RuntimeException {
    public AssetConflictException(String message) {
        super(message);
    }
}
