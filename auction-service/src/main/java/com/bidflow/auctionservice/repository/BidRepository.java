package com.bidflow.auctionservice.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.bidflow.auctionservice.entity.Bid;

public interface BidRepository extends JpaRepository<Bid, UUID> {
    Optional<Bid> findTopByAuctionIdOrderByAmountDesc(UUID auctionId);
    Page<Bid> findByAuctionId(UUID auctionId, Pageable pageable);
}
