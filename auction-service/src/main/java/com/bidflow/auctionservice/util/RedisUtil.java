package com.bidflow.auctionservice.util;

public class RedisUtil {
    public static String getHighestBidKey(String auctionId){
        return "auction:" + auctionId + ":highestBid";
    }
}
