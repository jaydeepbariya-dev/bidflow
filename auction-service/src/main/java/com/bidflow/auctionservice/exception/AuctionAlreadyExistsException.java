package com.bidflow.auctionservice.exception;

public class AuctionAlreadyExistsException extends RuntimeException {

    public AuctionAlreadyExistsException(String message) {
        super(message);
    }

}
