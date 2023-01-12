package com.bank.repository;

import com.bank.model.PccResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PccResponseRepository extends JpaRepository<PccResponse, UUID> {
}
