package com.bidflow.auctionservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bidflow.auctionservice.dto.BidRequestDTO;
import com.bidflow.auctionservice.dto.BidResponseDTO;
import com.bidflow.auctionservice.entity.Auction;
import com.bidflow.auctionservice.entity.Bid;
import com.bidflow.auctionservice.repository.AuctionRepository;
import com.bidflow.auctionservice.repository.BidRepository;
import com.bidflow.auctionservice.util.AuctionStatus;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
    }

    public BidResponseDTO placeBid(String auctionId, BidRequestDTO dto) {

        Auction auction = auctionRepository.findById(UUID.fromString(auctionId))
                .orElseThrow(() -> new RuntimeException("AUCTION_NOT_FOUND"));

        if (auction.getAuctionStatus() != AuctionStatus.ACTIVE) {
            throw new RuntimeException("AUCTION_NOT_ACTIVE");
        }

        Double highestBid = bidRepository
                .findTopByAuctionIdOrderByAmountDesc(UUID.fromString(auctionId))
                .map(Bid::getAmount)
                .orElse(0.0);

        if (dto.getAmount() <= highestBid) {
            throw new RuntimeException("BID_TOO_LOW");
        }

        Bid bid = new Bid();
        bid.setAuctionId(UUID.fromString(auctionId));
        bid.setUserId(UUID.fromString(dto.getUserId()));
        bid.setAmount(dto.getAmount());

        Bid saved = bidRepository.save(bid);

        BidResponseDTO res = new BidResponseDTO();
        res.setId(saved.getId().toString());
        res.setAuctionId(saved.getAuctionId().toString());
        res.setUserId(saved.getUserId().toString());
        res.setAmount(saved.getAmount());

        return res;
    }

    public Page<BidResponseDTO> getBidsByAuction(
            String auctionId,
            int page,
            int size,
            String sortBy,
            String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Bid> bids = bidRepository.findByAuctionId(
                UUID.fromString(auctionId),
                pageable);

        return bids.map(bid -> {
            BidResponseDTO dto = new BidResponseDTO();

            dto.setId(bid.getId().toString());
            dto.setAuctionId(bid.getAuctionId().toString());
            dto.setUserId(bid.getUserId().toString());
            dto.setAmount(bid.getAmount());

            return dto;
        });
    }
}