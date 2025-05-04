package com.transactio.repository;

import com.transactio.model.Payment;
import com.transactio.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findBySenderId(String senderId);

    List<Payment> findByReceiverId(String receiverId);

    List<Payment> findBySenderIdOrReceiverId(String senderId, String receiverId);
}