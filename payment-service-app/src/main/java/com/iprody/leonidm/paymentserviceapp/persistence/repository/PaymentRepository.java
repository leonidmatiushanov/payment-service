package com.iprody.leonidm.paymentserviceapp.persistence.repository;

import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    List<Payment> findByStatus(PaymentStatus status);
}
