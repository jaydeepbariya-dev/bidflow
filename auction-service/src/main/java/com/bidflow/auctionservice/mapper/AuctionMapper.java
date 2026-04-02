package com.bidflow.auctionservice.mapper;

import com.bidflow.auctionservice.dto.AuctionResponseDTO;
import com.bidflow.auctionservice.entity.Auction;

public class AuctionMapper {

    public static AuctionResponseDTO mapToDTO(Auction auction) {

        AuctionResponseDTO dto = new AuctionResponseDTO();

        dto.setId(auction.getId().toString());
        dto.setTitle(auction.getTitle());
        dto.setDescription(auction.getDescription());
        dto.setStartDate(auction.getStartDate().toString());
        dto.setEndDate(auction.getEndDate().toString());
        dto.setCreatedByUserId(auction.getCreatedByUserId().toString());
        dto.setAuctionStatus(auction.getAuctionStatus().toString());

        return dto;
    }
}
