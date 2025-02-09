package com.blautech.pruebaTecnica.demo.audit.service;

import com.blautech.pruebaTecnica.demo.audit.model.AuditLog;
import com.blautech.pruebaTecnica.demo.audit.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String action, String performedBy, String details) {
        AuditLog log = new AuditLog(
                action,
                performedBy,
                LocalDateTime.now(),
                details
        );
        auditLogRepository.save(log);
    }
}
