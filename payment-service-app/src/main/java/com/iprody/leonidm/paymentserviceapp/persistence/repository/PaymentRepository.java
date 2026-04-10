package com.iprody.leonidm.paymentserviceapp.persistence.repository;

import com.iprody.leonidm.paymentserviceapp.persistence.entity.Payment;
import com.iprody.leonidm.paymentserviceapp.persistence.entity.PaymentStatus;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID>, JpaSpecificationExecutor<Payment> {

    List<Payment> findByStatus(PaymentStatus status);

    @Modifying
    @Query("update Payment p SET p.note = :note WHERE p.guid = :guid")
    void updateNotePayment(@RequestParam UUID guid, @RequestParam("note") String note);

    @Modifying
    @Query("update Payment p SET p.transactionRefId = :transactionRefId WHERE p.guid = :guid")
    @Transactional
    void updateTransactionRefIdByGuid(@RequestParam UUID transactionRefId, @RequestParam UUID guid);
}
