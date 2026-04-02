package com.bidflow.auctionservice.controller;


import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bidflow.auctionservice.dto.AuctionRequestDTO;
import com.bidflow.auctionservice.dto.AuctionResponseDTO;
import com.bidflow.auctionservice.dto.BidRequestDTO;
import com.bidflow.auctionservice.dto.BidResponseDTO;
import com.bidflow.auctionservice.service.AuctionService;
import com.bidflow.auctionservice.service.BidService;

@RestController
@RequestMapping("api/auctions")
public class AuctionController {

    private final AuctionService auctionService;
    private final BidService bidService;

    public AuctionController(AuctionService auctionService, BidService bidService) {
        this.auctionService = auctionService;
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<AuctionResponseDTO> createAuction(@RequestBody AuctionRequestDTO auctionRequestDTO) {
        AuctionResponseDTO auctionResponseDTO = auctionService.createAuction(auctionRequestDTO);
        return new ResponseEntity<AuctionResponseDTO>(auctionResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuctionResponseDTO> getAuctionById(@PathVariable String id) {

        AuctionResponseDTO response = auctionService.getAuctionById(id);
        return new ResponseEntity<AuctionResponseDTO>(response, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<AuctionResponseDTO>> getAllAuctions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<AuctionResponseDTO> response = auctionService.getAllAuctions(page, size, sortBy, direction);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/bids")
    public ResponseEntity<BidResponseDTO> placeBid(@PathVariable String id, @RequestBody BidRequestDTO bidRequestDTO) {
        BidResponseDTO bidResponseDTO = bidService.placeBid(id, bidRequestDTO);
        return new ResponseEntity<>(bidResponseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}/bids")
    public ResponseEntity<Page<BidResponseDTO>> getBidsByAuction(
            @PathVariable String id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "amount") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        Page<BidResponseDTO> response = bidService.getBidsByAuction(id, page, size, sortBy, direction);

        return ResponseEntity.ok(response);
    }
}
