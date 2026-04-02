package com.bidflow.auctionservice.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bidflow.auctionservice.entity.Auction;


@Repository
public interface AuctionRepository extends JpaRepository<Auction, UUID> {
    boolean existsByTitle(String title);
}