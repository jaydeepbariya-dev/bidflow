package com.bidflow.auctionservice.service;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bidflow.auctionservice.dto.AuctionRequestDTO;
import com.bidflow.auctionservice.dto.AuctionResponseDTO;
import com.bidflow.auctionservice.entity.Auction;
import com.bidflow.auctionservice.exception.AuctionAlreadyExistsException;
import com.bidflow.auctionservice.mapper.AuctionMapper;
import com.bidflow.auctionservice.repository.AuctionRepository;
import com.bidflow.auctionservice.util.AuctionStatus;

@Service
public class AuctionService {

    private final AuctionRepository auctionRepository;

    public AuctionService(AuctionRepository auctionRepository) {
        this.auctionRepository = auctionRepository;
    }

    public AuctionResponseDTO createAuction(AuctionRequestDTO auctionRequestDTO) {

        if (auctionRepository.existsByTitle(auctionRequestDTO.getTitle())) {
            throw new AuctionAlreadyExistsException(
                    "AUCTION_ALREADY_EXISTS_WITH_THIS_TITLE: " + auctionRequestDTO.getTitle());
        }

        Auction auction = new Auction();
        auction.setTitle(auctionRequestDTO.getTitle());
        auction.setDescription(auctionRequestDTO.getDescription());
        auction.setStartDate(auctionRequestDTO.getStartDate());
        auction.setEndDate(auctionRequestDTO.getEndDate());
        auction.setCreatedByUserId(UUID.fromString(auctionRequestDTO.getCreatedByUserId()));
        auction.setAuctionStatus(AuctionStatus.valueOf(auctionRequestDTO.getAuctionStatus()));

        Auction auctionSaved = auctionRepository.save(auction);

        return AuctionMapper.mapToDTO(auctionSaved);
    }

    public AuctionResponseDTO getAuctionById(String id) {

        Auction auction = auctionRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new RuntimeException("AUCTION_NOT_FOUND"));

        return AuctionMapper.mapToDTO(auction);
    }

    public Page<AuctionResponseDTO> getAllAuctions(int page, int size, String sortBy, String direction) {

        Sort sort = direction.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return auctionRepository.findAll(pageable)
                .map(AuctionMapper::mapToDTO);
    }

}