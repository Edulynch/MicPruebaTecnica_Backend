package com.blautech.pruebaTecnica.demo.audit.repository;

import com.blautech.pruebaTecnica.demo.audit.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
