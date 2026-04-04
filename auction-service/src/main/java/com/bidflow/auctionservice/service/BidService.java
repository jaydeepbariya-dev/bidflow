package com.bidflow.auctionservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.bidflow.auctionservice.dto.BidRequestDTO;
import com.bidflow.auctionservice.dto.BidResponseDTO;
import com.bidflow.auctionservice.entity.Auction;
import com.bidflow.auctionservice.entity.Bid;
import com.bidflow.auctionservice.event.BidEvent;
import com.bidflow.auctionservice.repository.AuctionRepository;
import com.bidflow.auctionservice.repository.BidRepository;
import com.bidflow.auctionservice.util.AuctionStatus;
import com.bidflow.auctionservice.util.RedisUtil;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final AuctionRepository auctionRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public BidService(BidRepository bidRepository, AuctionRepository auctionRepository,
            RedisTemplate<String, Object> redisTemplate, KafkaTemplate<String, Object> kafkaTemplate) {
        this.bidRepository = bidRepository;
        this.auctionRepository = auctionRepository;
        this.redisTemplate = redisTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    public BidResponseDTO placeBid(String auctionId, BidRequestDTO dto) {

        Auction auction = auctionRepository.findById(UUID.fromString(auctionId))
                .orElseThrow(() -> new RuntimeException("AUCTION_NOT_FOUND"));

        if (auction.getAuctionStatus() != AuctionStatus.ACTIVE) {
            throw new RuntimeException("AUCTION_NOT_ACTIVE");
        }

        String key = RedisUtil.getHighestBidKey(auctionId);

        Object cachedValue = redisTemplate.opsForValue().get(key);

        Double highestBid;

        if (cachedValue != null) {
            highestBid = Double.valueOf(cachedValue.toString());
        } else {
            highestBid = bidRepository.findTopByAuctionIdOrderByAmountDesc(UUID.fromString(auctionId))
                    .map(Bid::getAmount).orElse(0.0);

            redisTemplate.opsForValue().set(key, highestBid.toString());
        }

        Bid bid = new Bid();
        bid.setAuctionId(UUID.fromString(auctionId));
        bid.setUserId(UUID.fromString(dto.getUserId()));
        bid.setAmount(dto.getAmount());

        Bid saved = bidRepository.save(bid);

        redisTemplate.opsForValue().set(key, dto.getAmount().toString());

        BidEvent bidEvent = new BidEvent();
        bidEvent.setAmount(saved.getAmount());
        bidEvent.setAuctionId(saved.getAuctionId().toString());
        bidEvent.setUserId(saved.getUserId().toString());
        kafkaTemplate.send("bid-events", bidEvent);

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